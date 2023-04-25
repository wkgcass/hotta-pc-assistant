package net.cassite.hottapcassistant.ui;

import io.vproxy.vfx.component.logconsole.LogConsole;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.util.FXUtils;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import net.cassite.hottapcassistant.i18n.I18n;

public class LogScene extends AbstractMainScene {
    public LogScene() {
        enableAutoContentWidthHeight();

        var logConsole = new LogConsole();
        var scrollCheckBox = new CheckBox(I18n.get().scrollLogCheckBoxDesc()) {{
            FXUtils.disableFocusColor(this);
            FontManager.get().setFont(this);
            setTextFill(Theme.current().normalTextColor());
        }};
        scrollCheckBox.setSelected(true);
        logConsole.setAlwaysScrollToEnd(true);

        scrollCheckBox.setOnAction(e -> logConsole.setAlwaysScrollToEnd(scrollCheckBox.isSelected()));

        FXUtils.observeWidthHeight(getContentPane(), logConsole.getNode(), -60, -80);

        getContentPane().getChildren().addAll(new VBox(logConsole.getNode(), scrollCheckBox
        ) {{
            setPadding(new Insets(30, 20, 30, 20));
            setSpacing(10);
        }});
    }

    @Override
    public String title() {
        return I18n.get().toolNameLog();
    }
}
