package net.cassite.hottapcassistant.ui;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import net.cassite.hottapcassistant.component.keybinding.UIKeyBindingList;
import net.cassite.hottapcassistant.config.InputConfig;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.FontManager;
import net.cassite.hottapcassistant.util.GlobalValues;
import net.cassite.hottapcassistant.util.SimpleAlert;
import net.cassite.hottapcassistant.util.StyleUtils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class InputSettingsPane extends WithConfirmPane {
    private InputConfig inputConfig = null;
    private final UIKeyBindingList ls;

    public InputSettingsPane() {
        ls = new UIKeyBindingList(this::setModified);
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

        var openInputIni = new Button(I18n.get().openInputIni()) {{
            FontManager.setFont(this);
        }};
        openInputIni.setOnAction(e -> {
            try {
                Desktop.getDesktop().open(new File(getInputConfig().path));
            } catch (IOException ignore) {
                new SimpleAlert(Alert.AlertType.ERROR, I18n.get().openFileFailed()).show();
            }
        });
        insertElementToBottom(openInputIni);
        var aboutAxisMappings = new Button(I18n.get().aboutEmptyTableOrMissingFields()) {{
            FontManager.setFont(this);
        }};
        aboutAxisMappings.setOnAction(e -> {
            new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().detailAboutEmptyTableOrMissingFields()).showAndWait();
        });
        insertElementToBottom(aboutAxisMappings);
    }

    private InputConfig getInputConfig() {
        if (inputConfig == null) {
            inputConfig = new InputConfig(Path.of(GlobalValues.SavedPath, "Config", "WindowsNoEditor", "Input.ini").toString());
        }
        return inputConfig;
    }

    @Override
    protected void confirm() throws IOException {
        getInputConfig().write(ls.getItems());
    }

    @Override
    protected void reset() throws IOException {
        ls.setItems(FXCollections.observableList(getInputConfig().read()));
    }
}
