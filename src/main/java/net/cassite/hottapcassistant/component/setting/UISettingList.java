package net.cassite.hottapcassistant.component.setting;

import io.vproxy.vfx.ui.alert.SimpleAlert;
import io.vproxy.vfx.ui.table.VTableColumn;
import io.vproxy.vfx.ui.table.VTableView;
import io.vproxy.vfx.ui.wrapper.FusionW;
import io.vproxy.vfx.util.FXUtils;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import net.cassite.hottapcassistant.i18n.I18n;

public class UISettingList extends VTableView<Setting> {
    private final Runnable modifiedCallback;

    public UISettingList(Runnable modifiedCallback) {
        this.modifiedCallback = modifiedCallback;

        var nameColumn = new VTableColumn<Setting, String>(I18n.get().settingColumnNameName(),
            s -> I18n.get().configNameMapping(s.name));
        var valueColumn = new VTableColumn<Setting, Setting>(I18n.get().settingColumnNameValue(), s -> s);

        nameColumn.setComparator(String::compareTo);
        nameColumn.setMaxWidth(250);
        nameColumn.setAlignment(Pos.CENTER_RIGHT);
        valueColumn.setNodeBuilder(this::generateNode);

        //noinspection unchecked
        getColumns().addAll(nameColumn, valueColumn);
    }

    private Node generateNode(Setting setting) {
        return switch (setting.type) {
            case INT, FLOAT -> {
                var input = new TextField(setting.formatValue());
                boolean[] modified = new boolean[]{false};
                Runnable applyHandler = () -> {
                    if (!modified[0]) {
                        return;
                    }
                    Platform.runLater(() -> modified[0] = false);
                    String text = input.getText();
                    Object v;
                    try {
                        if (setting.type == SettingType.INT) {
                            v = Integer.parseInt(text);
                        } else {
                            v = Double.parseDouble(text);
                        }
                    } catch (NumberFormatException ex) {
                        SimpleAlert.show(Alert.AlertType.INFORMATION, I18n.get().invalidNumberValue());
                        input.setText(setting.formatValue());
                        return;
                    }
                    if (setting.additionalCheck != null) {
                        if (!setting.additionalCheck.apply(v)) {
                            input.setText(setting.formatValue());
                            return;
                        }
                    }
                    setting.value = v;
                    modifiedCallback.run();
                };
                input.textProperty().addListener((ob, old, now) -> modified[0] = true);
                input.setOnMouseExited(e -> applyHandler.run());
                yield new FusionW(input);
            }
            case BOOL, BOOL_0_1 -> {
                var box = new CheckBox();
                FXUtils.disableFocusColor(box);
                box.setSelected((boolean) setting.value);
                box.setOnAction(e -> {
                    setting.value = box.isSelected();
                    modifiedCallback.run();
                });
                yield box;
            }
            case RESOLUTION -> {
                var box = new ComboBox<String>();
                FXUtils.disableFocusColor(box);
                box.setEditable(true);
                box.setValue((String) setting.value);
                box.getItems().addAll(
                    "1024x768",
                    "1280x720",
                    "1152x720", // 0.9 * 1280x800
                    "1280x800",
                    "1366x768",
                    "1728x972", // 0.9 * 1920x1080
                    "1920x1080",
                    "1728x1080", // 0.9 * 1920x1200
                    "1920x1200", // 16:10
                    "2304x1296", // 0.9 * 2560x1440(2K)
                    "2560x1440", // 2K
                    "3456x1944", // 0.9 * 3840x2160(4K)
                    "3840x2160", // 4K
                    "-1x-1" // fullscreen
                );
                boolean[] modified = new boolean[]{false};
                Runnable applyHandler = () -> {
                    if (!modified[0]) {
                        return;
                    }
                    Platform.runLater(() -> modified[0] = false);
                    String v = box.getValue();
                    if (!v.contains("x")) {
                        SimpleAlert.show(Alert.AlertType.INFORMATION, I18n.get().invalidResolutionValue());
                        box.setValue((String) setting.value);
                        return;
                    }
                    String[] split = v.split("x");
                    if (split.length != 2) {
                        SimpleAlert.show(Alert.AlertType.INFORMATION, I18n.get().invalidResolutionValue());
                        box.setValue((String) setting.value);
                        return;
                    }
                    int x;
                    int y;
                    try {
                        x = Integer.parseInt(split[0]);
                        y = Integer.parseInt(split[1]);
                    } catch (NumberFormatException ex) {
                        SimpleAlert.show(Alert.AlertType.INFORMATION, I18n.get().invalidResolutionIntegerValue());
                        box.setValue((String) setting.value);
                        return;
                    }
                    if (x == -1 && y == -1) {
                        setting.value = "-1x-1";
                        return;
                    }
                    if (x <= 0 || y <= 0) {
                        SimpleAlert.show(Alert.AlertType.INFORMATION, I18n.get().invalidResolutionIntegerValue());
                        box.setValue((String) setting.value);
                        return;
                    }
                    setting.value = x + "x" + y;
                    modifiedCallback.run();
                };
                box.valueProperty().addListener((ob, old, now) -> modified[0] = true);
                box.setOnMouseExited(e -> applyHandler.run());
                yield box;
            }
        };
    }
}
