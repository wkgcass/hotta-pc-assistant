package net.cassite.hottapcassistant.component.keybinding;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import net.cassite.hottapcassistant.entity.KeyBinding;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Logger;
import net.cassite.hottapcassistant.util.SimpleAlert;
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
        var keyColumn = new TableColumn<KeyBinding, String>(I18n.get().hotkeyColumnNameKey());
        var scaleColumn = new TableColumn<KeyBinding, String>(I18n.get().hotkeyColumnNameScale());

        actionColumn.setSortable(false);
        actionColumn.setMinWidth(100);
        actionColumn.setCellValueFactory(f -> new SimpleStringProperty(I18n.get().inputActionMapping(f.getValue().action)));
        ctrlColumn.setSortable(false);
        ctrlColumn.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue()));
        ctrlColumn.setCellFactory(param -> {
            var cell = new TableCell<KeyBinding, KeyBinding>();
            var checkBox = new CheckBox();
            cell.itemProperty().addListener((o, old, now) -> {
                if (cell.getTableRow() != null) {
                    if (cell.getTableRow().getItem() != null) {
                        cell.setGraphic(checkBox);
                    }
                }
                if (now == null) return;
                checkBox.setDisable(now.isAxis);
                checkBox.setSelected(now.ctrl);
            });
            checkBox.setOnAction(e -> {
                if (cell.getTableRow().getItem() == null) {
                    return;
                }
                cell.getTableRow().getItem().ctrl = checkBox.isSelected();
                modifiedCallback.run();
            });
            return cell;
        });
        altColumn.setSortable(false);
        altColumn.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue()));
        altColumn.setCellFactory(param -> {
            var cell = new TableCell<KeyBinding, KeyBinding>();
            var checkBox = new CheckBox();
            cell.itemProperty().addListener((o, old, now) -> {
                if (cell.getTableRow() != null) {
                    if (cell.getTableRow().getItem() != null) {
                        cell.setGraphic(checkBox);
                    }
                }
                if (now == null) return;
                checkBox.setDisable(now.isAxis);
                checkBox.setSelected(now.alt);
            });
            checkBox.setOnAction(e -> {
                if (cell.getTableRow().getItem() == null) {
                    return;
                }
                cell.getTableRow().getItem().alt = checkBox.isSelected();
                modifiedCallback.run();
            });
            return cell;
        });
        shiftColumn.setSortable(false);
        shiftColumn.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue()));
        shiftColumn.setCellFactory(param -> {
            var cell = new TableCell<KeyBinding, KeyBinding>();
            var checkBox = new CheckBox();
            cell.itemProperty().addListener((o, old, now) -> {
                if (cell.getTableRow() != null) {
                    if (cell.getTableRow().getItem() != null) {
                        cell.setGraphic(checkBox);
                    }
                }
                if (now == null) return;
                checkBox.setDisable(now.isAxis);
                checkBox.setSelected(now.shift);
            });
            checkBox.setOnAction(e -> {
                if (cell.getTableRow().getItem() == null) {
                    return;
                }
                cell.getTableRow().getItem().shift = checkBox.isSelected();
                modifiedCallback.run();
            });
            return cell;
        });
        keyColumn.setSortable(false);
        keyColumn.setMinWidth(100);
        keyColumn.setCellValueFactory(f -> {
            if (f.getValue().key == null) {
                return new SimpleStringProperty("");
            } else {
                return new SimpleStringProperty(f.getValue().key.toString());
            }
        });
        keyColumn.setCellFactory(param -> {
            var cell = new TableCell<KeyBinding, String>();
            cell.setAlignment(Pos.CENTER);
            cell.itemProperty().addListener((o, old, now) -> {
                if (cell.getTableRow() != null) {
                    if (cell.getTableRow().getItem() != null) {
                        cell.setCursor(Cursor.HAND);
                    }
                }
                if (now == null || now.isEmpty()) {
                    cell.setText("");
                } else {
                    cell.setText(now);
                }
            });
            cell.setOnMouseClicked(e -> {
                if (cell.getTableRow().getItem() == null) {
                    return;
                }
                var chooser = new UIKeyChooser();
                var keyOpt = chooser.choose();
                if (keyOpt.isPresent()) {
                    var key = keyOpt.get();
                    var o = cell.getTableRow().getItem();
                    if (!key.isValid()) {
                        Logger.debug("unsupported key: " + key.key);
                        new SimpleAlert(Alert.AlertType.ERROR, I18n.get().unsupportedKeyErrorMessage()).showAndWait();
                    } else {
                        o.key = key;
                        cell.setItem(key.toString());
                        modifiedCallback.run();
                    }
                }
            });
            return cell;
        });
        scaleColumn.setSortable(false);
        scaleColumn.setMinWidth(100);
        scaleColumn.setCellValueFactory(f -> {
            var result = "";
            if (f.getValue().isAxis) {
                result = Utils.floatValueFormat.format(f.getValue().scale);
            }
            return new SimpleStringProperty(result);
        });
        scaleColumn.setCellFactory(param -> {
            var cell = new TableCell<KeyBinding, String>();
            var inputBox = new TextField();
            cell.itemProperty().addListener((o, old, now) -> {
                if (cell.getTableRow() != null) {
                    if (cell.getTableRow().getItem() != null) {
                        if (cell.getTableRow().getItem().isAxis) {
                            cell.setGraphic(inputBox);
                            cell.setText(null);
                        } else {
                            cell.setGraphic(null);
                            cell.setText("");
                        }
                    }
                }
                if (now == null || now.isEmpty()) {
                    inputBox.setText("");
                } else {
                    inputBox.setText(now);
                }
            });
            inputBox.setOnAction(e -> {
                if (cell.getTableRow().getItem() == null) {
                    return;
                }
                var o = cell.getTableRow().getItem();
                var v = inputBox.getText().trim();
                double dv;
                try {
                    dv = Double.parseDouble(v);
                } catch (NumberFormatException ex) {
                    new SimpleAlert(Alert.AlertType.ERROR, I18n.get().notFloatingPointValue()).showAndWait();
                    inputBox.setText(Utils.floatValueFormat.format(o.scale));
                    return;
                }
                o.scale = dv;
                modifiedCallback.run();
            });
            return cell;
        });

        //noinspection unchecked
        getColumns().addAll(actionColumn, ctrlColumn, altColumn, shiftColumn, keyColumn, scaleColumn);
    }
}
