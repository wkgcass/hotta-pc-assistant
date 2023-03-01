package net.cassite.hottapcassistant.ui;

import io.vproxy.vfx.ui.alert.SimpleAlert;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.util.FXUtils;
import javafx.scene.control.Alert;
import net.cassite.hottapcassistant.component.setting.UISettingList;
import net.cassite.hottapcassistant.config.SettingConfig;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.GlobalValues;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class GameSettingsScene extends WithConfirmScene {
    private final UISettingList ls;

    public GameSettingsScene() {
        ls = new UISettingList(this::setModified);
        FXUtils.observeWidthHeight(content, ls.getNode());
        content.getChildren().add(ls.getNode());

        var openGameUserSettingsIni = new FusionButton(I18n.get().openGameUserSettingsIni());
        openGameUserSettingsIni.setPrefWidth(180);
        FXUtils.observeHeight(bottomPane.getContentPane(), openGameUserSettingsIni);
        openGameUserSettingsIni.setOnAction(e -> {
            try {
                Desktop.getDesktop().open(new File(getSettingConfig().settingsPath));
            } catch (IOException ignore) {
                SimpleAlert.show(Alert.AlertType.ERROR, I18n.get().openFileFailed());
            }
        });
        insertElementToBottom(openGameUserSettingsIni);
    }

    @Override
    public String title() {
        return I18n.get().toolNameGameSettings();
    }

    private SettingConfig getSettingConfig() {
        var settings = Path.of(GlobalValues.savedPath.get(), "Config", "WindowsNoEditor", "GameUserSettings.ini").toString();
        return SettingConfig.ofSaved(settings, GlobalValues.getGameAssistantConfig());
    }

    @Override
    protected void confirm() throws Exception {
        getSettingConfig().write(ls.getItems());
    }

    @Override
    protected void reset() throws Exception {
        ls.setItems(getSettingConfig().read());
    }
}
