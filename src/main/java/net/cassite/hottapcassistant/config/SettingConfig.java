package net.cassite.hottapcassistant.config;

import javafx.scene.control.Alert;
import net.cassite.hottapcassistant.component.setting.Setting;
import net.cassite.hottapcassistant.component.setting.SettingType;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Logger;
import net.cassite.hottapcassistant.util.SimpleAlert;
import net.cassite.hottapcassistant.util.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public class SettingConfig {
    public final String settingsPath;

    private SettingConfig(String settingsPath) {
        this.settingsPath = settingsPath;
    }

    public static SettingConfig ofSaved(String settingsPath) {
        return new SettingConfig(settingsPath);
    }

    private static final LinkedHashMap<String, SettingType> availableSettings = new LinkedHashMap<>() {{
        put("bAutoCombatDiet", SettingType.BOOL);
        put("AutoCombatDietHpPercent", SettingType.FLOAT);
        put("bAutoCombatArtifactSkill", SettingType.BOOL);
        put("bAutoCombatChangeWeaponSkill", SettingType.BOOL);
        put("fFightCameraDistance", SettingType.FLOAT);
        put("MaxVisibilityPlayer", SettingType.INT);
        put("FrameRateLimit", SettingType.FLOAT);
        put("ResolutionSizeX", SettingType.INT);
        put("ResolutionSizeY", SettingType.INT);
        put("FullscreenMode", SettingType.INT);
    }};
    private static final List<String> availableSettingsOrder = new ArrayList<>() {{
        addAll(availableSettings.keySet());
    }};
    private static final Map<String, Function<Object, Boolean>> additionalCheckMap = new HashMap<>() {{
        put("fFightCameraDistance", o -> {
            double d = (double) o;
            double min = 0;
            double max = 3;
            if (d < min || d > max) {
                new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().fightRangeOutOfBounds(min, max)).showAndWait();
                return false;
            }
            return true;
        });
    }};

    public List<Setting> read() throws IOException {
        List<Setting> settings = new ArrayList<>();
        initSettingsConfig();
        readConfigFrom(settings, settingsPath);

        settings.sort((a, b) -> {
            if (availableSettings.containsKey(a.name) && availableSettings.containsKey(b.name)) {
                return availableSettingsOrder.indexOf(a.name) - availableSettingsOrder.indexOf(b.name);
            } else if (availableSettings.containsKey(a.name)) {
                return -1;
            } else if (availableSettings.containsKey(b.name)) {
                return 1;
            } else {
                return a.name.compareTo(b.name);
            }
        });

        return settings;
    }

    private void initSettingsConfig() throws IOException {
        Path settingsPath = Path.of(this.settingsPath);
        var lines = Files.readAllLines(settingsPath);
        var gameUserSettingsIndex = -1;
        boolean hasResolutionSizeX = false;
        boolean hasResolutionSizeY = false;
        boolean hasFullscreenMode = false;
        for (var i = 0; i < lines.size(); ++i) {
            var line = lines.get(i);
            line = line.trim();
            if (line.startsWith("[") && line.endsWith("]")) {
                line = line.substring(1, line.length() - 1);
                line = line.trim();
                if (line.equals("/Script/QRSL.QRSLGameUserSettings")) {
                    gameUserSettingsIndex = i;
                }
            } else if (line.contains("=")) {
                var split = line.split("=");
                if (split.length != 2) continue;
                var key = split[0].trim();
                switch (key) {
                    case "ResolutionSizeX" -> hasResolutionSizeX = true;
                    case "ResolutionSizeY" -> hasResolutionSizeY = true;
                    case "FullscreenMode" -> hasFullscreenMode = true;
                }
            }
        }
        if (gameUserSettingsIndex == -1) {
            Logger.warn("cannot find [/Script/QRSL.QRSLGameUserSettings] in " + settingsPath);
            return;
        }
        var modified = false;
        if (!hasFullscreenMode) {
            lines.add(gameUserSettingsIndex + 1, "FullscreenMode=2");
            modified = true;
        }
        if (!hasResolutionSizeY) {
            lines.add(gameUserSettingsIndex + 1, "ResolutionSizeY=768");
            modified = true;
        }
        if (!hasResolutionSizeX) {
            lines.add(gameUserSettingsIndex + 1, "ResolutionSizeX=1024");
            modified = true;
        }
        if (modified) {
            Utils.writeFile(settingsPath, String.join("\n", lines));
        }
    }

    private void readConfigFrom(List<Setting> settings, String path) throws IOException {
        var lines = Files.readAllLines(Path.of(path));
        for (int i = 0; i < lines.size(); i++) {
            var line = lines.get(i);
            if (!line.contains("=")) {
                continue;
            }
            String[] split = line.split("=");
            if (split.length != 2) {
                continue;
            }
            String key = split[0].trim();
            if (!availableSettings.containsKey(key)) {
                continue;
            }
            var type = availableSettings.get(key);
            String value = split[1].trim();
            if (!type.check(value)) {
                new SimpleAlert(Alert.AlertType.WARNING, I18n.get().invalidConfigInFile(key, value)).show();
                continue;
            }
            Object v = type.parse(value);
            var set = new Setting();
            set.name = key;
            set.type = type;
            set.value = v;
            set.source = path;
            set.lineIndex = i;
            set.additionalCheck = additionalCheckMap.get(set.name);

            settings.add(set);
        }
    }

    public void write(List<Setting> settings) throws IOException {
        Path settingsPath = Path.of(this.settingsPath);
        var settingsFile = Files.readAllLines(settingsPath);

        for (var s : settings) {
            if (s.lineIndex == -1 || s.source == null) {
                continue;
            }
            if (s.source.equals(this.settingsPath)) {
                if (s.lineIndex >= settingsFile.size()) {
                    throw new IOException("settings file has been changed, lines cannot match");
                }
                settingsFile.set(s.lineIndex, s.toString());
            }
        }
        Utils.writeFile(settingsPath, String.join("\n", settingsFile));
    }
}
