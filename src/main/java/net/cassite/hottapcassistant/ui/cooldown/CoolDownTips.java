package net.cassite.hottapcassistant.ui.cooldown;

import io.vproxy.vfx.control.scroll.ScrollDirection;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.alert.SimpleAlert;
import io.vproxy.vfx.ui.layout.HPadding;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.scene.VScene;
import io.vproxy.vfx.ui.scene.VSceneRole;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import io.vproxy.vfx.util.Logger;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.*;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Consts;

import java.awt.*;
import java.net.URL;
import java.util.Map;

public class CoolDownTips extends VScene {
    public CoolDownTips() {
        super(VSceneRole.TEMPORARY);
        getScrollPane().setScrollDirection(ScrollDirection.HORIZONTAL);
        getNode().setBackground(new Background(new BackgroundFill(
            Theme.current().sceneBackgroundColor(),
            CornerRadii.EMPTY,
            Insets.EMPTY
        )));

        var vbox = new VBox();
        FXUtils.observeWidthCenter(getContentPane(), vbox);
        getContentPane().getChildren().add(new HBox(
            new HPadding(30),
            vbox
        ));
        vbox.getChildren().add(new VPadding(40));

        vbox.getChildren().add(new ThemeLabel(I18n.get().cooldownTips()) {{
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
    }
}
