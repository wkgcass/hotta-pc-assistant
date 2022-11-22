package net.cassite.hottapcassistant.util;

import net.cassite.hottapcassistant.config.AssistantConfig;

import java.nio.file.Path;

public class GlobalValues {
    private GlobalValues() {
    }

    public static String SavedPath = null;
    public static String GamePath = null;
    private static volatile AssistantConfig Config = null;

    public static AssistantConfig getAssistantConfig() {
        if (Config == null) {
            if (SavedPath == null) throw new IllegalStateException();
            synchronized (GlobalValues.class) {
                if (Config == null) {
                    Config = new AssistantConfig(Path.of(SavedPath, "Assistant.json").toAbsolutePath().toString());
                }
            }
        }
        return Config;
    }
}
