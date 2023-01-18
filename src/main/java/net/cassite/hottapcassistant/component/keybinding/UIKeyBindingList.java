package net.cassite.hottapcassistant.component.keybinding;

import io.vproxy.vfx.component.keychooser.KeyChooser;
import io.vproxy.vfx.ui.alert.SimpleAlert;
import io.vproxy.vfx.ui.table.VTableColumn;
import io.vproxy.vfx.ui.table.VTableView;
import io.vproxy.vfx.util.Logger;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import net.cassite.hottapcassistant.entity.KeyBinding;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class UIKeyBindingList extends VTableView<KeyBinding> {
    public UIKeyBindingList(Runnable modifiedCallback) {
        var actionColumn = new VTableColumn<KeyBinding, String>(I18n.get().hotkeyColumnNameAction(),
            kb -> I18n.get().inputActionMapping(kb.action));
        var ctrlColumn = new VTableColumn<KeyBinding, KeyBinding>(I18n.get().hotkeyColumnNameCtrl(), kb -> kb);
        var altColumn = new VTableColumn<KeyBinding, KeyBinding>(I18n.get().hotkeyColumnNameAlt(), kb -> kb);
        var shiftColumn = new VTableColumn<KeyBinding, KeyBinding>(I18n.get().hotkeyColumnNameShift(), kb -> kb);
        var keyColumn = new VTableColumn<KeyBinding, KeyBinding>(I18n.get().hotkeyColumnNameKey(), kb -> kb);
        var scaleColumn = new VTableColumn<KeyBinding, KeyBinding>(I18n.get().hotkeyColumnNameScale(), kb -> kb);

        actionColumn.setMinWidth(100);
        actionColumn.setComparator(String::compareTo);
        ctrlColumn.setMaxWidth(80);
        ctrlColumn.setAlignment(Pos.CENTER);
        ctrlColumn.setNodeBuilder(kb -> {
            var checkBox = new CheckBox();
            checkBox.setDisable(kb.isAxis);
            checkBox.setSelected(kb.ctrl);
            checkBox.setOnAction(e -> {
                kb.ctrl = checkBox.isSelected();
                modifiedCallback.run();
            });
            return checkBox;
        });
        altColumn.setMaxWidth(80);
        altColumn.setAlignment(Pos.CENTER);
        altColumn.setNodeBuilder(kb -> {
            var checkBox = new CheckBox();
            checkBox.setDisable(kb.isAxis);
            checkBox.setSelected(kb.alt);
            checkBox.setOnAction(e -> {
                kb.alt = checkBox.isSelected();
                modifiedCallback.run();
            });
            return checkBox;
        });
        shiftColumn.setMaxWidth(80);
        shiftColumn.setAlignment(Pos.CENTER);
        shiftColumn.setNodeBuilder(kb -> {
            var checkBox = new CheckBox();
            checkBox.setDisable(kb.isAxis);
            checkBox.setSelected(kb.shift);
            checkBox.setOnAction(e -> {
                kb.shift = checkBox.isSelected();
                modifiedCallback.run();
            });
            return checkBox;
        });
        keyColumn.setMinWidth(100);
        keyColumn.setAlignment(Pos.CENTER);
        keyColumn.setNodeBuilder(kb -> {
            var cell = new Label();
            cell.setCursor(Cursor.HAND);
            cell.setText(kb.key.toString());
            cell.setOnMouseClicked(e -> {
                var chooser = new KeyChooser();
                var keyOpt = chooser.choose();
                if (keyOpt.isPresent()) {
                    var key = keyOpt.get();
                    if (!key.isValid()) {
                        Logger.debug("unsupported key: " + key.key);
                        SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().unsupportedKeyErrorMessage());
                    } else {
                        kb.key = key;
                        cell.setText(key.toString());
                        modifiedCallback.run();
                    }
                }
            });
            return cell;
        });
        scaleColumn.setMinWidth(100);
        scaleColumn.setNodeBuilder(kb -> {
            if (!kb.isAxis) {
                return new Label();
            }
            var inputBox = new TextField();
            inputBox.setText(Utils.floatValueFormat.format(kb.scale));
            inputBox.setOnAction(e -> {
                var v = inputBox.getText().trim();
                double dv;
                try {
                    dv = Double.parseDouble(v);
                } catch (NumberFormatException ex) {
                    SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().notFloatingPointValue());
                    inputBox.setText(Utils.floatValueFormat.format(kb.scale));
                    return;
                }
                kb.scale = dv;
                modifiedCallback.run();
            });
            return inputBox;
        });

        //noinspection unchecked
        getColumns().addAll(actionColumn, ctrlColumn, altColumn, shiftColumn, keyColumn, scaleColumn);
    }
}
