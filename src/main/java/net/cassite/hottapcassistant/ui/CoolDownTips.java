package net.cassite.hottapcassistant.ui;

import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.ui.alert.SimpleAlert;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.util.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Consts;

import java.awt.*;
import java.net.URL;
import java.util.Map;

public class CoolDownTips extends Stage {
    public CoolDownTips() {
        var vbox = new VBox();
        var scene = new Scene(vbox);
        setScene(scene);
        initStyle(StageStyle.UTILITY);

        setTitle(I18n.get().alertInfoTitle());

        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.getChildren().add(new Label(I18n.get().cooldownTips()) {{
            FontManager.get().setFont(Consts.NotoFont, this);
        }});
        vbox.getChildren().add(new VPadding(5));
        vbox.getChildren().add(new Hyperlink(I18n.get().cooldownTutorialLink()) {{
            FontManager.get().setFont(this, settings -> settings.setSize(12));
            setOnAction(e -> {
                var url = "https://www.acfun.cn/v/ac40130001";
                try {
                    Desktop.getDesktop().browse(new URL(url).toURI());
                } catch (Throwable t) {
                    Logger.error("failed opening cd indicator tutorial link", t);
                    Clipboard.getSystemClipboard().setContent(Map.of(DataFormat.PLAIN_TEXT, url));
                    SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().cooldownOpenBrowserForTutorialFailed(url));
                }
            });
        }});
        vbox.getChildren().add(new HBox() {{
            setAlignment(Pos.CENTER_RIGHT);
            var btn = new Button(I18n.get().confirm()) {{
                setPrefWidth(100);
            }};
            getChildren().add(btn);

            btn.setOnAction(e -> close());
        }});
    }
}
