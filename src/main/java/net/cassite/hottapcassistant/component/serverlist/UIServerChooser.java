package net.cassite.hottapcassistant.component.serverlist;

import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.ui.layout.VPadding;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import net.cassite.hottapcassistant.entity.TofServer;
import net.cassite.hottapcassistant.i18n.I18n;

import java.util.ArrayList;
import java.util.List;

public class UIServerChooser extends Dialog<List<TofServer>> {
    public UIServerChooser(List<TofServer> servers) {
        initStyle(StageStyle.UTILITY);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        getDialogPane().setPrefWidth(700);

        var vbox = new VBox();
        var ls = new UIServerList();
        ls.setItems(servers);
        ls.getNode().setMouseTransparent(true);
        ls.getNode().setDisable(true);
        var checkbox = new CheckBox(I18n.get().enableHostsFileModificationForGlobalServer()) {{
            FontManager.get().setFont(this);
        }};
        boolean checkboxSelected = false;
        for (var s : servers) {
            if (s.selected) {
                checkboxSelected = true;
                break;
            }
        }
        checkbox.selectedProperty().addListener((ob, old, now) -> {
            if (now == null) return;
            ls.getNode().setMouseTransparent(!now);
            ls.getNode().setDisable(!now);
        });
        checkbox.setSelected(checkboxSelected);
        vbox.getChildren().addAll(checkbox, new VPadding(5), ls.getNode());

        setResultConverter(t -> {
            if (t != ButtonType.OK) return null;
            if (!checkbox.isSelected()) return new ArrayList<>();
            var ret = new ArrayList<TofServer>();
            ls.getItems().forEach(e -> {
                if (e.selected) ret.add(e);
            });
            return ret;
        });

        getDialogPane().setContent(vbox);
    }
}
