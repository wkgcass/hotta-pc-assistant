package net.cassite.hottapcassistant.ui;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
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
    public record ToolInfo(String name, Supplier<Pane> instantiate) {
    }

    private static final List<ToolInfo> tools = new ArrayList<>();

    static {
        tools.add(new ToolInfo(I18n.get().toolNameWelcome(), WelcomePane::new));
        tools.add(new ToolInfo(I18n.get().toolNameGameSettings(), GameSettingsPane::new));
        tools.add(new ToolInfo(I18n.get().toolNameInputSettings(), InputSettingsPane::new));
        tools.add(new ToolInfo(I18n.get().toolNameMacro(), MacroPane::new));
        tools.add(new ToolInfo(I18n.get().toolNameFishing(), FishingPane::new));
        tools.add(new ToolInfo(I18n.get().toolNameAbout(), AboutPane::new));
    }

    private record ToolInstance(int index, StackPane pane, Label label, Node content) {
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
            var label = new Label(tool.name());
            var contentNode = tool.instantiate.get();
            var inst = new ToolInstance(i, pane, label, contentNode);
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
            leftSideBar.getChildren().add(new Separator());

            pane.setOnMouseClicked(e -> {
                if (getCenter() == null) {
                    if (canEnterTool(inst)) {
                        setCenter(contentNode);
                        setLabelSelected(inst);
                    }
                    return;
                }
                if (getCenter() != contentNode) {
                    if (canEnterTool(inst) && exitTool()) {
                        setCenter(contentNode);
                        setLabelSelected(inst);
                    }
                }
            });
        }

        setLeft(new HBox(leftSideBar, new Separator(Orientation.VERTICAL)));
    }

    private boolean canEnterTool(ToolInstance tool) {
        if (tool.content instanceof EnterCheck) {
            return ((EnterCheck) tool.content).enterCheck();
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
