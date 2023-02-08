package net.cassite.hottapcassistant.ui;

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
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import net.cassite.hottapcassistant.feed.Feed;
import net.cassite.hottapcassistant.util.GlobalValues;

import java.util.Arrays;
import java.util.List;

public class UIEntry {
    public final List<MainScene> mainScenes;
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
            new AboutScene(),
            new LogScene()
        );

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
        var sceneGroup = stage.getSceneGroup();
        if (sceneGroup.getCurrentMainScene() != s) {
            if (canEnterTool(s) && exitTool()) {
                showScene(switchToIndex);
                setSceneSelected(s);
                s.getNode().requestFocus();

                if (switchToIndex == 0) {
                    FXUtils.runDelay(VScene.ANIMATION_DURATION_MILLIS, this::configureRootCorrespondToWelcomeScene);
                } else {
                    configureRootCorrespondToNormalScene();
                }
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

    public void init() {
        hideInactive();
        configureRootCorrespondToWelcomeScene();
        Feed.updated.addListener((ob, old, now) -> checkFeedAndSetRootImage());
        stage.getStage().heightProperty().addListener((ob, old, now) -> updateRootImage());
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
        setRootImageBg(bg);
    }

    private void updateRootImage() {
        var bg = Feed.get().introBg;
        if (bg == null) {
            bg = ImageManager.get().load("images/bg/bg1.png");
        }
        setRootImageBg(bg);
    }

    private void setRootImageBg(Image image) {
        if (stage.getSceneGroup().getNextOrCurrentMainScene() != mainScenes.get(0)) {
            return;
        }
        stage.getRoot().getNode().setBackground(new Background(new BackgroundImage(
            image,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            new BackgroundPosition(
                Side.LEFT, 0.5, true,
                Side.TOP, (stage.getStage().getHeight() - image.getHeight()) / 2 + VStage.TITLE_BAR_HEIGHT / 2d, false),
            BackgroundSize.DEFAULT
        )));
    }
}
