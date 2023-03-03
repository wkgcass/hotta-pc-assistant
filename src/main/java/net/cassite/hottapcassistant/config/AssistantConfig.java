package net.cassite.hottapcassistant.config;

import io.vproxy.base.util.LogType;
import io.vproxy.vfx.control.dialog.VDialog;
import io.vproxy.vfx.control.dialog.VDialogButton;
import io.vproxy.commons.util.IOUtils;
import io.vproxy.base.util.Logger;
import net.cassite.hottapcassistant.entity.Assistant;
import net.cassite.hottapcassistant.entity.GameAssistant;
import net.cassite.hottapcassistant.i18n.I18n;
import vjson.CharStream;
import vjson.JSON;
import vjson.cs.LineColCharStream;
import vjson.parser.ParserOptions;
import vjson.pl.ScriptifyContext;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Consumer;

public class AssistantConfig {
    public final String path;

    private AssistantConfig(String path) {
        this.path = path;
    }

    public static AssistantConfig ofSaved(String path) {
        return new AssistantConfig(path);
    }

    public static final Path assistantDirPath = Path.of(System.getProperty("user.home"), "AppData", "Local", "HottaPCAssistant");
    public static final Path assistantFilePath = Path.of(assistantDirPath.toString(), "Assistant.vjson.txt");

    public static Assistant readAssistant() throws Exception {
        return readAssistant(false);
    }

    public static Assistant readAssistant(boolean askForDeletion) throws Exception {
        var f = assistantFilePath.toFile();
        if (f.exists()) {
            if (!f.isFile()) {
                throw new IOException(assistantFilePath + " is not a file");
            }
        } else {
            return Assistant.empty();
        }
        String str;
        try {
            str = Files.readString(assistantFilePath);
        } catch (IOException e) {
            if (askForDeletion) {
                var ret = askForDeletion();
                if (ret != null) {
                    return ret;
                }
            }
            throw e;
        }
        if (str.isBlank()) {
            return Assistant.empty();
        }
        try {
            return JSON.deserialize(
                new LineColCharStream(CharStream.from(str), f.getAbsolutePath()),
                Assistant.rule, ParserOptions.allFeatures());
        } catch (RuntimeException e) {
            if (askForDeletion) {
                var ret = askForDeletion();
                if (ret != null) {
                    return ret;
                }
            }
            throw new IOException("failed to deserialize config from " + assistantFilePath, e);
        }
    }

    private static Assistant askForDeletion() {
        var dialog = new VDialog<Integer>();
        dialog.setText(I18n.get().invalidAssistantConfigFileAskForDeletion(assistantFilePath.toString()));
        dialog.setButtons(Arrays.asList(
            new VDialogButton<>(I18n.get().modifyInvalidAssistantConfigBtn(), 1),
            new VDialogButton<>(I18n.get().deleteInvalidAssistantConfigBtn(), 2),
            new VDialogButton<>(I18n.get().cancelInvalidAssistantConfigBtn(), 3)
        ));
        var res = dialog.showAndWait();
        if (res.isPresent()) {
            int t = res.get();
            if (t == 2) {
                deleteAssistant();
                return Assistant.empty();
            } else if (t == 1) {
                try {
                    Desktop.getDesktop().open(assistantFilePath.toFile());
                } catch (IOException ee) {
                    Logger.error(LogType.SYS_ERROR, "failed opening invalid assistant config file", ee);
                }
            }
        }
        return null;
    }

    public static void deleteAssistant() {
        try {
            Files.delete(assistantFilePath);
            Logger.warn(LogType.ALERT, "assistant config file deleted: " + assistantFilePath);
        } catch (IOException ee) {
            Logger.error(LogType.FILE_ERROR, "deleting assistant config file failed: " + assistantFilePath, ee);
        }
    }

    public static void writeAssistant(Assistant assistant) throws Exception {
        var sb = new StringBuilder();
        assistant.toJson().scriptify(sb, new ScriptifyContext(2));
        IOUtils.writeFileWithBackup(assistantFilePath.toString(), sb.toString());
    }

    public static void updateAssistant(Consumer<Assistant> f) throws Exception {
        var ass = readAssistant();
        f.accept(ass);
        writeAssistant(ass);
    }

    public GameAssistant readGameAssistant() throws IOException {
        var f = new File(path);
        if (f.exists()) {
            if (!f.isFile()) {
                throw new IOException(path + " is not a file");
            }
        } else {
            return GameAssistant.empty();
        }
        var str = Files.readString(Path.of(path));
        if (str.isBlank()) {
            return GameAssistant.empty();
        }
        try {
            return JSON.deserialize(new LineColCharStream(CharStream.from(str), f.getAbsolutePath()),
                GameAssistant.rule, ParserOptions.allFeatures());
        } catch (RuntimeException e) {
            throw new IOException("failed to deserialize config from " + path, e);
        }
    }

    public void writeGameAssistant(GameAssistant assistant) throws Exception {
        var sb = new StringBuilder();
        assistant.toJson().scriptify(sb, new ScriptifyContext(2));
        IOUtils.writeFileWithBackup(path, sb.toString());
    }

    public void updateGameAssistant(Consumer<GameAssistant> f) throws Exception {
        var a = readGameAssistant();
        f.accept(a);
        writeGameAssistant(a);
    }
}
