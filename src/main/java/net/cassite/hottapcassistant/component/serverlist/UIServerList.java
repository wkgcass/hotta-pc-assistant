package net.cassite.hottapcassistant.component.serverlist;

import io.vproxy.vfx.ui.table.VTableColumn;
import io.vproxy.vfx.ui.table.VTableView;
import javafx.scene.control.CheckBox;
import net.cassite.hottapcassistant.entity.TofServer;
import net.cassite.hottapcassistant.i18n.I18n;

public class UIServerList extends VTableView<TofServer> {
    public UIServerList() {
        var checkColumn = new VTableColumn<TofServer, TofServer>("", s -> s);
        var regionColumn = new VTableColumn<TofServer, String>(I18n.get().serverListColumnNameRegion(), s -> s.region);
        var nameColumn = new VTableColumn<TofServer, String>(I18n.get().serverListColumnNameName(), s -> s.name);

        checkColumn.setMinWidth(60);
        checkColumn.setPrefWidth(60);
        checkColumn.setMaxWidth(80);
        checkColumn.setNodeBuilder(s -> {
            var checkbox = new CheckBox();
            checkbox.setSelected(s.selected);
            checkbox.setOnAction(e -> s.selected = checkbox.isSelected());
            return checkbox;
        });

        //noinspection unchecked
        getColumns().addAll(checkColumn, regionColumn, nameColumn);
    }
}
