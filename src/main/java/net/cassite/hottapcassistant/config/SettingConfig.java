package net.cassite.hottapcassistant.config;

import javafx.scene.control.Alert;
import net.cassite.hottapcassistant.component.setting.Setting;
import net.cassite.hottapcassistant.component.setting.SettingType;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.SimpleAlert;
import net.cassite.hottapcassistant.util.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public class SettingConfig {
    public final String settingsPath;
    public final String configPath;

    public SettingConfig(String settingsPath, String configPath) {
        this.settingsPath = settingsPath;
        this.configPath = configPath;
    }

    private static final LinkedHashMap<String, SettingType> availableSettings = new LinkedHashMap<>() {{
        put("dx11", SettingType.BOOL_0_1);
        put("Resolution_0", SettingType.RESOLUTION);
        put("bAutoCombatDiet", SettingType.BOOL);
        put("AutoCombatDietHpPercent", SettingType.FLOAT);
        put("bAutoCombatArtifactSkill", SettingType.BOOL);
        put("bAutoCombatChangeWeaponSkill", SettingType.BOOL);
        put("fFightCameraDistance", SettingType.FLOAT);
        put("MaxVisibilityPlayer", SettingType.INT);
        put("FrameRateLimit", SettingType.FLOAT);
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
        readConfigFrom(settings, configPath);
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
        Path configPath = Path.of(this.configPath);
        Path settingsPath = Path.of(this.settingsPath);
        var configsFile = Files.readAllLines(configPath);
        var settingsFile = Files.readAllLines(settingsPath);
        for (var s : settings) {
            if (s.lineIndex == -1 || s.source == null) {
                continue;
            }
            if (s.source.equals(this.configPath)) {
                if (s.lineIndex >= configsFile.size()) {
                    throw new IOException("config file has been changed, lines cannot match");
                }
                configsFile.set(s.lineIndex, s.toString());
            } else if (s.source.equals(this.settingsPath)) {
                if (s.lineIndex >= settingsFile.size()) {
                    throw new IOException("settings file has been changed, lines cannot match");
                }
                settingsFile.set(s.lineIndex, s.toString());
            }
        }
        Utils.writeFile(configPath, String.join("\n", configsFile));
        Utils.writeFile(settingsPath, String.join("\n", settingsFile));
    }
}
