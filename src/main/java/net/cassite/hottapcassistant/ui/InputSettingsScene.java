package net.cassite.hottapcassistant.ui;

import io.vproxy.vfx.ui.alert.SimpleAlert;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.util.FXUtils;
import javafx.scene.control.Alert;
import net.cassite.hottapcassistant.component.keybinding.UIKeyBindingList;
import net.cassite.hottapcassistant.config.InputConfig;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.GlobalValues;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class InputSettingsScene extends WithConfirmScene {
    private final UIKeyBindingList ls;

    public InputSettingsScene() {
        ls = new UIKeyBindingList(this::setModified);
        FXUtils.observeWidthHeight(content, ls.getNode());
        content.getChildren().add(ls.getNode());

        var openInputIni = new FusionButton(I18n.get().openInputIni());
        FXUtils.observeHeight(bottomPane.getContentPane(), openInputIni);
        openInputIni.setPrefWidth(120);
        openInputIni.setOnAction(e -> {
            try {
                Desktop.getDesktop().open(new File(getInputConfig().path));
            } catch (IOException ignore) {
                SimpleAlert.show(Alert.AlertType.ERROR, I18n.get().openFileFailed());
            }
        });
        insertElementToBottom(openInputIni);
        var aboutAxisMappings = new FusionButton(I18n.get().aboutEmptyTableOrMissingFields());
        FXUtils.observeHeight(bottomPane.getContentPane(), aboutAxisMappings);
        aboutAxisMappings.setPrefWidth(300);
        aboutAxisMappings.setOnAction(e ->
            SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().detailAboutEmptyTableOrMissingFields()));
        insertElementToBottom(aboutAxisMappings);
    }

    @Override
    public String title() {
        return I18n.get().toolNameInputSettings();
    }

    private InputConfig getInputConfig() {
        return InputConfig.ofSaved(Path.of(GlobalValues.savedPath.get(), "Config", "WindowsNoEditor", "Input.ini").toString());
    }

    @Override
    protected void confirm() throws IOException {
        getInputConfig().write(ls.getItems());
    }

    @Override
    protected void reset() throws IOException {
        ls.setItems(getInputConfig().read());
    }
}
