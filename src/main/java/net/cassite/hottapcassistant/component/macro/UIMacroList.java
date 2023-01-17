package net.cassite.hottapcassistant.component.macro;

import io.vproxy.vfx.component.keychooser.KeyChooser;
import io.vproxy.vfx.ui.alert.SimpleAlert;
import io.vproxy.vfx.util.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import net.cassite.hottapcassistant.entity.AssistantMacroData;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.ui.Pointer;

public class UIMacroList extends TableView<AssistantMacroData> {
    public UIMacroList(Runnable modifiedCallback) {
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        ScrollBar hScrollBar = (ScrollBar) lookup(".scroll-bar:horizontal");
        if (hScrollBar != null) {
            hScrollBar.setVisible(false);
        }

        var enableColumn = new TableColumn<AssistantMacroData, AssistantMacroData>(I18n.get().macroColumnNameEnable());
        var macroColumn = new TableColumn<AssistantMacroData, String>(I18n.get().macroColumnNameName());
        var ctrlColumn = new TableColumn<AssistantMacroData, AssistantMacroData>(I18n.get().hotkeyColumnNameCtrl());
        var altColumn = new TableColumn<AssistantMacroData, AssistantMacroData>(I18n.get().hotkeyColumnNameAlt());
        var shiftColumn = new TableColumn<AssistantMacroData, AssistantMacroData>(I18n.get().hotkeyColumnNameShift());
        var keyColumn = new TableColumn<AssistantMacroData, Pointer<AssistantMacroData>>(I18n.get().hotkeyColumnNameKey());

        enableColumn.setSortable(false);
        enableColumn.setCellValueFactory(f -> new SimpleObjectProperty<>(f.getValue()));
        enableColumn.setCellFactory(param -> {
            var cell = new TableCell<AssistantMacroData, AssistantMacroData>();
            cell.itemProperty().addListener((o, old, now) -> {
                if (cell.getTableRow() == null || cell.getTableRow().getItem() == null || now == null) {
                    cell.setGraphic(null);
                    return;
                }
                var row = cell.getTableRow().getItem();
                var checkBox = new CheckBox();
                cell.setGraphic(checkBox);
                checkBox.setSelected(now.enabled);
                checkBox.setOnAction(e -> {
                    row.enabled = checkBox.isSelected();
                    modifiedCallback.run();
                });
            });
            return cell;
        });
        macroColumn.setSortable(false);
        macroColumn.setMinWidth(100);
        macroColumn.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().name));
        ctrlColumn.setSortable(false);
        ctrlColumn.setCellValueFactory(f -> new SimpleObjectProperty<>(f.getValue()));
        ctrlColumn.setCellFactory(param -> {
            var cell = new TableCell<AssistantMacroData, AssistantMacroData>();
            cell.itemProperty().addListener((o, old, now) -> {
                if (cell.getTableRow() == null || cell.getTableRow().getItem() == null || now == null) {
                    cell.setGraphic(null);
                    return;
                }
                var row = cell.getTableRow().getItem();
                var checkBox = new CheckBox();
                cell.setGraphic(checkBox);
                checkBox.setSelected(now.ctrl);
                checkBox.setOnAction(e -> {
                    row.ctrl = checkBox.isSelected();
                    modifiedCallback.run();
                });
            });
            return cell;
        });
        altColumn.setSortable(false);
        altColumn.setCellValueFactory(f -> new SimpleObjectProperty<>(f.getValue()));
        altColumn.setCellFactory(param -> {
            var cell = new TableCell<AssistantMacroData, AssistantMacroData>();
            cell.itemProperty().addListener((o, old, now) -> {
                if (cell.getTableRow() == null || cell.getTableRow().getItem() == null || now == null) {
                    cell.setGraphic(null);
                    return;
                }
                var row = cell.getTableRow().getItem();
                var checkBox = new CheckBox();
                cell.setGraphic(checkBox);
                checkBox.setSelected(now.alt);
                checkBox.setOnAction(e -> {
                    row.alt = checkBox.isSelected();
                    modifiedCallback.run();
                });
            });
            return cell;
        });
        shiftColumn.setSortable(false);
        shiftColumn.setCellValueFactory(f -> new SimpleObjectProperty<>(f.getValue()));
        shiftColumn.setCellFactory(param -> {
            var cell = new TableCell<AssistantMacroData, AssistantMacroData>();
            cell.itemProperty().addListener((o, old, now) -> {
                if (cell.getTableRow() == null || cell.getTableRow().getItem() == null || now == null) {
                    cell.setGraphic(null);
                    return;
                }
                var row = cell.getTableRow().getItem();
                var checkBox = new CheckBox();
                cell.setGraphic(checkBox);
                checkBox.setSelected(now.shift);
                checkBox.setOnAction(e -> {
                    row.shift = checkBox.isSelected();
                    modifiedCallback.run();
                });
            });
            return cell;
        });
        keyColumn.setSortable(false);
        keyColumn.setMinWidth(100);
        keyColumn.setCellValueFactory(f -> new SimpleObjectProperty<>(Pointer.of(f.getValue())));
        keyColumn.setCellFactory(param -> {
            var cell = new TableCell<AssistantMacroData, Pointer<AssistantMacroData>>();
            cell.setAlignment(Pos.CENTER);
            cell.itemProperty().addListener((o, old, now) -> {
                if (cell.getTableRow() == null || cell.getTableRow().getItem() == null || now == null) {
                    cell.setText(null);
                    cell.setCursor(Cursor.DEFAULT);
                    return;
                }
                cell.setCursor(Cursor.HAND);
                if (now.item.key == null) {
                    cell.setText("");
                } else {
                    cell.setText(now.item.key.toString());
                }
            });
            cell.setOnMouseClicked(e -> {
                if (cell.getTableRow().getItem() == null) {
                    return;
                }
                var chooser = new KeyChooser();
                var keyOpt = chooser.choose();
                if (keyOpt.isPresent()) {
                    var key = keyOpt.get();
                    var o = cell.getTableRow().getItem();
                    if (!key.isValid() || (key.key != null && key.key.java == null)) {
                        Logger.debug("unsupported key: " + key.key);
                        SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().unsupportedKeyErrorMessage());
                    } else {
                        o.key = key;
                        cell.setItem(Pointer.of(o));
                        modifiedCallback.run();
                    }
                }
            });
            return cell;
        });

        //noinspection unchecked
        getColumns().addAll(enableColumn, macroColumn, ctrlColumn, altColumn, shiftColumn, keyColumn);
    }
}
