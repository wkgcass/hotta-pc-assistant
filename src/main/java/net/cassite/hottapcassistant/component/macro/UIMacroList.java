package net.cassite.hottapcassistant.component.macro;

import io.vproxy.vfx.component.keychooser.KeyChooser;
import io.vproxy.vfx.ui.alert.SimpleAlert;
import io.vproxy.vfx.ui.table.VTableColumn;
import io.vproxy.vfx.ui.table.VTableView;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import io.vproxy.vfx.util.Logger;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import net.cassite.hottapcassistant.entity.AssistantMacroData;
import net.cassite.hottapcassistant.i18n.I18n;

public class UIMacroList extends VTableView<AssistantMacroData> {
    public UIMacroList(Runnable modifiedCallback) {
        var enableColumn = new VTableColumn<AssistantMacroData, AssistantMacroData>(I18n.get().macroColumnNameEnable(), kb -> kb);
        var macroColumn = new VTableColumn<AssistantMacroData, String>(I18n.get().macroColumnNameName(), kb -> kb.name);
        var ctrlColumn = new VTableColumn<AssistantMacroData, AssistantMacroData>(I18n.get().hotkeyColumnNameCtrl(), kb -> kb);
        var altColumn = new VTableColumn<AssistantMacroData, AssistantMacroData>(I18n.get().hotkeyColumnNameAlt(), kb -> kb);
        var shiftColumn = new VTableColumn<AssistantMacroData, AssistantMacroData>(I18n.get().hotkeyColumnNameShift(), kb -> kb);
        var keyColumn = new VTableColumn<AssistantMacroData, AssistantMacroData>(I18n.get().hotkeyColumnNameKey(), kb -> kb);

        enableColumn.setMaxWidth(80);
        enableColumn.setAlignment(Pos.CENTER);
        enableColumn.setNodeBuilder(kb -> {
            var checkBox = new CheckBox();
            FXUtils.disableFocusColor(checkBox);
            checkBox.setSelected(kb.enabled);
            checkBox.setOnAction(e -> {
                kb.enabled = checkBox.isSelected();
                modifiedCallback.run();
            });
            return checkBox;
        });
        macroColumn.setMinWidth(100);
        ctrlColumn.setMaxWidth(80);
        ctrlColumn.setAlignment(Pos.CENTER);
        ctrlColumn.setNodeBuilder(kb -> {
            var checkBox = new CheckBox();
            FXUtils.disableFocusColor(checkBox);
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
            FXUtils.disableFocusColor(checkBox);
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
            FXUtils.disableFocusColor(checkBox);
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
            var label = new ThemeLabel();
            label.setCursor(Cursor.HAND);
            if (kb.key == null) {
                label.setText("");
            } else {
                label.setText(kb.key.toString());
            }
            label.setOnMouseClicked(e -> {
                var chooser = new KeyChooser();
                var keyOpt = chooser.choose();
                if (keyOpt.isPresent()) {
                    var key = keyOpt.get();
                    if (!key.isValid() || (key.key != null && key.key.java == null)) {
                        Logger.debug("unsupported key: " + key.key);
                        SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().unsupportedKeyErrorMessage());
                    } else {
                        kb.key = key;
                        label.setText(key.toString());
                        modifiedCallback.run();
                    }
                }
            });
            return label;
        });

        //noinspection unchecked
        getColumns().addAll(enableColumn, macroColumn, ctrlColumn, altColumn, shiftColumn, keyColumn);
    }
}
