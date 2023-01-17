package net.cassite.hottapcassistant.component.keybinding;

import io.vproxy.vfx.component.keychooser.KeyChooser;
import io.vproxy.vfx.ui.alert.SimpleAlert;
import io.vproxy.vfx.util.Logger;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import net.cassite.hottapcassistant.entity.KeyBinding;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.ui.Pointer;
import net.cassite.hottapcassistant.util.Utils;

public class UIKeyBindingList extends TableView<KeyBinding> {
    public UIKeyBindingList(Runnable modifiedCallback) {
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        ScrollBar hScrollBar = (ScrollBar) lookup(".scroll-bar:horizontal");
        if (hScrollBar != null) {
            hScrollBar.setVisible(false);
        }

        var actionColumn = new TableColumn<KeyBinding, String>(I18n.get().hotkeyColumnNameAction());
        var ctrlColumn = new TableColumn<KeyBinding, KeyBinding>(I18n.get().hotkeyColumnNameCtrl());
        var altColumn = new TableColumn<KeyBinding, KeyBinding>(I18n.get().hotkeyColumnNameAlt());
        var shiftColumn = new TableColumn<KeyBinding, KeyBinding>(I18n.get().hotkeyColumnNameShift());
        var keyColumn = new TableColumn<KeyBinding, Pointer<KeyBinding>>(I18n.get().hotkeyColumnNameKey());
        var scaleColumn = new TableColumn<KeyBinding, KeyBinding>(I18n.get().hotkeyColumnNameScale());

        actionColumn.setSortable(false);
        actionColumn.setMinWidth(100);
        actionColumn.setCellValueFactory(f -> new SimpleStringProperty(I18n.get().inputActionMapping(f.getValue().action)));
        ctrlColumn.setSortable(false);
        ctrlColumn.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue()));
        ctrlColumn.setCellFactory(param -> {
            var cell = new TableCell<KeyBinding, KeyBinding>();
            cell.itemProperty().addListener((o, old, now) -> {
                if (cell.getTableRow() == null || cell.getTableRow().getItem() == null || now == null) {
                    cell.setGraphic(null);
                    return;
                }
                var row = cell.getTableRow().getItem();
                var checkBox = new CheckBox();
                cell.setGraphic(checkBox);
                checkBox.setDisable(now.isAxis);
                checkBox.setSelected(now.ctrl);
                checkBox.setOnAction(e -> {
                    row.ctrl = checkBox.isSelected();
                    modifiedCallback.run();
                });
            });
            return cell;
        });
        altColumn.setSortable(false);
        altColumn.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue()));
        altColumn.setCellFactory(param -> {
            var cell = new TableCell<KeyBinding, KeyBinding>();
            cell.itemProperty().addListener((o, old, now) -> {
                if (cell.getTableRow() == null || cell.getTableRow().getItem() == null || now == null) {
                    cell.setGraphic(null);
                    return;
                }
                var row = cell.getTableRow().getItem();
                var checkBox = new CheckBox();
                cell.setGraphic(checkBox);
                checkBox.setDisable(now.isAxis);
                checkBox.setSelected(now.alt);
                checkBox.setOnAction(e -> {
                    row.alt = checkBox.isSelected();
                    modifiedCallback.run();
                });
            });
            return cell;
        });
        shiftColumn.setSortable(false);
        shiftColumn.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue()));
        shiftColumn.setCellFactory(param -> {
            var cell = new TableCell<KeyBinding, KeyBinding>();
            cell.itemProperty().addListener((o, old, now) -> {
                if (cell.getTableRow() == null || cell.getTableRow().getItem() == null || now == null) {
                    cell.setGraphic(null);
                    return;
                }
                var row = cell.getTableRow().getItem();
                var checkBox = new CheckBox();
                cell.setGraphic(checkBox);
                checkBox.setDisable(now.isAxis);
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
            var cell = new TableCell<KeyBinding, Pointer<KeyBinding>>();
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
                    if (!key.isValid()) {
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
        scaleColumn.setSortable(false);
        scaleColumn.setMinWidth(100);
        scaleColumn.setCellValueFactory(f -> new SimpleObjectProperty<>(f.getValue()));
        scaleColumn.setCellFactory(param -> {
            var cell = new TableCell<KeyBinding, KeyBinding>();
            cell.itemProperty().addListener((ob, old, now) -> {
                if (cell.getTableRow() == null || cell.getTableRow().getItem() == null || now == null) {
                    cell.setGraphic(null);
                    cell.setText(null);
                    return;
                }
                var row = cell.getTableRow().getItem();
                if (!cell.getTableRow().getItem().isAxis) {
                    cell.setGraphic(null);
                    cell.setText("");
                    return;
                }
                var inputBox = new TextField();
                cell.setGraphic(inputBox);
                cell.setText(null);
                if (!now.isAxis) {
                    inputBox.setText("");
                } else {
                    inputBox.setText(Utils.floatValueFormat.format(now.scale));
                }
                inputBox.setOnAction(e -> {
                    var v = inputBox.getText().trim();
                    double dv;
                    try {
                        dv = Double.parseDouble(v);
                    } catch (NumberFormatException ex) {
                        SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().notFloatingPointValue());
                        inputBox.setText(Utils.floatValueFormat.format(row.scale));
                        return;
                    }
                    row.scale = dv;
                    modifiedCallback.run();
                });
            });
            return cell;
        });

        //noinspection unchecked
        getColumns().addAll(actionColumn, ctrlColumn, altColumn, shiftColumn, keyColumn, scaleColumn);
    }
}
