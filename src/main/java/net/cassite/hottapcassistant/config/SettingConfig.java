package net.cassite.hottapcassistant.config;

import io.vproxy.base.util.LogType;
import io.vproxy.vfx.ui.alert.SimpleAlert;
import io.vproxy.commons.util.IOUtils;
import io.vproxy.base.util.Logger;
import javafx.scene.control.Alert;
import net.cassite.hottapcassistant.component.setting.Setting;
import net.cassite.hottapcassistant.component.setting.SettingType;
import net.cassite.hottapcassistant.entity.GameAssistant;
import net.cassite.hottapcassistant.i18n.I18n;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public class SettingConfig {
    public final String settingsPath;
    public final AssistantConfig assistant;

    private SettingConfig(String settingsPath, AssistantConfig assistant) {
        this.settingsPath = settingsPath;
        this.assistant = assistant;
    }

    public static SettingConfig ofSaved(String settingsPath, AssistantConfig assistant) {
        return new SettingConfig(settingsPath, assistant);
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
        put("bPreferD3D12InGame", SettingType.BOOL);
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
                SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().fightRangeOutOfBounds(min, max));
                return false;
            }
            return true;
        });
    }};

    public List<Setting> read() throws Exception {
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

    private void initSettingsConfig() throws Exception {
        Path settingsPath = Path.of(this.settingsPath);
        var lines = Files.readAllLines(settingsPath);
        var gameUserSettingsIndex = -1;
        var d3dRHIPreferenceIndex = -1;
        boolean hasResolutionSizeX = false;
        boolean hasResolutionSizeY = false;
        boolean hasFullscreenMode = false;
        boolean hasD3D12 = false;
        for (var i = 0; i < lines.size(); ++i) {
            var line = lines.get(i);
            line = line.trim();
            if (line.startsWith("[") && line.endsWith("]")) {
                line = line.substring(1, line.length() - 1);
                line = line.trim();
                if (line.equals("/Script/QRSL.QRSLGameUserSettings")) {
                    gameUserSettingsIndex = i;
                } else if (line.equals("D3DRHIPreference")) {
                    d3dRHIPreferenceIndex = i;
                }
            } else if (line.contains("=")) {
                var split = line.split("=");
                if (split.length != 2) continue;
                var key = split[0].trim();
                switch (key) {
                    case "ResolutionSizeX" -> hasResolutionSizeX = true;
                    case "ResolutionSizeY" -> hasResolutionSizeY = true;
                    case "FullscreenMode" -> hasFullscreenMode = true;
                    case "bPreferD3D12InGame" -> hasD3D12 = true;
                }
            }
        }
        if (gameUserSettingsIndex == -1) {
            Logger.warn(LogType.INVALID_EXTERNAL_DATA,"cannot find [/Script/QRSL.QRSLGameUserSettings] in " + settingsPath);
            return;
        }
        GameAssistant gameAssistant;
        try {
            gameAssistant = assistant.readGameAssistant();
        } catch (Exception e) {
            gameAssistant = null;
        }
        var modified = 0;
        if (!hasFullscreenMode) {
            int mode = 2;
            if (gameAssistant != null && gameAssistant.fullscreenMode != 0) {
                mode = gameAssistant.fullscreenMode;
            }
            lines.add(gameUserSettingsIndex + 1, "FullscreenMode=" + mode);
            ++modified;
        }
        if (!hasResolutionSizeY) {
            int resolutionSizeY = 720;
            if (gameAssistant != null && gameAssistant.resolutionSizeY != 0) {
                resolutionSizeY = gameAssistant.resolutionSizeY;
            }
            lines.add(gameUserSettingsIndex + 1, "ResolutionSizeY=" + resolutionSizeY);
            ++modified;
        }
        if (!hasResolutionSizeX) {
            int resolutionSizeX = 1280;
            if (gameAssistant != null && gameAssistant.resolutionSizeX != 0) {
                resolutionSizeX = gameAssistant.resolutionSizeX;
            }
            lines.add(gameUserSettingsIndex + 1, "ResolutionSizeX=" + resolutionSizeX);
            ++modified;
        }
        if (!hasD3D12) {
            if (d3dRHIPreferenceIndex == -1) {
                lines.add("[D3DRHIPreference]");
                lines.add("bPreferD3D12InGame=True");
            } else {
                if (d3dRHIPreferenceIndex < gameUserSettingsIndex) {
                    lines.add(d3dRHIPreferenceIndex + 1, "bPreferD3D12InGame=True");
                } else {
                    lines.add(d3dRHIPreferenceIndex + 1 + modified, "bPreferD3D12InGame=True");
                }
            }
            ++modified;
        }
        if (modified != 0) {
            IOUtils.writeFileWithBackup(settingsPath.toString(), String.join("\n", lines));
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
                SimpleAlert.show(Alert.AlertType.WARNING, I18n.get().invalidConfigInFile(key, value));
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

    public void write(List<Setting> settings) throws Exception {
        Path settingsPath = Path.of(this.settingsPath);
        var settingsFile = Files.readAllLines(settingsPath);

        int fullscreenMode = 0;
        int resolutionSizeX = 0;
        int resolutionSizeY = 0;
        boolean modified = false;

        for (var s : settings) {
            if ("FullscreenMode".equals(s.name)) {
                fullscreenMode = (int) s.value;
                modified = true;
            } else if ("ResolutionSizeX".equals(s.name)) {
                resolutionSizeX = (int) s.value;
                modified = true;
            } else if ("ResolutionSizeY".equals(s.name)) {
                resolutionSizeY = (int) s.value;
                modified = true;
            }
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
        IOUtils.writeFileWithBackup(settingsPath.toString(), String.join("\n", settingsFile));
        if (modified) {
            final var fFullscreenMode = fullscreenMode;
            final var fResolutionSizeX = resolutionSizeX;
            final var fResolutionSizeY = resolutionSizeY;
            assistant.updateGameAssistant(a -> {
                if (fFullscreenMode != 0) {
                    a.fullscreenMode = fFullscreenMode;
                }
                if (fResolutionSizeX != 0) {
                    a.resolutionSizeX = fResolutionSizeX;
                }
                if (fResolutionSizeY != 0) {
                    a.resolutionSizeY = fResolutionSizeY;
                }
            });
        }
    }
}
