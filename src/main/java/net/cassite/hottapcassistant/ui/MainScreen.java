package net.cassite.hottapcassistant.ui;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.FontManager;
import net.cassite.hottapcassistant.util.Version;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MainScreen extends BorderPane {
    private record ToolInfo(String name, Supplier<Pane> instantiate, boolean hide) {
    }

    private static final List<ToolInfo> tools = new ArrayList<>();

    static {
        tools.add(new ToolInfo(I18n.get().toolNameWelcome(), WelcomePane::new, false));
        tools.add(new ToolInfo(I18n.get().toolNameGameSettings(), GameSettingsPane::new, false));
        tools.add(new ToolInfo(I18n.get().toolNameInputSettings(), InputSettingsPane::new, false));
        tools.add(new ToolInfo(I18n.get().toolNameMacro(), MacroPane::new, true));
        tools.add(new ToolInfo(I18n.get().toolNameFishing(), FishingPane::new, true));
        tools.add(new ToolInfo(I18n.get().toolNameCoolDown(), CoolDownPane::new, false));
        tools.add(new ToolInfo(I18n.get().toolNameToolBox(), ToolBoxPane::new, true));
        tools.add(new ToolInfo(I18n.get().toolNameAbout(), AboutPane::new, false));
    }

    private record ToolInstance(int index, StackPane pane, Label label, Node content, Separator sep,
                                boolean canBeHidden) {
        public void setVisible(boolean visible, Node center) {
            if (!visible) {
                if (!canBeHidden) return;
                if (content == center) return;
            }
            pane.setVisible(visible);
            sep.setVisible(visible);
            pane.setManaged(visible);
            sep.setManaged(visible);
        }

        public void terminate() {
            if (content instanceof Terminate) {
                ((Terminate) content).terminate();
            }
        }
    }

    private final List<ToolInstance> toolInstances = new ArrayList<>();

    public MainScreen() {
        setBackground(Background.EMPTY);
        VBox leftSideBar = new VBox();
        leftSideBar.setBackground(new Background(
            new BackgroundFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.color(35f / 255, 35f / 255, 39f / 255)),
                new Stop(1, Color.color(75f / 255, 75f / 255, 79f / 255))),
                CornerRadii.EMPTY, Insets.EMPTY)));
        leftSideBar.setMinWidth(200);
        leftSideBar.setMaxWidth(300);
        leftSideBar.setPadding(new Insets(5, 0, 0, 0));
        var isFirst = true;
        for (int i = 0; i < tools.size(); i++) {
            var tool = tools.get(i);
            var pane = new StackPane();
            var sep = new Separator();
            var label = new Label(tool.name());
            var contentNode = tool.instantiate.get();
            var inst = new ToolInstance(i, pane, label, contentNode, sep, tool.hide);
            toolInstances.add(inst);
            if (isFirst) {
                isFirst = false;
                setLabelSelected(inst);
                setCenter(contentNode);
            } else {
                setLabelUnselected(inst);
            }
            FontManager.setFont(label, 18);
            label.setAlignment(Pos.CENTER);

            pane.setCursor(Cursor.HAND);
            pane.setMinHeight(40);
            pane.setAlignment(Pos.CENTER);
            pane.getChildren().add(label);
            leftSideBar.getChildren().add(pane);
            leftSideBar.getChildren().add(sep);
            if (tool.hide) {
                inst.setVisible(false, getCenter());
            }

            pane.setOnMouseClicked(e -> {
                if (getCenter() == null) {
                    if (canEnterTool(inst)) {
                        setCenter(contentNode);
                        inst.setVisible(true, null);
                        setLabelSelected(inst);
                        contentNode.requestFocus();
                    }
                    return;
                }
                if (getCenter() != contentNode) {
                    if (canEnterTool(inst) && exitTool()) {
                        setCenter(contentNode);
                        inst.setVisible(true, null);
                        setLabelSelected(inst);
                        contentNode.requestFocus();
                    } else {
                        hideInactive();
                    }
                }
            });
        }

        setLeft(new HBox(leftSideBar, new Separator(Orientation.VERTICAL)));

        setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ALT) {
                altIsPressed = true;
                for (var inst : toolInstances) {
                    inst.setVisible(true, getCenter());
                }
            }
        });
        setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ALT) {
                altIsPressed = false;
                for (var inst : toolInstances) {
                    inst.setVisible(false, getCenter());
                }
            }
        });
    }

    private boolean altIsPressed = false;

    private boolean canEnterTool(ToolInstance tool) {
        if (tool.content instanceof EnterCheck) {
            return ((EnterCheck) tool.content).enterCheck(altIsPressed);
        } else {
            return true;
        }
    }

    private boolean exitTool() {
        var centerNode = getCenter();
        for (var tool : toolInstances) {
            if (tool.content != centerNode) {
                continue;
            }
            if (exitTool(tool)) {
                setLabelUnselected(tool);
                tool.setVisible(false, null);
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    private boolean exitTool(ToolInstance tool) {
        if (tool.content instanceof ExitCheck) {
            return ((ExitCheck) tool.content).exitCheck();
        } else {
            return true;
        }
    }

    private void hideInactive() {
        var centerNode = getCenter();
        for (var tool : toolInstances) {
            if (tool.content == centerNode) {
                continue;
            }
            tool.setVisible(false, centerNode);
        }
    }

    public void terminate() {
        for (var t : toolInstances) {
            t.terminate();
        }
    }

    private static void setLabelSelected(ToolInstance inst) {
        inst.pane.setBackground(new Background(new BackgroundFill(new Color(0, 0, 0, 1), CornerRadii.EMPTY, Insets.EMPTY)));
        inst.label.setTextFill(new Color(1, 1, 1, 1));
    }

    private static void setLabelUnselected(ToolInstance inst) {
        inst.pane.setBackground(new Background(new BackgroundFill(new Color(1, 1, 1, 1), CornerRadii.EMPTY, Insets.EMPTY)));
        inst.label.setTextFill(new Color(0, 0, 0, 1));
    }

    public static void initStage(Stage stage) {
        var title = I18n.get().titleMainScreen();
        //noinspection ConstantConditions
        if (Version.version.endsWith("-dev")) {
            title = title + " " + I18n.get().titleMainScreenDevVersion();
        }
        stage.setTitle(title);
        stage.setWidth(1024);
        stage.setHeight(768);
        stage.centerOnScreen();
    }
}
