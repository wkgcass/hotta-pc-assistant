package net.cassite.hottapcassistant.config;

import net.cassite.hottapcassistant.entity.Assistant;
import net.cassite.hottapcassistant.entity.GameAssistant;
import net.cassite.hottapcassistant.util.Utils;
import vjson.CharStream;
import vjson.JSON;
import vjson.cs.LineColCharStream;
import vjson.parser.ParserOptions;
import vjson.pl.ScriptifyContext;

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

    public static final Path assistantFilePath = Path.of(System.getProperty("user.home"), "AppData", "Local", "HottaPCAssistant", "Assistant.vjson");

    public static Assistant readAssistant() throws IOException {
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
            throw new IOException("failed to deserialize config from " + assistantFilePath, e);
        }
    }

    public static void writeAssistant(Assistant assistant) throws IOException {
        var sb = new StringBuilder();
        assistant.toJson().scriptify(sb, new ScriptifyContext(2));
        Utils.writeFile(assistantFilePath, sb.toString());
    }

    public static void updateAssistant(Consumer<Assistant> f) throws IOException {
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
        Utils.writeFile(Path.of(path), sb.toString());
    }

    public void updateGameAssistant(Consumer<GameAssistant> f) throws IOException {
        var a = readGameAssistant();
        f.accept(a);
        writeGameAssistant(a);
    }
}
