package net.cassite.hottapcassistant.component.serverlist;

import io.vproxy.vfx.control.dialog.VDialog;
import io.vproxy.vfx.control.dialog.VDialogButton;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.util.FXUtils;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import net.cassite.hottapcassistant.entity.TofServer;
import net.cassite.hottapcassistant.i18n.I18n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UIServerChooser extends VDialog<List<TofServer>> {
    private final VDialogButton<List<TofServer>> okBtn = new VDialogButton<>(I18n.get().alertOkButton());
    private final CheckBox checkbox = new CheckBox(I18n.get().enableHostsFileModificationForGlobalServer()) {{
        FontManager.get().setFont(this);
        FXUtils.disableFocusColor(this);
        setTextFill(Theme.current().normalTextColor());
    }};
    private final UIServerList ls = new UIServerList();

    public UIServerChooser(List<TofServer> servers) {
        setButtons(Arrays.asList(okBtn, new VDialogButton<>(I18n.get().cancelButton())));

        getStage().getStage().setWidth(400);

        var vbox = new VBox();
        ls.setItems(servers);
        ls.getNode().setMouseTransparent(true);
        ls.getNode().setDisable(true);
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

        getCleanContent().getChildren().add(vbox);
    }

    @Override
    protected void onButtonClicked(VDialogButton<List<TofServer>> btn) {
        if (btn != okBtn) {
            return;
        }
        if (!checkbox.isSelected()) {
            returnValue = new ArrayList<>();
            return;
        }
        var ret = new ArrayList<TofServer>();
        ls.getItems().forEach(e -> {
            if (e.selected) ret.add(e);
        });
        returnValue = ret;
    }
}
