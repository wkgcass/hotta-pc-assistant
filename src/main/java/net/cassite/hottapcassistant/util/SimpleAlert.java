package net.cassite.hottapcassistant.util;

import javafx.scene.control.*;
import net.cassite.hottapcassistant.i18n.I18n;

import java.util.function.Consumer;

public class SimpleAlert extends Dialog<Void> {
    public SimpleAlert(Alert.AlertType type, String contentText) {
        this(type, contentText, null);
    }

    public SimpleAlert(Alert.AlertType type, String contentText, Consumer<Labeled> fontSetter) {
        this((
            type == Alert.AlertType.INFORMATION ?
                I18n.get().levelInformation() :
                (type == Alert.AlertType.ERROR ?
                    I18n.get().levelError() :
                    (type == Alert.AlertType.WARNING ?
                        I18n.get().levelWarning() :
                        type.name()))), contentText, fontSetter);
    }

    public SimpleAlert(String title, String contentText) {
        this(title, contentText, null);
    }

    public SimpleAlert(String title, String contentText, Consumer<Labeled> fontSetter) {
        setTitle(title);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        getDialogPane().setContent(new Label(contentText) {{
            if (fontSetter == null) {
                FontManager.setFont(this);
            } else {
                fontSetter.accept(this);
            }
        }});
    }
}
