package net.cassite.hottapcassistant.ui;

import io.vproxy.base.util.LogType;
import io.vproxy.base.util.Logger;
import io.vproxy.vfx.control.dialog.VDialog;
import io.vproxy.vfx.control.dialog.VDialogButton;
import io.vproxy.vfx.manager.image.ImageManager;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.button.FusionImageButton;
import io.vproxy.vfx.ui.button.TransparentFusionImageButton;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.scene.VScene;
import io.vproxy.vfx.ui.scene.VSceneHideMethod;
import io.vproxy.vfx.ui.scene.VSceneRole;
import io.vproxy.vfx.ui.scene.VSceneShowMethod;
import io.vproxy.vfx.ui.stage.VStage;
import io.vproxy.vfx.util.FXUtils;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import net.cassite.hottapcassistant.config.AssistantConfig;
import net.cassite.hottapcassistant.entity.Assistant;
import net.cassite.hottapcassistant.feed.Feed;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.GlobalValues;

import java.util.*;

public class UIEntry {
    public final List<MainScene> mainScenes;
    private final Map<VScene, MainScene> sceneMap = new HashMap<>();
    private final VStage stage;
    private final VScene menuScene;
    private final Group normalButtonGroup = new Group() {{
        setManaged(false);
        setVisible(false);
    }};
    private final Group transparentButtonGroup = new Group();

    public UIEntry(VStage stage) {
        this.stage = stage;
        mainScenes = Arrays.asList(
            new WelcomeScene(),
            new GameSettingsScene(),
            new InputSettingsScene(),
            new MacroScene(),
            new FishingScene(stage),
            new CoolDownScene(stage.getSceneGroup()),
            new ToolBoxScene(stage.getSceneGroup()),
            new XBoxScene(stage.getSceneGroup()),
            new AboutScene(),
            new LogScene(),
            new ResetScene()
        );
        for (var s : mainScenes) {
            sceneMap.put(s.getScene(), s);
        }

        var sceneGroup = stage.getSceneGroup();
        for (var scene : mainScenes) {
            sceneGroup.addScene(scene.getScene());
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
            var button = s.getMenuButton();
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
        }};
        menuBtn.setOnAction(e -> {
            GlobalValues.callBackFunction();
            stage.getRootSceneGroup().show(menuScene, VSceneShowMethod.FROM_LEFT);
        });

        var transparentMenuBtn = new TransparentFusionImageButton(ImageManager.get().load("/images/icon/menu.png")) {{
            setPrefWidth(menuBtn.getPrefWidth());
            setPrefHeight(menuBtn.getPrefHeight());
            getImageView().setFitHeight(menuBtn.getImageView().getFitHeight());
        }};
        transparentMenuBtn.setOnAction(menuBtn.getOnAction());

        normalButtonGroup.getChildren().add(menuBtn);
        transparentButtonGroup.getChildren().add(transparentMenuBtn);

        var backBtn = new FusionImageButton(ImageManager.get().load("/images/icon/return.png:white")) {{
            setPrefWidth(40);
            setPrefHeight(VStage.TITLE_BAR_HEIGHT + 1);
            getImageView().setFitHeight(15);
            setVisible(false);
        }};
        backBtn.setOnAction(e -> GlobalValues.callBackFunction());
        GlobalValues.backButton = backBtn;

        var closeBtn = new FusionImageButton(ImageManager.get().load("/io/vproxy/vfx/res/image/close.png:white")) {{
            setPrefWidth(40);
            setPrefHeight(VStage.TITLE_BAR_HEIGHT + 1);
            getImageView().setFitHeight(12);
            setVisible(false);
        }};
        closeBtn.setOnAction(e -> GlobalValues.callCloseFunction());
        GlobalValues.closeButton = closeBtn;

        var hbox = new HBox();
        hbox.setSpacing(5);
        hbox.setLayoutY(-1);
        hbox.getChildren().addAll(normalButtonGroup, transparentButtonGroup, backBtn, closeBtn);
        stage.getRoot().getContentPane().getChildren().add(hbox);

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
        var current = sceneMap.get(stage.getSceneGroup().getCurrentMainScene());
        assert current != null;
        if (current != s) {
            if (canEnterTool(s) && exitTool()) {
                showScene(switchToIndex);
                setSceneSelected(s);
                s.getScene().getNode().requestFocus();

                if (s instanceof WelcomeScene) {
                    configureRootCorrespondToWelcomeScene();
                } else {
                    FXUtils.runDelay(VScene.ANIMATION_DURATION_MILLIS, this::configureRootCorrespondToNormalScene);
                }
            }
            hideInactive();
        }
    }

    private void showScene(int switchToIndex) {
        var sceneGroup = stage.getSceneGroup();
        var s = mainScenes.get(switchToIndex);

        var current = sceneMap.get(sceneGroup.getCurrentMainScene());
        assert current != null;
        var currentIndex = mainScenes.indexOf(current);
        if (currentIndex != switchToIndex) {
            sceneGroup.show(s.getScene(), currentIndex < switchToIndex ? VSceneShowMethod.FROM_BOTTOM : VSceneShowMethod.FROM_TOP);
        }
        stage.getRootSceneGroup().hide(menuScene, VSceneHideMethod.TO_LEFT);
    }

    private boolean altIsPressed = false;

    private boolean canEnterTool(MainScene scene) {
        if (scene instanceof EnterCheck) {
            return ((EnterCheck) scene).enterCheck(altIsPressed);
        } else {
            return true;
        }
    }

    private boolean exitTool() {
        var sceneGroup = stage.getSceneGroup();
        var mainScene = sceneMap.get(sceneGroup.getCurrentMainScene());
        assert mainScene != null;
        if (exitTool(mainScene)) {
            setSceneUnselected(mainScene);
            return true;
        } else {
            return false;
        }
    }

    private boolean exitTool(MainScene scene) {
        if (scene instanceof ExitCheck) {
            return ((ExitCheck) scene).exitCheck();
        } else {
            return true;
        }
    }

    public void hideInactive() {
        var currentScene = sceneMap.get(stage.getSceneGroup().getNextOrCurrentMainScene());
        assert currentScene != null;
        for (var s : mainScenes) {
            if (s == currentScene) {
                continue;
            }
            s.setVisible(false, currentScene.getScene());
        }
    }

    private void setSceneSelected(MainScene inst) {
        inst.getMenuButton().setDisable(true);
        inst.setVisible(true, null);
    }

    private void setSceneUnselected(MainScene inst) {
        inst.getMenuButton().setDisable(false);
        inst.setVisible(false, null);
    }

    public void init() {
        hideInactive();
        configureRootCorrespondToWelcomeScene();
        Feed.updated.addListener((ob, old, now) -> checkFeedAndSetRootImage());

        FXUtils.runDelay(VScene.ANIMATION_DURATION_MILLIS, this::showGPLAlert);
    }

    private void showGPLAlert() {
        Assistant ass;
        try {
            ass = AssistantConfig.readAssistant(false);
        } catch (Exception e) {
            Logger.error(LogType.FILE_ERROR, "reading assistant config failed", e);
            return;
        }
        if (ass.disableAlertingGPL) {
            return;
        }
        var dialog = new VDialog<Void>();
        dialog.setText(I18n.get().gplAlert("github.com/wkgcass/hotta-pc-assistant"));
        dialog.setButtons(Collections.singletonList(
            new VDialogButton<>(I18n.get().confirmAndDisableGPLAlert())
        ));
        dialog.showAndWait();
        try {
            AssistantConfig.updateAssistant(a -> a.disableAlertingGPL = true);
        } catch (Exception e) {
            Logger.error(LogType.FILE_ERROR, "flushing assistant config for gpl alert failed", e);
        }
    }

    private void configureRootCorrespondToWelcomeScene() {
        stage.useDarkBorder();
        updateRootImage();
        transparentButtonGroup.setManaged(true);
        transparentButtonGroup.setVisible(true);
        normalButtonGroup.setManaged(false);
        normalButtonGroup.setVisible(false);
    }

    private void configureRootCorrespondToNormalScene() {
        stage.useLightBorder();
        updateRootImage();
        stage.getRoot().getNode().setBackground(new Background(new BackgroundFill(
            Theme.current().sceneBackgroundColor(),
            CornerRadii.EMPTY,
            Insets.EMPTY
        )));
        transparentButtonGroup.setManaged(false);
        transparentButtonGroup.setVisible(false);
        normalButtonGroup.setManaged(true);
        normalButtonGroup.setVisible(true);
    }

    private void checkFeedAndSetRootImage() {
        var bg = Feed.get().introBg;
        if (bg == null) {
            return;
        }
        FXUtils.runOnFX(() -> setRootImageBg(bg));
    }

    private void updateRootImage() {
        var bg = Feed.get().introBg;
        if (bg == null) {
            bg = ImageManager.get().load("images/bg/bg1.png");
        }
        setRootImageBg(bg);
    }

    private void setRootImageBg(Image image) {
        if (sceneMap.get(stage.getSceneGroup().getNextOrCurrentMainScene()) != mainScenes.get(0)) {
            stage.getRoot().setBackgroundImage(null);
        } else {
            stage.getRoot().setBackgroundImage(image);
        }
    }
}
