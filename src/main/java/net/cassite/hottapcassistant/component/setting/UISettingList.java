package net.cassite.hottapcassistant.component.setting;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.*;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.SimpleAlert;

import java.util.Objects;

public class UISettingList extends TableView<Setting> {
    private final Runnable modifiedCallback;

    public UISettingList(Runnable modifiedCallback) {
        this.modifiedCallback = modifiedCallback;
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        ScrollBar hScrollBar = (ScrollBar) lookup(".scroll-bar:horizontal");
        if (hScrollBar != null) {
            hScrollBar.setVisible(false);
        }
        var nameColumn = new TableColumn<Setting, String>(I18n.get().settingColumnNameName());
        var valueColumn = new TableColumn<Setting, Setting>(I18n.get().settingColumnNameValue());

        nameColumn.setSortable(false);
        nameColumn.setCellValueFactory(f -> new SimpleStringProperty(I18n.get().configNameMapping(f.getValue().name)));
        valueColumn.setSortable(false);
        valueColumn.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue()));
        valueColumn.setCellFactory(param -> {
            var cell = new TableCell<Setting, Setting>();
            cell.itemProperty().addListener((o, old, now) -> {
                if (cell.getTableRow() == null || cell.getTableRow().getItem() == null) {
                    return;
                }
                if (now == null) return;
                now.node = generateNode(now);
                cell.setGraphic(now.node);
            });
            return cell;
        });

        //noinspection unchecked
        getColumns().addAll(nameColumn, valueColumn);
    }

    private Node generateNode(Setting setting) {
        return switch (setting.type) {
            case INT, FLOAT -> {
                var input = new TextField(setting.formatValue());
                Runnable applyHandler = () -> {
                    String text = input.getText();
                    Object v;
                    try {
                        if (setting.type == SettingType.INT) {
                            v = Integer.parseInt(text);
                        } else {
                            v = Double.parseDouble(text);
                        }
                    } catch (NumberFormatException ex) {
                        new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().invalidNumberValue()).showAndWait();
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
                input.focusedProperty().addListener((ob, old, now) -> {
                    if (now == null) return;
                    if (Objects.equals(old, now)) return;
                    if (!now) applyHandler.run();
                });
                input.setOnAction(e -> applyHandler.run());
                yield input;
            }
            case BOOL, BOOL_0_1 -> {
                var box = new CheckBox();
                box.setSelected((boolean) setting.value);
                box.setOnAction(e -> {
                    setting.value = box.isSelected();
                    modifiedCallback.run();
                });
                yield box;
            }
            case RESOLUTION -> {
                var box = new ComboBox<String>();
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
                Runnable applyHandler = () -> {
                    String v = box.getValue();
                    if (!v.contains("x")) {
                        new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().invalidResolutionValue()).showAndWait();
                        box.setValue((String) setting.value);
                        return;
                    }
                    String[] split = v.split("x");
                    if (split.length != 2) {
                        new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().invalidResolutionValue()).showAndWait();
                        box.setValue((String) setting.value);
                        return;
                    }
                    int x;
                    int y;
                    try {
                        x = Integer.parseInt(split[0]);
                        y = Integer.parseInt(split[1]);
                    } catch (NumberFormatException ex) {
                        new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().invalidResolutionIntegerValue()).showAndWait();
                        box.setValue((String) setting.value);
                        return;
                    }
                    if (x == -1 && y == -1) {
                        setting.value = "-1x-1";
                        return;
                    }
                    if (x <= 0 || y <= 0) {
                        new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().invalidResolutionIntegerValue()).showAndWait();
                        box.setValue((String) setting.value);
                        return;
                    }
                    setting.value = x + "x" + y;
                    modifiedCallback.run();
                };
                box.focusedProperty().addListener((ob, old, now) -> {
                    if (now == null) return;
                    if (Objects.equals(old, now)) return;
                    if (!now) applyHandler.run();
                });
                box.setOnAction(e -> applyHandler.run());
                yield box;
            }
        };
    }
}
