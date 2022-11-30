package net.cassite.hottapcassistant.ui;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import net.cassite.hottapcassistant.component.HPadding;
import net.cassite.hottapcassistant.component.VPadding;
import net.cassite.hottapcassistant.component.keybinding.UIKeyChooser;
import net.cassite.hottapcassistant.component.shapes.MovablePoint;
import net.cassite.hottapcassistant.component.shapes.MovableRect;
import net.cassite.hottapcassistant.config.AssistantConfig;
import net.cassite.hottapcassistant.entity.AssistantFishing;
import net.cassite.hottapcassistant.entity.Key;
import net.cassite.hottapcassistant.fish.FishRobot;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.*;
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
                FontManager.setFont(this);
            }};
            FontManager.setFont(statusValue);
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
                FontManager.setFont(this);
            }};
            switchHBox.getChildren().addAll(switchBtnLabel, new HPadding(5), switchButton);
            vbox.getChildren().add(switchHBox);
        }

        vbox.getChildren().add(new VPadding(2));

        {
            var macroAlertLabel = new Label(I18n.get().macroAlertLabel()) {{
                FontManager.setFont(this);
            }};
            var knowConsequenceCheckBox = new CheckBox(I18n.get().knowConsequencePrompt()) {{
                FontManager.setFont(this);
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
                FontManager.setFont(this);
                setPrefWidth(60);
            }};
            var stopKeyLabel = new Label(I18n.get().fishingStopKey()) {{
                FontManager.setFont(this);
                setPrefWidth(60);
            }};
            var leftKeyLabel = new Label(I18n.get().fishingLeftKey()) {{
                FontManager.setFont(this);
                setPrefWidth(60);
            }};
            var rightKeyLabel = new Label(I18n.get().fishingRightKey()) {{
                FontManager.setFont(this);
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
                FontManager.setFont(this);
            }};
            resetBtn.setPrefWidth(120);
            resetBtn.setOnAction(e -> reset());
            var configBtn = new Button(I18n.get().configureFishing()) {{
                FontManager.setFont(this);
            }};
            configBtn.setPrefWidth(120);
            configBtn.setOnAction(e -> configure());

            var configStep1Btn = new Button(I18n.get().configureFishingOnlyStep1()) {{
                FontManager.setFont(this);
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
                FontManager.setFont(this);
            }});
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
        FontManager.setFont(label);
        label.setMinWidth(100);
        label.setBackground(new Background(
            new BackgroundFill(Color.color(0.7f, 0.7f, 0.7f), CornerRadii.EMPTY, Insets.EMPTY)
        ));
        label.setAlignment(Pos.CENTER);
        label.setCursor(Cursor.HAND);
        label.setOnMouseClicked(e -> {
            var keyOpt = new UIKeyChooser(false).choose();
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
    public boolean enterCheck() {
        try {
            var a = AssistantConfig.readAssistant();
            fishing = a.fishing;
        } catch (Throwable t) {
            new StackTraceAlert(t).show();
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
            Utils.runOnFX(this::configure);
            return;
        }
        Utils.runOnFX(() -> {
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
            var config = AssistantConfig.readAssistant();
            config.fishing = fishing;
            AssistantConfig.writeAssistant(config);
        } catch (Throwable t) {
            new StackTraceAlert(t).show();
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
            new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().fishingConfigureTips2()).showAndWait();
            Utils.iconifyWindow(getScene().getWindow());
            TaskManager.execute(() -> {
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
        new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().fishingConfigureTips1()).showAndWait();
        Utils.iconifyWindow(getScene().getWindow());
        TaskManager.execute(() -> {
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
        Utils.showWindow(getScene().getWindow());
        isConfiguring = false;
    }

    @SuppressWarnings("DuplicatedCode")
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
            BackgroundSize.DEFAULT)));

        var scene = new Scene(imagePane);

        var fishingPoint = new MovablePoint(I18n.get().positionOfFishingPointTip());
        if (fishing.fishingPoint == null) {
            fishingPoint.setLayoutX(img.getWidth() / 2);
            fishingPoint.setLayoutY(img.getHeight() / 2);
        } else {
            fishingPoint.from(fishing.fishingPoint);
        }
        var castingPoint = new MovablePoint(I18n.get().positionOfCastingPointTip());
        if (fishing.castingPoint == null) {
            castingPoint.setLayoutX(img.getWidth() * 2 / 3);
            castingPoint.setLayoutY(img.getHeight() * 2 / 3);
        } else {
            castingPoint.from(fishing.castingPoint);
        }
        var desc = new Label(I18n.get().fishingConfiguringScreenDescription()) {{
            FontManager.setFont(this, 48);
            setTextFill(Color.RED);
        }};
        {
            var wh = Utils.calculateTextBounds(desc);
            desc.setLayoutX(0);
            desc.setLayoutY(img.getHeight() - wh.getHeight() - 110);
        }
        imagePane.getChildren().addAll(desc, fishingPoint, castingPoint);

        imagePane.setOnKeyReleased(e -> {
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
        var screenOb = Screen.getScreensForRectangle(window.getX(), window.getY(), window.getWidth(), window.getHeight());
        Screen screen;
        if (screenOb.isEmpty()) {
            screen = Screen.getPrimary();
        } else {
            screen = screenOb.get(0);
        }
        if (screen == null) {
            new SimpleAlert(Alert.AlertType.WARNING, "cannot find any display").showAndWait();
            return null;
        }
        return screen;
    }

    @SuppressWarnings("DuplicatedCode")
    private void configurePosBarAndStamina(Consumer<Boolean> cb) {
        var window = this.getScene().getWindow();
        var screenOb = Screen.getScreensForRectangle(window.getX(), window.getY(), window.getWidth(), window.getHeight());
        Screen screen;
        if (screenOb.isEmpty()) {
            screen = Screen.getPrimary();
        } else {
            screen = screenOb.get(0);
        }
        if (screen == null) {
            new SimpleAlert(Alert.AlertType.WARNING, "cannot find any display").showAndWait();
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
            BackgroundSize.DEFAULT)));

        var scene = new Scene(imagePane);

        var posBarRect = new MovableRect(I18n.get().positionOfPositionTip());
        if (fishing.posBarRect == null) {
            posBarRect.setLayoutX(img.getWidth() / 3);
            posBarRect.setLayoutY(50);
            posBarRect.setWidth(img.getWidth() / 3);
            posBarRect.setHeight(150);
        } else {
            posBarRect.from(fishing.posBarRect);
        }
        var staminaRect = new MovableRect(I18n.get().positionOfFishStaminaTip());
        if (fishing.fishStaminaRect == null) {
            staminaRect.setLayoutX(img.getWidth() / 6);
            staminaRect.setLayoutY(50);
            staminaRect.setWidth(img.getWidth() / 6);
            staminaRect.setHeight(150);
        } else {
            staminaRect.from(fishing.fishStaminaRect);
        }
        var desc = new Label(I18n.get().fishingConfiguringScreenDescription()) {{
            FontManager.setFont(this, 48);
            setTextFill(Color.RED);
        }};
        {
            var wh = Utils.calculateTextBounds(desc);
            desc.setLayoutX(0);
            desc.setLayoutY(img.getHeight() - wh.getHeight() - 110);
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
