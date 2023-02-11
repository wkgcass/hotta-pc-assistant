package net.cassite.hottapcassistant.ui;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import io.vproxy.vfx.component.keychooser.KeyChooser;
import io.vproxy.vfx.control.globalscreen.GlobalScreenUtils;
import io.vproxy.vfx.entity.input.Key;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.manager.image.ImageManager;
import io.vproxy.vfx.manager.task.TaskManager;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.alert.SimpleAlert;
import io.vproxy.vfx.ui.alert.StackTraceAlert;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.layout.HPadding;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.pane.FusionPane;
import io.vproxy.vfx.ui.scene.VScene;
import io.vproxy.vfx.ui.scene.VSceneHideMethod;
import io.vproxy.vfx.ui.scene.VSceneRole;
import io.vproxy.vfx.ui.scene.VSceneShowMethod;
import io.vproxy.vfx.ui.shapes.MovablePoint;
import io.vproxy.vfx.ui.shapes.MovableRect;
import io.vproxy.vfx.ui.stage.VStage;
import io.vproxy.vfx.ui.toggle.ToggleSwitch;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import io.vproxy.vfx.util.Logger;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import net.cassite.hottapcassistant.config.AssistantConfig;
import net.cassite.hottapcassistant.entity.AssistantFishing;
import net.cassite.hottapcassistant.fish.FishRobot;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.status.Status;
import net.cassite.hottapcassistant.status.StatusComponent;
import net.cassite.hottapcassistant.status.StatusEnum;
import net.cassite.hottapcassistant.status.StatusManager;
import net.cassite.hottapcassistant.util.Consts;
import net.cassite.hottapcassistant.util.Utils;

import java.awt.*;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class FishingScene extends MainScene implements NativeKeyListener, EnterCheck, Terminate {
    private final VStage stage;

    private final FishRobot robot = new FishRobot(this::setStatus, this::setPercentage);

    private final Label statusValue = new ThemeLabel();
    private final ToggleSwitch switchButton = new ToggleSwitch(10, 30);

    private final Label startKey = new Label();
    private final Label stopKey = new Label();
    private final Label leftKey = new Label();
    private final Label rightKey = new Label();

    private boolean isConfiguring = false;
    private AssistantFishing fishing;

    public FishingScene(VStage stage) {
        this.stage = stage;
        enableAutoContentWidth();

        initKeyLabel(startKey, key -> fishing.startKey = key);
        initKeyLabel(stopKey, key -> fishing.stopKey = key);
        initKeyLabel(leftKey, key -> fishing.leftKey = key);
        initKeyLabel(rightKey, key -> fishing.rightKey = key);

        var vbox = new VBox();
        vbox.setLayoutX(10);
        getContentPane().getChildren().add(vbox);
        FXUtils.observeWidthCenter(getContentPane(), vbox);

        vbox.setPadding(new Insets(10, 0, 0, 0));

        {
            var hbox = new HBox();
            var statusLabel = new ThemeLabel(I18n.get().fishingStatus());
            FontManager.get().setFont(statusValue);
            hbox.getChildren().addAll(statusLabel, new HPadding(2), statusValue);
            vbox.getChildren().add(hbox);
            setStatus(FishRobot.Status.STOPPED);
        }

        {
            vbox.getChildren().add(new VPadding(20));
        }

        {
            var switchHBox = new HBox();
            var switchBtnLabel = new ThemeLabel(I18n.get().fishingSwitchButtonLabel());
            switchHBox.getChildren().addAll(switchBtnLabel, new HPadding(15), switchButton.getNode());
            vbox.getChildren().add(switchHBox);
        }

        vbox.getChildren().add(new VPadding(2));

        {
            var macroAlertLabel = new ThemeLabel(I18n.get().macroAlertLabel());
            var knowConsequenceCheckBox = new CheckBox(I18n.get().knowConsequencePrompt()) {{
                FontManager.get().setFont(this);
                FXUtils.disableFocusColor(this);
                setTextFill(Theme.current().normalTextColor());
            }};
            vbox.getChildren().addAll(macroAlertLabel, new VPadding(5), knowConsequenceCheckBox, new VPadding(5));

            boolean[] consequenceIsCheckedOnSelect = new boolean[]{false};
            switchButton.selectedProperty().addListener((ob, old, now) -> {
                if (Objects.equals(old, now)) return;
                if (switchButton.isSelected()) {
                    if (!knowConsequenceCheckBox.isSelected()) {
                        consequenceIsCheckedOnSelect[0] = false;
                        switchButton.setSelected(false);
                        return;
                    }
                    consequenceIsCheckedOnSelect[0] = true;
                    GlobalScreenUtils.enable(this);
                    GlobalScreen.addNativeKeyListener(this);
                    StatusManager.get().updateStatus(new Status(I18n.get().toolNameFishing(), StatusComponent.MODULE, StatusEnum.READY));
                } else {
                    if (!consequenceIsCheckedOnSelect[0]) {
                        return;
                    }
                    GlobalScreen.removeNativeKeyListener(this);
                    GlobalScreenUtils.disable(this);
                    StatusManager.get().updateStatus(new Status(I18n.get().toolNameFishing(), StatusComponent.MODULE, StatusEnum.STOPPED));
                    stop();
                }
            });
            StatusManager.get().updateStatus(new Status(I18n.get().toolNameFishing(), StatusComponent.MODULE, StatusEnum.STOPPED));
        }

        {
            vbox.getChildren().add(new VPadding(20));
        }

        {
            var startKeyLabel = new ThemeLabel(I18n.get().fishingStartKey()) {{
                setPrefWidth(60);
            }};
            var stopKeyLabel = new ThemeLabel(I18n.get().fishingStopKey()) {{
                setPrefWidth(60);
            }};
            var leftKeyLabel = new ThemeLabel(I18n.get().fishingLeftKey()) {{
                setPrefWidth(60);
            }};
            var rightKeyLabel = new ThemeLabel(I18n.get().fishingRightKey()) {{
                setPrefWidth(60);
            }};

            var hbox1 = new HBox();
            hbox1.getChildren().addAll(
                startKeyLabel, startKey,
                new HPadding(50),
                stopKeyLabel, stopKey);

            var hbox2 = new HBox();
            hbox2.getChildren().addAll(
                leftKeyLabel, leftKey,
                new HPadding(50),
                rightKeyLabel, rightKey
            );

            vbox.getChildren().addAll(hbox1, new VPadding(10), hbox2);
        }

        {
            vbox.getChildren().add(new VPadding(20));
        }

        {
            var buttonPane = new FusionPane(false);
            var hbox = new HBox();
            buttonPane.getContentPane().getChildren().add(hbox);

            var resetBtn = new FusionButton(I18n.get().resetFishing());
            resetBtn.setPrefWidth(120);
            resetBtn.setPrefHeight(40);
            resetBtn.setOnAction(e -> reset());
            var configBtn = new FusionButton(I18n.get().configureFishing());
            configBtn.setPrefWidth(120);
            configBtn.setPrefHeight(40);
            configBtn.setOnAction(e -> configure());

            hbox.getChildren().addAll(resetBtn, new HPadding(4), configBtn);
            vbox.getChildren().add(buttonPane.getNode());
        }

        {
            vbox.getChildren().add(new VPadding(20));
        }

        {
            vbox.getChildren().add(new ThemeLabel(I18n.get().configureFishingHelpMsg()));
        }

        {
            vbox.getChildren().add(new VPadding(5));
        }

        {
            vbox.getChildren().add(new Hyperlink(I18n.get().fishTutorialLinkDesc()) {{
                FontManager.get().setFont(this, settings -> settings.setSize(12));
                setOnAction(e -> {
                    var url = "https://www.bilibili.com/video/BV1fT411m7xR/";
                    try {
                        Desktop.getDesktop().browse(new URL(url).toURI());
                    } catch (Throwable t) {
                        Logger.error("failed opening fishing tutorial link", t);
                        Clipboard.getSystemClipboard().setContent(Map.of(DataFormat.PLAIN_TEXT, url));
                        SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().fishingOpenBrowserForTutorialFailed(url));
                    }
                });
            }});
        }

        {
            vbox.getChildren().add(new VPadding(20));
        }

        {
            var tip1 = new ImageView(ImageManager.get().load("/images/misc/fishing-1.png"));
            tip1.setPreserveRatio(true);
            tip1.setFitHeight(400);
            var tip2 = new ImageView(ImageManager.get().load("/images/misc/fishing-2.png"));
            tip2.setPreserveRatio(true);
            tip2.setFitHeight(400);
            var tip3 = new ImageView(ImageManager.get().load("/images/misc/fishing-3.png"));
            tip3.setPreserveRatio(true);
            tip3.setFitWidth(800);
            vbox.getChildren().addAll(new HBox(tip1, tip2), tip3);
        }
    }

    @Override
    protected boolean hideMenuButton() {
        return true;
    }

    @Override
    public String title() {
        return I18n.get().toolNameFishing();
    }

    private void setStatus(FishRobot.Status status) {
        var text = switch (status) {
            case STOPPED -> I18n.get().fishingStatusStopped();
            case STOPPING -> I18n.get().fishingStatusStopping();
            case BEGIN -> I18n.get().fishingStatusBegin();
            case FAILED -> I18n.get().fishingStatusFailed();
            case WAITING_FOR_CASTING -> I18n.get().fishingStatusWaitingForCasting();
            case WAITING_FOR_BITE -> I18n.get().fishingStatusWaitingForBite();
            case MANAGING_POS -> I18n.get().fishingStatusManagingPos();
            case BEFORE_REELING -> I18n.get().fishingStatusBeforeReeling();
            case AFTER_REELING -> I18n.get().fishingStatusAfterReeling();
        };
        var color = switch (status) {
            case STOPPED, STOPPING, FAILED -> Consts.RED;
            case BEGIN, WAITING_FOR_CASTING, WAITING_FOR_BITE, AFTER_REELING -> Consts.ORANGE;
            case MANAGING_POS, BEFORE_REELING -> Consts.GREEN;
        };
        this.statusValue.setText(text);
        this.statusValue.setTextFill(color);
        switch (status) {
            case STOPPED -> {
                if (switchButton.isSelected())
                    StatusManager.get().updateStatus(new Status(I18n.get().toolNameFishing(), StatusComponent.MODULE, StatusEnum.READY));
                else
                    StatusManager.get().updateStatus(new Status(I18n.get().toolNameFishing(), StatusComponent.MODULE, StatusEnum.STOPPED));
            }
            case STOPPING ->
                StatusManager.get().updateStatus(new Status(I18n.get().toolNameFishing(), StatusComponent.MODULE, StatusEnum.STOPPING));
            default ->
                StatusManager.get().updateStatus(new Status(I18n.get().toolNameFishing(), StatusComponent.MODULE, StatusEnum.RUNNING));
        }
    }

    private void setPercentage(double p) {
        if (robot.getStatus() == FishRobot.Status.MANAGING_POS) {
            statusValue.setText(I18n.get().fishingStatusManagingPos() + " " + Utils.roughFloatValueFormat.format(p * 100) + "%");
        }
    }

    private void initKeyLabel(Label label, Consumer<Key> keyCallback) {
        FontManager.get().setFont(label);
        label.setMinWidth(100);
        label.setBackground(new Background(
            new BackgroundFill(Color.color(0.7f, 0.7f, 0.7f), CornerRadii.EMPTY, Insets.EMPTY)
        ));
        label.setAlignment(Pos.CENTER);
        label.setCursor(Cursor.HAND);
        label.setOnMouseClicked(e -> {
            var keyOpt = new KeyChooser(false).choose();
            if (keyOpt.isEmpty()) return;
            var key = keyOpt.get();
            if (key.button != null) {
                return; // should not happen
            }
            keyCallback.accept(key);
            resetKeyLabels();
            flushConfig();
        });
    }

    @Override
    public boolean enterCheck(boolean skipGamePathCheck) {
        if (!Utils.checkLock("fishing")) {
            return false;
        }

        try {
            var a = AssistantConfig.readAssistant(true);
            fishing = a.fishing;
        } catch (Throwable t) {
            StackTraceAlert.show(I18n.get().readAssistantConfigFailed(), t);
            return false;
        }
        if (fishing == null) {
            fishing = AssistantFishing.empty();
        }
        resetKeyLabels();
        return true;
    }

    private void resetKeyLabels() {
        startKey.setText(fishing.startKey.toString());
        stopKey.setText(fishing.stopKey.toString());
        leftKey.setText(fishing.leftKey.toString());
        rightKey.setText(fishing.rightKey.toString());
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        if (!switchButton.isSelected()) {
            return;
        }
        var fishing = this.fishing;
        if (fishing == null) {
            return;
        }
        if (robot.isRunning()) {
            if (fishing.stopKey == null) {
                return;
            }
            if (fishing.stopKey.key.code == e.getKeyCode()) {
                stop();
            }
        } else {
            if (fishing.startKey == null) {
                return;
            }
            if (fishing.startKey.key.code == e.getKeyCode()) {
                FXUtils.runOnFX(() -> {
                    if (!fishing.isValid()) {
                        FXUtils.runOnFX(this::configure);
                        return;
                    }
                    stage.temporaryOnTop();
                    FXUtils.toFrontWindow(stage.getStage());
                    configureStep1(500, true, () -> {
                        flushConfig();
                        postConfigure(false);
                        start();
                    });
                });
            }
        }
    }

    private void start() {
        FXUtils.runOnFX(() -> {
            var screen = getScreen();
            if (screen == null) {
                return;
            }
            robot.start(fishing, screen, fishing.leftKey, fishing.rightKey);
        });
    }

    private void stop() {
        robot.stop();
    }

    private void flushConfig() {
        try {
            AssistantConfig.updateAssistant(config -> config.fishing = fishing);
        } catch (Throwable t) {
            StackTraceAlert.show(I18n.get().writeAssistantConfigFailed(), t);
        }
    }

    private void reset() {
        if (robot.isRunning()) {
            switchButton.setSelected(false);
            Platform.runLater(this::reset);
            return;
        }
        fishing.reset();
        flushConfig();
    }

    private void configure() {
        configureStep1(() -> {
            SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().fishingConfigureTips2());
            FXUtils.toBackWindow(getNode().getScene().getWindow());
            TaskManager.get().execute(() -> {
                try {
                    Thread.sleep(3_000);
                } catch (InterruptedException ignore) {
                }
                Platform.runLater(() -> configurePosBarAndStamina(ok2 -> {
                    if (!ok2) {
                        postConfigure();
                        return;
                    }
                    flushConfig();
                    postConfigure();
                }));
            });
        });
    }

    private void configureStep1(Runnable okCallback) {
        configureStep1(3_000, false, okCallback);
    }

    private void configureStep1(int timeoutMillis, boolean useScene, Runnable okCallback) {
        if (isConfiguring) {
            return;
        }
        if (robot.isRunning()) {
            switchButton.setSelected(false);
            Platform.runLater(() -> configureStep1(okCallback));
            return;
        }
        isConfiguring = true;
        Runnable runOnButtonClicked = () -> {
            FXUtils.toBackWindow(getNode().getScene().getWindow());
            TaskManager.get().execute(() -> {
                try {
                    Thread.sleep(timeoutMillis);
                } catch (InterruptedException ignore) {
                }
                Platform.runLater(() -> configureFishingPointAndCastingPoint(ok -> {
                    if (!ok) {
                        postConfigure();
                        return;
                    }
                    if (okCallback == null) {
                        flushConfig();
                        postConfigure();
                    } else {
                        okCallback.run();
                    }
                }));
            });
        };
        if (useScene) {
            var confirmScene = new VScene(VSceneRole.TEMPORARY);
            confirmScene.getNode().setBackground(new Background(new BackgroundFill(
                Theme.current().sceneBackgroundColor(),
                CornerRadii.EMPTY,
                Insets.EMPTY
            )));
            confirmScene.enableAutoContentWidthHeight();
            var label = new ThemeLabel(I18n.get().fishingConfigureTips1());
            var button = new FusionButton(I18n.get().fishingStartConfiguring()) {{
                setPrefWidth(120);
                setPrefHeight(45);
                setOnAction(e -> {
                    stage.getSceneGroup().hide(confirmScene, VSceneHideMethod.TO_RIGHT);
                    FXUtils.runDelay(VScene.ANIMATION_DURATION_MILLIS, () -> stage.getSceneGroup().removeScene(confirmScene));
                    runOnButtonClicked.run();
                });
            }};
            var skipConfiguration = new FusionButton(I18n.get().fishingSkipConfigureStep1Button()) {{
                setPrefWidth(400);
                setPrefHeight(45);
                setOnAction(e -> {
                    stage.getSceneGroup().hide(confirmScene, VSceneHideMethod.TO_RIGHT);
                    okCallback.run();
                });
            }};
            var vbox = new VBox(
                label,
                new VPadding(10),
                new HBox(skipConfiguration, new HPadding(10), button)
            ) {{
                setAlignment(Pos.CENTER_RIGHT);
            }};
            confirmScene.getContentPane().getChildren().add(vbox);
            FXUtils.observeWidthHeightCenter(confirmScene.getContentPane(), vbox);

            stage.getSceneGroup().addScene(confirmScene, VSceneHideMethod.TO_RIGHT);
            stage.getSceneGroup().show(confirmScene, VSceneShowMethod.FROM_RIGHT);
        } else {
            SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().fishingConfigureTips1());
            runOnButtonClicked.run();
        }
    }

    private void postConfigure() {
        postConfigure(true);
    }

    private void postConfigure(boolean showWindow) {
        if (showWindow) {
            FXUtils.showWindow(getNode().getScene().getWindow());
        }
        isConfiguring = false;
    }

    @SuppressWarnings({"DuplicatedCode", "ManualMinMaxCalculation"})
    private void configureFishingPointAndCastingPoint(Consumer<Boolean> cb) {
        var screen = getScreen();
        if (screen == null) {
            cb.accept(false);
            return;
        }
        var img = Utils.execRobotOnThread(r -> r.captureScreen(screen));
        var stage = new Stage();
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setFullScreen(true);

        var imagePane = new Pane();
        imagePane.setBackground(new Background(new BackgroundImage(img,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.DEFAULT,
            new BackgroundSize(1, 1, true, true, false, false
            ))));

        var scene = new Scene(imagePane);

        var fishingPoint = new MovablePoint(I18n.get().positionOfFishingPointTip());
        if (fishing.fishingPoint == null) {
            fishingPoint.setLayoutX(img.getWidth() / 2 / screen.getOutputScaleX());
            fishingPoint.setLayoutY(img.getHeight() / 2 / screen.getOutputScaleY());
        } else {
            fishingPoint.from(fishing.fishingPoint);
        }
        var castingPoint = new MovablePoint(I18n.get().positionOfCastingPointTip());
        if (fishing.castingPoint == null) {
            castingPoint.setLayoutX(img.getWidth() * 2 / 3 / screen.getOutputScaleX());
            castingPoint.setLayoutY(img.getHeight() * 2 / 3 / screen.getOutputScaleY());
        } else {
            castingPoint.from(fishing.castingPoint);
        }
        var desc = new Label(I18n.get().fishingConfiguringScreenDescription()) {{
            FontManager.get().setFont(this, settings -> settings.setSize(48));
            setTextFill(Color.RED);
        }};
        {
            var wh = FXUtils.calculateTextBounds(desc);
            desc.setLayoutX(0);
            desc.setLayoutY(img.getHeight() / screen.getOutputScaleY() - wh.getHeight() - 110);
        }
        imagePane.getChildren().addAll(desc, fishingPoint, castingPoint);

        var wsadHandler = new WSADMovingHandler(0.02, 0.02,
            xy -> {
                if (xy[0] < 0) {
                    fishingPoint.setLayoutX(0);
                } else if (xy[0] > stage.getWidth()) {
                    fishingPoint.setLayoutX(stage.getWidth());
                } else {
                    fishingPoint.setLayoutX(xy[0]);
                }
                if (xy[1] < 0) {
                    fishingPoint.setLayoutY(0);
                } else if (xy[1] > stage.getHeight()) {
                    fishingPoint.setLayoutY(stage.getHeight());
                } else {
                    fishingPoint.setLayoutY(xy[1]);
                }
            },
            () -> new double[]{fishingPoint.getLayoutX(), fishingPoint.getLayoutY()}
        );

        imagePane.setOnKeyPressed(e -> wsadHandler.onPressed(e.getCode()));
        imagePane.setOnKeyReleased(e -> {
            wsadHandler.onReleased(e.getCode());
            if (e.getCode() == KeyCode.ENTER || (e.getCode() == KeyCode.W && e.isControlDown())) {
                stage.close();
            }
            if (e.getCode() == KeyCode.ENTER) {
                fishing.fishingPoint = fishingPoint.makePoint();
                fishing.castingPoint = castingPoint.makePoint();
                cb.accept(true);
            } else if (e.getCode() == KeyCode.W && e.isControlDown()) {
                cb.accept(false);
            }
        });

        stage.setScene(scene);
        stage.setOnCloseRequest(e -> cb.accept(false));
        stage.show();
        Platform.runLater(imagePane::requestFocus);
    }

    private Screen getScreen() {
        var window = getNode().getScene().getWindow();
        return FXUtils.getScreenOf(window);
    }

    @SuppressWarnings("DuplicatedCode")
    private void configurePosBarAndStamina(Consumer<Boolean> cb) {
        Screen screen = getScreen();
        if (screen == null) {
            SimpleAlert.showAndWait(Alert.AlertType.WARNING, "cannot find any display");
            cb.accept(false);
            return;
        }
        var img = Utils.execRobotOnThread(r -> r.captureScreen(screen));
        var stage = new Stage();
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setFullScreen(true);

        var imagePane = new Pane();
        imagePane.setBackground(new Background(new BackgroundImage(img,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.DEFAULT,
            new BackgroundSize(1, 1, true, true, false, false
            ))));

        var scene = new Scene(imagePane);

        var posBarRect = new MovableRect(I18n.get().positionOfPositionTip());
        if (fishing.posBarRect == null) {
            posBarRect.setLayoutX(img.getWidth() / 3 / screen.getOutputScaleX());
            posBarRect.setLayoutY(50);
            posBarRect.setWidth(img.getWidth() / 3 / screen.getOutputScaleX());
            posBarRect.setHeight(150);
        } else {
            posBarRect.from(fishing.posBarRect);
        }
        var staminaRect = new MovableRect(I18n.get().positionOfFishStaminaTip());
        if (fishing.fishStaminaRect == null) {
            staminaRect.setLayoutX(img.getWidth() / 6 / screen.getOutputScaleX());
            staminaRect.setLayoutY(50);
            staminaRect.setWidth(img.getWidth() / 6 / screen.getOutputScaleX());
            staminaRect.setHeight(150);
        } else {
            staminaRect.from(fishing.fishStaminaRect);
        }
        var desc = new Label(I18n.get().fishingConfiguringScreenDescription()) {{
            FontManager.get().setFont(this, settings -> settings.setSize(48));
            setTextFill(Color.RED);
        }};
        {
            var wh = FXUtils.calculateTextBounds(desc);
            desc.setLayoutX(0);
            desc.setLayoutY(img.getHeight() / screen.getOutputScaleY() - wh.getHeight() - 110);
        }
        imagePane.getChildren().addAll(desc, posBarRect, staminaRect);

        imagePane.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER || (e.getCode() == KeyCode.W && e.isControlDown())) {
                stage.close();
            }
            if (e.getCode() == KeyCode.ENTER) {
                fishing.posBarRect = posBarRect.makeRect();
                fishing.fishStaminaRect = staminaRect.makeRect();
                cb.accept(true);
            } else if ((e.getCode() == KeyCode.W && e.isControlDown())) {
                cb.accept(false);
            }
        });

        stage.setScene(scene);
        stage.setOnCloseRequest(e -> cb.accept(false));
        stage.show();
        Platform.runLater(imagePane::requestFocus);
    }

    @Override
    public void terminate() {
        stop();
    }
}
