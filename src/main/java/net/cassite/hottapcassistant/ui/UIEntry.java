package net.cassite.hottapcassistant.ui;

import io.vproxy.vfx.manager.image.ImageManager;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.button.FusionImageButton;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.scene.VScene;
import io.vproxy.vfx.ui.scene.VSceneHideMethod;
import io.vproxy.vfx.ui.scene.VSceneRole;
import io.vproxy.vfx.ui.scene.VSceneShowMethod;
import io.vproxy.vfx.ui.stage.VStage;
import javafx.geometry.Insets;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import net.cassite.hottapcassistant.i18n.I18n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class UIEntry {
    public final List<MainScene> mainScenes = Arrays.asList(
        new WelcomeScene(),
        new GameSettingsScene(),
        new InputSettingsScene(),
        new MacroScene(),
        new FishingScene(),
        new CoolDownScene()
        // TODO
    );
    private final VStage stage;
    private final VScene menuScene;

    public UIEntry(VStage stage) {
        this.stage = stage;

        var sceneGroup = stage.getSceneGroup();
        for (var scene : mainScenes) {
            sceneGroup.addScene(scene);
        }

        menuScene = new VScene(VSceneRole.DRAWER_VERTICAL);
        menuScene.getNode().setPrefWidth(300);
        menuScene.enableAutoContentWidth();
        menuScene.getNode().setBackground(new Background(new BackgroundFill(
            Theme.current().subSceneBackgroundColor(),
            CornerRadii.EMPTY,
            Insets.EMPTY
        )));
        stage.getRootSceneGroup().addScene(menuScene, VSceneHideMethod.TO_LEFT);
        var menuVBox = new VBox() {{
            setPadding(new Insets(0, 0, 0, 24));
            getChildren().add(new VPadding(20));
            setSpacing(10);
        }};
        menuScene.getContentPane().getChildren().add(menuVBox);
        for (int i = 0; i < mainScenes.size(); ++i) {
            final var fi = i;
            var s = mainScenes.get(i);
            var title = s.title();
            var button = s.menuButton;
            if (i == 0) {
                button.setDisable(true);
            }
            button.setText(title);
            button.setDisableAnimation(true);
            button.setOnAction(e -> switchScene(fi));
            button.setPrefWidth(250);
            button.setPrefHeight(40);
            menuVBox.getChildren().add(button);
        }
        menuVBox.getChildren().add(new VPadding(20));

        var menuBtn = new FusionImageButton(ImageManager.get().load("/images/icon/menu.png:white")) {{
            setPrefWidth(40);
            setPrefHeight(VStage.TITLE_BAR_HEIGHT + 1);
            getImageView().setFitHeight(15);
            setLayoutX(-2);
            setLayoutY(-1);
        }};
        stage.getRoot().getContentPane().getChildren().add(menuBtn);
        menuBtn.setOnAction(e -> stage.getRootSceneGroup().show(menuScene, VSceneShowMethod.FROM_LEFT));

        menuScene.getContentPane().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ALT) {
                altIsPressed = true;
                for (var scene : mainScenes) {
                    scene.setVisible(true, sceneGroup.getNextOrCurrentMainScene());
                }
            }
        });
        menuScene.getContentPane().setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ALT) {
                altIsPressed = false;
                for (var scene : mainScenes) {
                    scene.setVisible(false, sceneGroup.getNextOrCurrentMainScene());
                }
            }
        });
    }

    private void switchScene(int switchToIndex) {
        var s = mainScenes.get(switchToIndex);
        var sceneGroup = stage.getSceneGroup();
        if (sceneGroup.getCurrentMainScene() != s) {
            if (canEnterTool(s) && exitTool()) {
                showScene(switchToIndex);
                setSceneSelected(s);
                s.getNode().requestFocus();
            }
            hideInactive();
        }
    }

    private void showScene(int switchToIndex) {
        var sceneGroup = stage.getSceneGroup();
        var s = mainScenes.get(switchToIndex);

        //noinspection SuspiciousMethodCalls
        var currentIndex = mainScenes.indexOf(sceneGroup.getCurrentMainScene());
        if (currentIndex != switchToIndex) {
            sceneGroup.show(s, currentIndex < switchToIndex ? VSceneShowMethod.FROM_BOTTOM : VSceneShowMethod.FROM_TOP);
        }
        stage.getRootSceneGroup().hide(menuScene, VSceneHideMethod.TO_LEFT);
    }

    private record ToolInfo(String name, Supplier<Pane> instantiate, boolean hide) {
    }

    private static final List<ToolInfo> tools = new ArrayList<>();

    static {
        // tools.add(new ToolInfo(I18n.get().toolNameWelcome(), WelcomePane::new, false));
        // tools.add(new ToolInfo(I18n.get().toolNameGameSettings(), GameSettingsPane::new, false));
        // tools.add(new ToolInfo(I18n.get().toolNameInputSettings(), InputSettingsPane::new, false));
        // tools.add(new ToolInfo(I18n.get().toolNameMacro(), MacroPane::new, true));
        // tools.add(new ToolInfo(I18n.get().toolNameFishing(), FishingPane::new, true));
        // tools.add(new ToolInfo(I18n.get().toolNameCoolDown(), CoolDownPane::new, false));
        tools.add(new ToolInfo(I18n.get().toolNameToolBox(), ToolBoxPane::new, false));
        tools.add(new ToolInfo(I18n.get().toolNameAbout(), AboutPane::new, false));
    }

    private boolean altIsPressed = false;

    private boolean canEnterTool(VScene scene) {
        if (scene instanceof EnterCheck) {
            return ((EnterCheck) scene).enterCheck(altIsPressed);
        } else {
            return true;
        }
    }

    private boolean exitTool() {
        var sceneGroup = stage.getSceneGroup();
        var currentScene = sceneGroup.getCurrentMainScene();
        if (exitTool(currentScene)) {
            setSceneUnselected((MainScene) currentScene);
            return true;
        } else {
            return false;
        }
    }

    private boolean exitTool(VScene scene) {
        if (scene instanceof ExitCheck) {
            return ((ExitCheck) scene).exitCheck();
        } else {
            return true;
        }
    }

    public void hideInactive() {
        var currentScene = stage.getSceneGroup().getNextOrCurrentMainScene();
        for (var s : mainScenes) {
            if (s == currentScene) {
                continue;
            }
            s.setVisible(false, currentScene);
        }
    }

    private void setSceneSelected(MainScene inst) {
        inst.menuButton.setDisable(true);
        inst.setVisible(true, null);
    }

    private void setSceneUnselected(MainScene inst) {
        inst.menuButton.setDisable(false);
        inst.setVisible(false, null);
    }
}
