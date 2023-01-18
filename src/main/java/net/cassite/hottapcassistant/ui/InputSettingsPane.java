package net.cassite.hottapcassistant.ui;

import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.ui.alert.SimpleAlert;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import net.cassite.hottapcassistant.component.keybinding.UIKeyBindingList;
import net.cassite.hottapcassistant.config.InputConfig;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.GlobalValues;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class InputSettingsPane extends WithConfirmPane {
    private final UIKeyBindingList ls;

    public InputSettingsPane() {
        ls = new UIKeyBindingList(this::setModified);
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

        var openInputIni = new Button(I18n.get().openInputIni()) {{
            FontManager.get().setFont(this);
        }};
        openInputIni.setOnAction(e -> {
            try {
                Desktop.getDesktop().open(new File(getInputConfig().path));
            } catch (IOException ignore) {
                SimpleAlert.show(Alert.AlertType.ERROR, I18n.get().openFileFailed());
            }
        });
        insertElementToBottom(openInputIni);
        var aboutAxisMappings = new Button(I18n.get().aboutEmptyTableOrMissingFields()) {{
            FontManager.get().setFont(this);
        }};
        aboutAxisMappings.setOnAction(e ->
            SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().detailAboutEmptyTableOrMissingFields()));
        insertElementToBottom(aboutAxisMappings);
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
