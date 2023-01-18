package net.cassite.hottapcassistant.ui;

import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.ui.alert.SimpleAlert;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import net.cassite.hottapcassistant.component.setting.UISettingList;
import net.cassite.hottapcassistant.config.SettingConfig;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.GlobalValues;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class GameSettingsPane extends WithConfirmPane {
    private final UISettingList ls;

    public GameSettingsPane() {
        ls = new UISettingList(this::setModified);
        widthProperty().addListener((ob, old, now) -> {
            if (now == null) {
                return;
            }
            ls.getNode().setPrefWidth(now.doubleValue() - 4);
        });
        heightProperty().addListener((ob, old, now) -> {
            if (now == null) {
                return;
            }
            ls.getNode().setPrefHeight(now.doubleValue() - 50);
        });
        content.getChildren().add(ls.getNode());

        var openGameUserSettingsIni = new Button(I18n.get().openGameUserSettingsIni()) {{
            FontManager.get().setFont(this);
        }};
        openGameUserSettingsIni.setOnAction(e -> {
            try {
                Desktop.getDesktop().open(new File(getSettingConfig().settingsPath));
            } catch (IOException ignore) {
                SimpleAlert.show(Alert.AlertType.ERROR, I18n.get().openFileFailed());
            }
        });
        insertElementToBottom(openGameUserSettingsIni);
    }

    private SettingConfig getSettingConfig() {
        var settings = Path.of(GlobalValues.savedPath.get(), "Config", "WindowsNoEditor", "GameUserSettings.ini").toString();
        return SettingConfig.ofSaved(settings, GlobalValues.getGameAssistantConfig());
    }

    @Override
    protected void confirm() throws IOException {
        getSettingConfig().write(ls.getItems());
    }

    @Override
    protected void reset() throws IOException {
        ls.setItems(getSettingConfig().read());
    }
}
