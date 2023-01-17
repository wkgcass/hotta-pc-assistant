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
import io.vproxy.vfx.ui.alert.SimpleAlert;
import io.vproxy.vfx.ui.alert.StackTraceAlert;
import io.vproxy.vfx.ui.layout.HPadding;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.shapes.MovablePoint;
import io.vproxy.vfx.ui.shapes.MovableRect;
import io.vproxy.vfx.util.FXUtils;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
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
import net.cassite.hottapcassistant.util.Utils;
import org.controlsfx.control.ToggleSwitch;

import java.util.Objects;
import java.util.function.Consumer;

public class FishingPane extends StackPane implements NativeKeyListener, EnterCheck {
    private final FishRobot robot = new FishRobot(this::setStatus, this::setPercentage);

    private final Label statusValue = new Label();
    private final ToggleSwitch switchButton = new ToggleSwitch();

    private final Label startKey = new Label();
    private final Label stopKey = new Label();
    private final Label leftKey = new Label();
    private final Label rightKey = new Label();

    private boolean isConfiguring = false;
    private AssistantFishing fishing;

    public FishingPane() {
        initKeyLabel(startKey, key -> fishing.startKey = key);
        initKeyLabel(stopKey, key -> fishing.stopKey = key);
        initKeyLabel(leftKey, key -> fishing.leftKey = key);
        initKeyLabel(rightKey, key -> fishing.rightKey = key);

        var vbox = new VBox();
        getChildren().add(vbox);

        vbox.setPadding(new Insets(10, 0, 0, 0));

        {
            var hbox = new HBox();
            var statusLabel = new Label(I18n.get().fishingStatus()) {{
                FontManager.get().setFont(this);
            }};
            FontManager.get().setFont(statusValue);
            hbox.getChildren().addAll(statusLabel, new HPadding(2), statusValue);
            vbox.getChildren().add(hbox);
            setStatus(FishRobot.Status.STOPPED);
        }

        {
            var sep = new Separator();
            sep.setPadding(new Insets(10, 0, 10, 0));
            vbox.getChildren().add(sep);
        }

        {
            var switchHBox = new HBox();
            var switchBtnLabel = new Label(I18n.get().fishingSwitchButtonLabel()) {{
                FontManager.get().setFont(this);
            }};
            switchHBox.getChildren().addAll(switchBtnLabel, new HPadding(5), switchButton);
            vbox.getChildren().add(switchHBox);
        }

        vbox.getChildren().add(new VPadding(2));

        {
            var macroAlertLabel = new Label(I18n.get().macroAlertLabel()) {{
                FontManager.get().setFont(this);
            }};
            var knowConsequenceCheckBox = new CheckBox(I18n.get().knowConsequencePrompt()) {{
                FontManager.get().setFont(this);
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
                } else {
                    if (!consequenceIsCheckedOnSelect[0]) {
                        return;
                    }
                    GlobalScreen.removeNativeKeyListener(this);
                    GlobalScreenUtils.disable(this);
                    stop();
                }
            });
        }

        {
            var sep = new Separator();
            sep.setPadding(new Insets(10, 0, 10, 0));
            vbox.getChildren().add(sep);
        }

        {
            var startKeyLabel = new Label(I18n.get().fishingStartKey()) {{
                FontManager.get().setFont(this);
                setPrefWidth(60);
            }};
            var stopKeyLabel = new Label(I18n.get().fishingStopKey()) {{
                FontManager.get().setFont(this);
                setPrefWidth(60);
            }};
            var leftKeyLabel = new Label(I18n.get().fishingLeftKey()) {{
                FontManager.get().setFont(this);
                setPrefWidth(60);
            }};
            var rightKeyLabel = new Label(I18n.get().fishingRightKey()) {{
                FontManager.get().setFont(this);
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
            var sep = new Separator();
            sep.setPadding(new Insets(10, 0, 10, 0));
            vbox.getChildren().add(sep);
        }

        {
            var hbox = new HBox();
            var resetBtn = new Button(I18n.get().resetFishing()) {{
                FontManager.get().setFont(this);
            }};
            resetBtn.setPrefWidth(120);
            resetBtn.setOnAction(e -> reset());
            var configBtn = new Button(I18n.get().configureFishing()) {{
                FontManager.get().setFont(this);
            }};
            configBtn.setPrefWidth(120);
            configBtn.setOnAction(e -> configure());

            var configStep1Btn = new Button(I18n.get().configureFishingOnlyStep1()) {{
                FontManager.get().setFont(this);
            }};
            configStep1Btn.setPrefWidth(120);
            configStep1Btn.setOnAction(e -> configureStep1(null));

            hbox.getChildren().addAll(resetBtn, new HPadding(4), configBtn, new HPadding(4), configStep1Btn);
            vbox.getChildren().add(hbox);
        }

        {
            var sep = new Separator();
            sep.setPadding(new Insets(10, 0, 10, 0));
            vbox.getChildren().add(sep);
        }

        {
            vbox.getChildren().add(new Label(I18n.get().configureFishingHelpMsg()) {{
                FontManager.get().setFont(this);
            }});
        }

        {
            var sep = new Separator();
            sep.setPadding(new Insets(10, 0, 10, 0));
            vbox.getChildren().add(sep);
        }

        {
            var tip1 = new ImageView(ImageManager.get().load("/images/misc/fishing-1.png"));
            tip1.setPreserveRatio(true);
            tip1.setFitHeight(150);
            var tip2 = new ImageView(ImageManager.get().load("/images/misc/fishing-2.png"));
            tip2.setPreserveRatio(true);
            tip2.setFitHeight(150);
            var tip3 = new ImageView(ImageManager.get().load("/images/misc/fishing-3.png"));
            tip3.setPreserveRatio(true);
            tip3.setFitWidth(450);
            vbox.getChildren().addAll(new HBox(tip1, tip2), tip3);
        }
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
            case STOPPED, STOPPING, FAILED -> Color.RED;
            case BEGIN, WAITING_FOR_CASTING, WAITING_FOR_BITE, AFTER_REELING -> Color.ORANGE;
            case MANAGING_POS, BEFORE_REELING -> Color.GREEN;
        };
        this.statusValue.setText(text);
        this.statusValue.setTextFill(color);
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
                start();
            }
        }
    }

    private void start() {
        if (!fishing.isValid()) {
            FXUtils.runOnFX(this::configure);
            return;
        }
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
            FXUtils.iconifyWindow(getScene().getWindow());
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
        if (isConfiguring) {
            return;
        }
        if (robot.isRunning()) {
            switchButton.setSelected(false);
            Platform.runLater(() -> configureStep1(okCallback));
            return;
        }
        isConfiguring = true;
        SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().fishingConfigureTips1());
        FXUtils.iconifyWindow(getScene().getWindow());
        TaskManager.get().execute(() -> {
            try {
                Thread.sleep(3_000);
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
    }

    private void postConfigure() {
        FXUtils.showWindow(getScene().getWindow());
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
            FontManager.get().setFont(this, 48);
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
        var window = this.getScene().getWindow();
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
            FontManager.get().setFont(this, 48);
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
}
