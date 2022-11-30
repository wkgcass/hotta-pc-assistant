package net.cassite.hottapcassistant.component.serverlist;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import net.cassite.hottapcassistant.entity.TofServer;
import net.cassite.hottapcassistant.i18n.I18n;

public class UIServerList extends TableView<TofServer> {
    public UIServerList() {
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        ScrollBar hScrollBar = (ScrollBar) lookup(".scroll-bar:horizontal");
        if (hScrollBar != null) {
            hScrollBar.setVisible(false);
        }
        var checkColumn = new TableColumn<TofServer, TofServer>("");
        var regionColumn = new TableColumn<TofServer, String>(I18n.get().serverListColumnNameRegion());
        var nameColumn = new TableColumn<TofServer, String>(I18n.get().serverListColumnNameName());

        checkColumn.setSortable(false);
        checkColumn.setMinWidth(60);
        checkColumn.setPrefWidth(60);
        checkColumn.setMaxWidth(60);
        checkColumn.setCellValueFactory(f -> new SimpleObjectProperty<>(f.getValue()));
        checkColumn.setCellFactory(param -> {
            var cell = new TableCell<TofServer, TofServer>();
            cell.itemProperty().addListener((o, old, now) -> {
                if (cell.getTableRow() == null || cell.getTableRow().getItem() == null) {
                    return;
                }
                if (now == null) return;
                var row = cell.getTableRow().getItem();
                var checkbox = new CheckBox();
                cell.setGraphic(checkbox);
                checkbox.setSelected(now.selected);
                checkbox.setOnAction(e -> row.selected = checkbox.isSelected());
            });
            return cell;
        });
        regionColumn.setSortable(false);
        regionColumn.setCellValueFactory(f -> new SimpleStringProperty(I18n.get().configNameMapping(f.getValue().region)));
        nameColumn.setSortable(false);
        nameColumn.setCellValueFactory(f -> new SimpleStringProperty(I18n.get().configNameMapping(f.getValue().name)));

        //noinspection unchecked
        getColumns().addAll(checkColumn, regionColumn, nameColumn);
    }
}
