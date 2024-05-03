package net.cassite.hottapcassistant.tool;

import io.vertx.core.impl.ConcurrentHashSet;
import io.vproxy.base.util.LogType;
import io.vproxy.base.util.Logger;
import io.vproxy.base.util.Utils;
import io.vproxy.base.util.callback.Callback;
import io.vproxy.base.util.promise.Promise;
import io.vproxy.commons.util.IOUtils;
import io.vproxy.vfx.ui.loading.LoadingFailure;
import io.vproxy.vfx.ui.loading.LoadingItem;
import io.vproxy.vfx.ui.loading.LoadingStage;
import net.cassite.hottapcassistant.i18n.I18n;
import vjson.JSON;
import vjson.deserializer.rule.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class PatchInfoBuilder {
    public static final String CONFIG_SUFFIX = ".config.json.txt";

    public boolean enabled = false;
    public boolean isCNCompatible = true;
    public boolean isGlobalCompatible = true;
    public String description;
    public List<String> loadAfter;
    public List<String> dependsOn;

    public static final Rule<PatchInfoBuilder> rule = new ObjectRule<>(PatchInfoBuilder::new)
        .put("enabled", (it, o) -> it.enabled = o, BoolRule.get())
        .put("isCNCompatible", (it, o) -> it.isCNCompatible = o, BoolRule.get())
        .put("isGlobalCompatible", (it, o) -> it.isGlobalCompatible = o, BoolRule.get())
        .put("description", (it, o) -> it.description = o, StringRule.get())
        .put("loadAfter", (it, o) -> it.loadAfter = o, new ArrayRule<List<String>, String>(
            ArrayList::new, List::add, StringRule.get()
        ))
        .put("dependsOn", (it, o) -> it.dependsOn = o, new ArrayRule<List<String>, String>(
            ArrayList::new, List::add, StringRule.get()
        ));

    public static File getPatchManagerDir() throws Exception {
        var patchDirStr = Utils.homefile("hotta-patch");
        var patchDir = new File(patchDirStr);
        if (patchDir.exists()) {
            if (!patchDir.isDirectory()) {
                throw new Exception(patchDirStr + " is not a directory");
            }
        } else {
            var ok = patchDir.mkdirs();
            if (!ok) {
                throw new Exception("failed to create patch directory: " + patchDirStr);
            }
        }
        return patchDir;
    }

    public static Promise<Void> applyCNPatch(Path basedir) {
        return applyPatch(basedir, i -> i.isCNCompatible);
    }

    public static Promise<Void> applyGlobalPatch(Path basedir) {
        return applyPatch(basedir, i -> i.isGlobalCompatible);
    }

    private static class Item {
        String indexedName;
        final String name;
        final List<Item> parents = new ArrayList<>();
        boolean processing;
        boolean added;

        final Set<Parent> parentNames = new HashSet<>();

        record Parent(String name, boolean required) {
        }

        File pakFile;
        File sigFile;

        Item(String name, List<String> loadAfter, List<String> dependsOn) {
            this.name = name;
            if (loadAfter != null) {
                for (var n : loadAfter) {
                    parentNames.add(new Parent(n, false));
                }
            }
            if (dependsOn != null) {
                for (var n : dependsOn) {
                    parentNames.add(new Parent(n, true));
                }
            }
        }

        void addInto(List<Item> items) {
            if (added) {
                return;
            }
            if (processing) {
                Logger.error(LogType.INVALID_EXTERNAL_DATA, "circular dependency detected for " + name);
                return;
            }
            processing = true;
            for (var item : parents) {
                item.addInto(items);
            }
            added = true;
            items.add(this);
        }
    }

    private static final Set<InputStream> holdPatchFiles = new ConcurrentHashSet<>();

    private static Promise<Void> applyPatch(Path basedir, Predicate<PatchInfoBuilder> checkApply) {
        clearLastHoldPatchFiles();

        var promise = Promise.<Void>todo();

        File patchDir;
        try {
            patchDir = getPatchManagerDir();
        } catch (Exception e) {
            promise._2.failed(e);
            return promise._1;
        }
        var patches = patchDir.listFiles(file -> file.isFile() && file.getName().endsWith(".pak"));
        if (patches == null || patches.length == 0) {
            Logger.alert("no .pak file exist");
            patches = new File[0];
        }
        var items = new ArrayList<Item>();
        for (var file : patches) {
            var name = file.getName();
            name = name.substring(0, name.length() - ".pak".length());
            var configFilePath = Path.of(patchDir.getAbsolutePath(), name + CONFIG_SUFFIX);
            PatchInfoBuilder config;
            try {
                var content = Files.readString(configFilePath);
                config = JSON.deserialize(content, rule);
            } catch (Throwable t) {
                Logger.error(LogType.ALERT, "failed to retrieve config file from " + configFilePath, t);
                continue;
            }
            if (!config.enabled) {
                Logger.alert(name + " is not enabled");
                continue;
            }
            if (!checkApply.test(config)) {
                Logger.alert(name + " is not compatible with current game");
                continue;
            }
            var item = new Item(name, config.loadAfter, config.dependsOn);
            item.pakFile = file;
            var sigFile = Path.of(patchDir.getAbsolutePath(), name + ".sig").toFile();
            if (sigFile.exists()) {
                item.sigFile = sigFile;
            }
            items.add(item);
        }

        initItems(items);
        if (items.isEmpty()) {
            Logger.alert("no matching files (not enabled or game version mismatch)");
        }

        var pakDirPath = Path.of(basedir.toString(), "HottaPCAssistantPatchPaks");

        var loadingStage = new LoadingStage(I18n.get().applyPatchLoadingStageTitle());
        var progressItems = new ArrayList<LoadingItem>();
        {
            progressItems.add(new LoadingItem(1, I18n.get().applyPatchLoadingPreparePatchDirectory(), () -> ensureDir(pakDirPath)));
        }
        var sigFile = new File[]{null};
        if (items.stream().anyMatch(i -> i.sigFile == null)) {
            progressItems.add(new LoadingItem(1, I18n.get().applyPatchLoadingPrepareSigFile(), () -> {
                sigFile[0] = getAnySigFile(basedir);
                return sigFile[0] != null;
            }));
        }
        for (var copy : items) {
            progressItems.add(new LoadingItem(1, copy.name + ".pak", () ->
                copyFile(copy.pakFile, Path.of(pakDirPath.toString(), copy.indexedName + ".pak"))
            ));
            if (copy.sigFile != null) {
                progressItems.add(new LoadingItem(1, copy.name + ".sig", () ->
                    copyFile(copy.sigFile, Path.of(pakDirPath.toString(), copy.indexedName + ".sig"))
                ));
            }
        }
        for (var sig : items) {
            if (sig.sigFile != null) {
                continue;
            }
            progressItems.add(new LoadingItem(1, "copy sig: " + sig.name, () ->
                copyFile(sigFile[0], Path.of(pakDirPath.toString(), sig.indexedName + ".sig"))
            ));
        }
        loadingStage.setItems(progressItems);
        if (progressItems.size() < 25) {
            loadingStage.setInterval(50);
        }
        loadingStage.load(new Callback<>() {
            @Override
            protected void onSucceeded(Void unused) {
                promise._2.succeeded();
            }

            @Override
            protected void onFailed(LoadingFailure loadingFailure) {
                promise._2.failed(loadingFailure);
            }
        });
        return promise._1;
    }

    private static void clearLastHoldPatchFiles() {
        for (var f : holdPatchFiles) {
            try {
                f.close();
            } catch (IOException ignore) {
                // we can do nothing if closing failed
            }
        }
    }

    private static void initItems(List<Item> items) {
        // init nodes
        for (var item : items) {
            for (var p : item.parentNames) {
                var opt = items.stream().filter(i -> i.name.equals(p.name)).findAny();
                if (opt.isPresent()) {
                    item.parents.add(opt.get());
                } else if (p.required) {
                    Logger.error(LogType.INVALID_EXTERNAL_DATA, item.name + " depends on " + p + ", but " + p + " is not found");
                }
            }
        }
        // load
        var sorted = new ArrayList<Item>();
        for (var item : items) {
            item.addInto(sorted);
        }
        var i = 0;
        for (var item : sorted) {
            item.indexedName = String.format("p_%06d_P", ++i);
        }
        var log = new StringBuilder();
        log.append("patch files mapping:\n");
        for (var item : sorted) {
            log.append("  ").append(item.indexedName).append(" -> ").append(item.name).append("\n");
        }
        Logger.alert(log.toString());
    }

    private static File getAnySigFile(Path basedir) {
        var ls = basedir.toFile().listFiles(f -> f.isFile() && f.getName().endsWith(".sig"));
        if (ls != null && ls.length > 0) {
            return ls[0];
        }
        Logger.error(LogType.ALERT, "unable to find any .sig file in " + basedir);
        return null;
    }

    private static boolean ensureDir(Path pakDirPath) {
        var f = pakDirPath.toFile();
        if (f.exists()) {
            IOUtils.deleteDirectory(f);
        }
        var ok = f.mkdirs();
        if (!ok) {
            Logger.error(LogType.FILE_ERROR, "failed to mkdirs: " + f);
        }
        return ok;
    }

    private static boolean copyFile(File src, Path dest) {
        try {
            Files.copy(src.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
            var holdFile = new FileInputStream(dest.toFile());
            //noinspection ResultOfMethodCallIgnored
            holdFile.read(); // just read anything to ensure the file is opened
            holdPatchFiles.add(holdFile);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
