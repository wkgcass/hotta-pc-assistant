package net.cassite.hottapcassistant.ui;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import net.cassite.hottapcassistant.component.setting.UISettingList;
import net.cassite.hottapcassistant.config.SettingConfig;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.FontManager;
import net.cassite.hottapcassistant.util.GlobalValues;
import net.cassite.hottapcassistant.util.SimpleAlert;
import net.cassite.hottapcassistant.util.StyleUtils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class GameSettingsPane extends WithConfirmPane {
    private SettingConfig settingConfig;
    private final UISettingList ls;

    public GameSettingsPane() {
        ls = new UISettingList(this::setModified);
        StyleUtils.setNoFocusBlur(ls);
        widthProperty().addListener((ob, old, now) -> {
            if (now == null) {
                return;
            }
            ls.setPrefWidth(now.doubleValue() - 4);
        });
        heightProperty().addListener((ob, old, now) -> {
            if (now == null) {
                return;
            }
            ls.setPrefHeight(now.doubleValue() - 50);
        });
        content.getChildren().add(ls);

        var openGameUserSettingsIni = new Button(I18n.get().openGameUserSettingsIni()) {{
            FontManager.setFont(this);
        }};
        openGameUserSettingsIni.setOnAction(e -> {
            try {
                Desktop.getDesktop().open(new File(getSettingConfig().settingsPath));
            } catch (IOException ignore) {
                new SimpleAlert(Alert.AlertType.ERROR, I18n.get().openFileFailed()).show();
            }
        });
        var openConfigIni = new Button(I18n.get().openConfigIni()) {{
            FontManager.setFont(this);
        }};
        openConfigIni.setOnAction(e -> {
            try {
                Desktop.getDesktop().open(new File(getSettingConfig().configPath));
            } catch (IOException ignore) {
                new SimpleAlert(Alert.AlertType.ERROR, I18n.get().openFileFailed()).show();
            }
        });
        insertElementToBottom(openGameUserSettingsIni);
        insertElementToBottom(openConfigIni);
    }

    private SettingConfig getSettingConfig() {
        if (settingConfig == null) {
            settingConfig = new SettingConfig(Path.of(GlobalValues.SavedPath, "Config", "WindowsNoEditor", "GameUserSettings.ini").toString(),
                Path.of(GlobalValues.GamePath, "WmGpLaunch", "UserData", "Config", "Config.ini").toString());
        }
        return settingConfig;
    }

    @Override
    protected void confirm() throws IOException {
        getSettingConfig().write(ls.getItems());
    }

    @Override
    protected void reset() throws IOException {
        ls.setItems(FXCollections.observableList(getSettingConfig().read()));
    }
}
