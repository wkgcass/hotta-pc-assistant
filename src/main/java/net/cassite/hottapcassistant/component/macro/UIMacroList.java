package net.cassite.hottapcassistant.component.macro;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import net.cassite.hottapcassistant.component.keybinding.UIKeyChooser;
import net.cassite.hottapcassistant.entity.AssistantMacroData;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Logger;
import net.cassite.hottapcassistant.util.SimpleAlert;

public class UIMacroList extends TableView<AssistantMacroData> {
    public UIMacroList(Runnable modifiedCallback) {
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        ScrollBar hScrollBar = (ScrollBar) lookup(".scroll-bar:horizontal");
        if (hScrollBar != null) {
            hScrollBar.setVisible(false);
        }

        var enableColumn = new TableColumn<AssistantMacroData, Boolean>(I18n.get().macroColumnNameEnable());
        var macroColumn = new TableColumn<AssistantMacroData, String>(I18n.get().macroColumnNameName());
        var ctrlColumn = new TableColumn<AssistantMacroData, Boolean>(I18n.get().hotkeyColumnNameCtrl());
        var altColumn = new TableColumn<AssistantMacroData, Boolean>(I18n.get().hotkeyColumnNameAlt());
        var shiftColumn = new TableColumn<AssistantMacroData, Boolean>(I18n.get().hotkeyColumnNameShift());
        var keyColumn = new TableColumn<AssistantMacroData, String>(I18n.get().hotkeyColumnNameKey());

        enableColumn.setSortable(false);
        enableColumn.setCellValueFactory(f -> new SimpleBooleanProperty(f.getValue().enabled));
        enableColumn.setCellFactory(param -> {
            var cell = new TableCell<AssistantMacroData, Boolean>();
            var checkBox = new CheckBox();
            cell.itemProperty().addListener((o, old, now) -> {
                if (cell.getTableRow() != null) {
                    if (cell.getTableRow().getItem() != null) {
                        cell.setGraphic(checkBox);
                    }
                }
                if (now == null) return;
                checkBox.setSelected(now);
            });
            checkBox.setOnAction(e -> {
                if (cell.getTableRow().getItem() == null) {
                    return;
                }
                cell.getTableRow().getItem().enabled = checkBox.isSelected();
                modifiedCallback.run();
            });
            return cell;
        });
        macroColumn.setSortable(false);
        macroColumn.setMinWidth(100);
        macroColumn.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().name));
        ctrlColumn.setSortable(false);
        ctrlColumn.setCellValueFactory(f -> new SimpleBooleanProperty(f.getValue().ctrl));
        ctrlColumn.setCellFactory(param -> {
            var cell = new TableCell<AssistantMacroData, Boolean>();
            var checkBox = new CheckBox();
            cell.itemProperty().addListener((o, old, now) -> {
                if (cell.getTableRow() != null) {
                    if (cell.getTableRow().getItem() != null) {
                        cell.setGraphic(checkBox);
                    }
                }
                if (now == null) return;
                checkBox.setSelected(now);
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
        altColumn.setCellValueFactory(f -> new SimpleBooleanProperty(f.getValue().alt));
        altColumn.setCellFactory(param -> {
            var cell = new TableCell<AssistantMacroData, Boolean>();
            var checkBox = new CheckBox();
            cell.itemProperty().addListener((o, old, now) -> {
                if (cell.getTableRow() != null) {
                    if (cell.getTableRow().getItem() != null) {
                        cell.setGraphic(checkBox);
                    }
                }
                if (now == null) return;
                checkBox.setSelected(now);
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
        shiftColumn.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue().shift));
        shiftColumn.setCellFactory(param -> {
            var cell = new TableCell<AssistantMacroData, Boolean>();
            var checkBox = new CheckBox();
            cell.itemProperty().addListener((o, old, now) -> {
                if (cell.getTableRow() != null) {
                    if (cell.getTableRow().getItem() != null) {
                        cell.setGraphic(checkBox);
                    }
                }
                if (now == null) return;
                checkBox.setSelected(now);
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
            var cell = new TableCell<AssistantMacroData, String>();
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
                    if (!key.isValid() || (key.key != null && key.key.java == null)) {
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

        //noinspection unchecked
        getColumns().addAll(enableColumn, macroColumn, ctrlColumn, altColumn, shiftColumn, keyColumn);
    }
}
