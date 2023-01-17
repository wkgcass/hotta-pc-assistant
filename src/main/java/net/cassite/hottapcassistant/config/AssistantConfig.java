package net.cassite.hottapcassistant.config;

import io.vproxy.vfx.util.IOUtils;
import io.vproxy.vfx.util.Logger;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
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
        var str = Files.readString(assistantFilePath);
        if (str.isBlank()) {
            return Assistant.empty();
        }
        try {
            return JSON.deserialize(
                new LineColCharStream(CharStream.from(str), f.getAbsolutePath()),
                Assistant.rule, ParserOptions.allFeatures());
        } catch (RuntimeException e) {
            if (askForDeletion) {
                var dialog = new Dialog<ButtonType>();
                dialog.setContentText(I18n.get().invalidAssistantConfigFileAskForDeletion(assistantFilePath.toString()));
                var modify = new ButtonType(I18n.get().modifyInvalidAssistantConfigBtn(), ButtonBar.ButtonData.OK_DONE);
                var delete = new ButtonType(I18n.get().deleteInvalidAssistantConfigBtn(), ButtonBar.ButtonData.OK_DONE);
                var cancel = new ButtonType(I18n.get().cancelInvalidAssistantConfigBtn(), ButtonBar.ButtonData.CANCEL_CLOSE);
                dialog.getDialogPane().getButtonTypes().addAll(modify, delete, cancel);
                var res = dialog.showAndWait();
                if (res.isPresent()) {
                    var t = res.get();
                    if (t == delete) {
                        try {
                            Files.delete(assistantFilePath);
                        } catch (IOException ee) {
                            Logger.error("deleting invalid assistant config file failed", ee);
                        }
                        return Assistant.empty();
                    } else if (t == modify) {
                        try {
                            Desktop.getDesktop().open(assistantFilePath.toFile());
                        } catch (IOException ee) {
                            Logger.error("failed opening invalid assistant config file", ee);
                        }
                    }
                }
            }
            throw new IOException("failed to deserialize config from " + assistantFilePath, e);
        }
    }

    public static void writeAssistant(Assistant assistant) throws IOException {
        var sb = new StringBuilder();
        assistant.toJson().scriptify(sb, new ScriptifyContext(2));
        IOUtils.writeFile(assistantFilePath, sb.toString());
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

    public void writeGameAssistant(GameAssistant assistant) throws IOException {
        var sb = new StringBuilder();
        assistant.toJson().scriptify(sb, new ScriptifyContext(2));
        IOUtils.writeFile(Path.of(path), sb.toString());
    }

    public void updateGameAssistant(Consumer<GameAssistant> f) throws IOException {
        var a = readGameAssistant();
        f.accept(a);
        writeGameAssistant(a);
    }
}
