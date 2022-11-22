package net.cassite.hottapcassistant.config;

import net.cassite.hottapcassistant.entity.Assistant;
import net.cassite.hottapcassistant.util.Utils;
import vjson.JSON;
import vjson.stringifier.PrettyStringifier;
import vjson.util.PrintableChars;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AssistantConfig {
    public final String path;

    public AssistantConfig(String path) {
        this.path = path;
    }

    public Assistant read() throws IOException {
        var f = new File(path);
        if (f.exists()) {
            if (!f.isFile()) {
                throw new IOException(path + " is not a file");
            }
        } else {
            return Assistant.empty();
        }
        var str = Files.readString(Path.of(path));
        if (str.isBlank()) {
            return Assistant.empty();
        }
        try {
            return JSON.deserialize(str, Assistant.rule);
        } catch (RuntimeException e) {
            throw new IOException("failed to deserialize config from " + path, e);
        }
    }

    public void write(Assistant assistant) throws IOException {
        var sb = new StringBuilder();
        assistant.toJson().stringify(sb, new PrettyStringifier() {
            private static final StringOptions stringOptions = new StringOptions(
                PrintableChars.EveryCharExceptKnownUnprintable
            );

            @Override
            public StringOptions stringOptions() {
                return stringOptions;
            }
        });
        Utils.writeFile(Path.of(path), sb.toString());
    }
}
