package net.cassite.hottapcassistant.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import net.cassite.hottapcassistant.i18n.I18n;

public class SimpleAlert extends Dialog<Void> {
    public SimpleAlert(Alert.AlertType type, String contentText) {
        this((
            type == Alert.AlertType.INFORMATION ?
                I18n.get().levelInformation() :
                (type == Alert.AlertType.ERROR ?
                    I18n.get().levelError() :
                    (type == Alert.AlertType.WARNING ?
                        I18n.get().levelWarning() :
                        type.name()))), contentText);
    }

    public SimpleAlert(String title, String contentText) {
        setTitle(title);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        getDialogPane().setContent(new Label(contentText) {{
            FontManager.setFont(this);
        }});
    }
}
