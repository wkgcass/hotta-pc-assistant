package net.cassite.hottapcassistant.tool;

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
import vjson.deserializer.rule.BoolRule;
import vjson.deserializer.rule.ObjectRule;
import vjson.deserializer.rule.Rule;
import vjson.deserializer.rule.StringRule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.function.Predicate;

public class PatchInfoBuilder {
    public static final String CONFIG_SUFFIX = ".config.json.txt";
    public boolean enabled;
    public boolean isCNCompatible;
    public boolean isGlobalCompatible;
    public String description;
    public static final Rule<PatchInfoBuilder> rule = new ObjectRule<>(PatchInfoBuilder::new)
        .put("enabled", (it, o) -> it.enabled = o, BoolRule.get())
        .put("isCNCompatible", (it, o) -> it.isCNCompatible = o, BoolRule.get())
        .put("isGlobalCompatible", (it, o) -> it.isGlobalCompatible = o, BoolRule.get())
        .put("description", (it, o) -> it.description = o, StringRule.get());

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

    private static Promise<Void> applyPatch(Path basedir, Predicate<PatchInfoBuilder> checkApply) {
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
        var filesToCopy = new ArrayList<File>();
        var requireSigNames = new ArrayList<String>();
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
                continue;
            }
            if (!checkApply.test(config)) {
                continue;
            }
            filesToCopy.add(file);
            var sigFile = Path.of(patchDir.getAbsolutePath(), name + ".sig").toFile();
            if (sigFile.exists()) {
                filesToCopy.add(sigFile);
            } else {
                requireSigNames.add(name + ".sig");
            }
        }

        if (filesToCopy.isEmpty() && requireSigNames.isEmpty()) {
            Logger.alert("no matching files (not enabled or game version mismatch)");
        }

        var pakDirPath = Path.of(basedir.toString(), "HottaPCAssistantPatchPaks");

        var loadingStage = new LoadingStage(I18n.get().applyPatchLoadingStageTitle());
        loadingStage.setInterval(50);
        var progressItems = new ArrayList<LoadingItem>();
        {
            progressItems.add(new LoadingItem(1, I18n.get().applyPatchLoadingPreparePatchDirectory(), () -> ensureDir(pakDirPath)));
        }
        for (var copy : filesToCopy) {
            progressItems.add(new LoadingItem(1, copy.getName(), () ->
                copyFile(copy, Path.of(pakDirPath.toString(), copy.getName()))
            ));
        }
        var sigFile = new File[]{null};
        if (!requireSigNames.isEmpty()) {
            progressItems.add(new LoadingItem(1, I18n.get().applyPatchLoadingPrepareSigFile(), () -> {
                sigFile[0] = getAnySigFile(basedir);
                return sigFile[0] != null;
            }));
        }
        for (var sig : requireSigNames) {
            progressItems.add(new LoadingItem(1, sig, () ->
                copyFile(sigFile[0], Path.of(pakDirPath.toString(), sig))
            ));
        }
        loadingStage.setItems(progressItems);
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
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
