package net.cassite.hottapcassistant.ui;

import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.layout.HPadding;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.pane.FusionPane;
import io.vproxy.vfx.ui.scene.VScene;
import io.vproxy.vfx.ui.scene.VSceneGroup;
import io.vproxy.vfx.ui.scene.VSceneHideMethod;
import io.vproxy.vfx.ui.scene.VSceneShowMethod;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.status.Status;
import net.cassite.hottapcassistant.status.StatusComponent;
import net.cassite.hottapcassistant.status.StatusEnum;
import net.cassite.hottapcassistant.status.StatusManager;
import net.cassite.hottapcassistant.tool.*;
import net.cassite.hottapcassistant.util.GlobalValues;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ToolBoxScene extends AbstractMainScene implements Terminate {
    private static final List<Supplier<Tool>> tools = new ArrayList<>() {{
        add(WorldBossTimer::new);
        add(MessageHelper::new);
        add(LansBrainWash::new);
        add(MultiHottaInstance::new);
        add(StatusIndicator::new);
        add(MessageMonitor::new);
        add(PatchManager::new);
    }};
    private static final int colsPerLine = 6;
    private final List<Tool> toolInstances = new ArrayList<>();
    private final VSceneGroup sceneGroup;

    public ToolBoxScene(VSceneGroup sceneGroup) {
        this.sceneGroup = sceneGroup;
        var gridPane = new GridPane();
        getContentPane().getChildren().add(new VBox(
            new VPadding(50),
            new HBox(new HPadding(100), gridPane)
        ));

        initTools(gridPane);
    }

    @Override
    public String title() {
        return I18n.get().toolNameToolBox();
    }

    private void initTools(GridPane grid) {
        grid.setHgap(60);
        grid.setVgap(30);
        var i = 0;
        for (var tool : tools) {
            var t = tool.get();
            toolInstances.add(t);

            var icon = t.getIcon();
            var name = t.getName();

            var imageView = new ImageView(icon);
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);

            var label = new ThemeLabel(name);

            var runningDot = new Circle(8);
            runningDot.setStrokeWidth(2);
            runningDot.setStroke(Theme.current().borderColor());
            runningDot.setFill(Theme.current().progressBarProgressColor());
            runningDot.setVisible(false);
            runningDot.setLayoutX(80);
            runningDot.setLayoutY(20);

            var vbox = new VBox();
            vbox.setAlignment(Pos.CENTER);
            vbox.getChildren().addAll(
                new Pane(imageView, runningDot) {{
                    setPrefWidth(imageView.getFitWidth());
                    setPrefHeight(imageView.getFitHeight());
                }},
                new VPadding(2),
                label
            );

            vbox.setOnMouseClicked(e -> openTool(t, runningDot));
            vbox.setCursor(Cursor.HAND);

            grid.add(new FusionPane(false, vbox).getContentPane(), i % colsPerLine, i / colsPerLine);
            ++i;
        }
    }

    private void openTool(Tool tool, Circle runningDot) {
        if (tool.isRunning()) {
            sceneGroup.show(tool.getScene(), VSceneShowMethod.FROM_RIGHT);
            GlobalValues.setBackFunction(() -> sceneGroup.hide(tool.getScene(), VSceneHideMethod.TO_RIGHT));
            GlobalValues.setCloseFunction(tool::terminate);
            return;
        }
        if (tool instanceof EnterCheck) {
            var checkPass = ((EnterCheck) tool).enterCheck(false);
            if (!checkPass) return;
        }
        tool.launch();
        runningDot.setVisible(true);
        var scene = tool.getScene();
        sceneGroup.addScene(scene, VSceneHideMethod.TO_RIGHT);
        sceneGroup.show(scene, VSceneShowMethod.FADE_IN);
        GlobalValues.setBackFunction(() -> sceneGroup.hide(tool.getScene(), VSceneHideMethod.TO_RIGHT));
        GlobalValues.setCloseFunction(tool::terminate);
        tool.setOnTerminated(() -> {
            runningDot.setVisible(false);
            if (sceneGroup.isShowing(scene)) {
                sceneGroup.hide(scene, VSceneHideMethod.FADE_OUT);
                FXUtils.runDelay(VScene.ANIMATION_DURATION_MILLIS, () -> sceneGroup.removeScene(scene));
            } else {
                sceneGroup.removeScene(scene);
            }
            StatusManager.get().removeStatus(new Status(tool.getName(), StatusComponent.TOOL, StatusEnum.STOPPED));
        });
        StatusManager.get().updateStatus(new Status(tool.getName(), StatusComponent.TOOL, StatusEnum.RUNNING));
    }

    @Override
    public void terminate() {
        for (var t : toolInstances) {
            t.terminate();
        }
    }
}
