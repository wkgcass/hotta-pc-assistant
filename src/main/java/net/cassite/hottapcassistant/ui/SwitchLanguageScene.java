package net.cassite.hottapcassistant.ui;

import io.vproxy.vfx.ui.alert.SimpleAlert;
import io.vproxy.vfx.ui.alert.StackTraceAlert;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.util.FXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import net.cassite.hottapcassistant.config.AssistantConfig;
import net.cassite.hottapcassistant.i18n.I18nType;

public class SwitchLanguageScene extends AbstractMainScene {
    public SwitchLanguageScene() {
        enableAutoContentWidthHeight();
        var vbox = new VBox();
        FXUtils.observeWidthHeightCenter(getContentPane(), vbox);

        vbox.setSpacing(40);
        vbox.setAlignment(Pos.CENTER);
        var zhcn = new FusionButton("切换到中文") {{
            setPrefWidth(240);
            setMaxWidth(240);
            setPrefHeight(60);
            setOnAction(e -> {
                try {
                    AssistantConfig.updateAssistant(a -> a.i18n = I18nType.ZhCn);
                } catch (Exception ex) {
                    StackTraceAlert.show("切换至中文失败", ex);
                    return;
                }
                SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, "切换成功，程序即将退出。请手动重新打开。");
                System.exit(0);
            });
        }};
        var enus = new FusionButton("Switch to English") {{
            setPrefWidth(240);
            setMaxWidth(240);
            setPrefHeight(60);
            setOnAction(e -> {
                try {
                    AssistantConfig.updateAssistant(a -> a.i18n = I18nType.EnUs);
                } catch (Exception ex) {
                    StackTraceAlert.show("Switching to English failed", ex);
                    return;
                }
                SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, "Switching to English succeeded. The program will exit, please re-open manually.");
                System.exit(0);
            });
        }};
        vbox.getChildren().addAll(
            zhcn,
            enus
        );

        getContentPane().getChildren().add(vbox);
    }

    @Override
    public String title() {
        return "Language";
    }
}
