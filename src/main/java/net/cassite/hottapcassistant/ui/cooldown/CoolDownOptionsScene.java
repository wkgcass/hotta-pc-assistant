package net.cassite.hottapcassistant.ui.cooldown;

import io.vproxy.vfx.entity.Point;
import io.vproxy.vfx.entity.Rect;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.manager.task.TaskManager;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.alert.SimpleAlert;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.scene.VScene;
import io.vproxy.vfx.ui.scene.VSceneRole;
import io.vproxy.vfx.ui.shapes.MovableRect;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import io.vproxy.vfx.util.Logger;
import io.vproxy.vfx.util.MiscUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import net.cassite.hottapcassistant.discharge.DischargeCheckAlgorithm;
import net.cassite.hottapcassistant.discharge.DischargeCheckContext;
import net.cassite.hottapcassistant.discharge.SimpleDischargeCheckAlgorithm;
import net.cassite.hottapcassistant.entity.AssistantCoolDownOptions;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.ui.CoolDownScene;
import net.cassite.hottapcassistant.util.Consts;
import net.cassite.hottapcassistant.util.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public class CoolDownOptionsScene extends VScene {
    private final SimpleObjectProperty<AssistantCoolDownOptions> options;
    private final CoolDownScene coolDownScene;

    public CoolDownOptionsScene(SimpleObjectProperty<AssistantCoolDownOptions> options, CoolDownScene coolDownScene) {
        super(VSceneRole.TEMPORARY);
        enableAutoContentWidth();
        this.options = options;
        this.coolDownScene = coolDownScene;
        getNode().setBackground(new Background(new BackgroundFill(
            Theme.current().sceneBackgroundColor(),
            CornerRadii.EMPTY,
            Insets.EMPTY
        )));

        var vbox = new VBox();
        vbox.setSpacing(4);
        vbox.setPadding(new Insets(10, 10, 10, 10));
        getContentPane().getChildren().add(vbox);
        FXUtils.observeWidthCenter(getContentPane(), vbox);

        {
            var scanDischargeDesc = new ThemeLabel(I18n.get().cooldownScanDischargeDesc()) {{
                FontManager.get().setFont(Consts.NotoFont, this);
            }};
            vbox.getChildren().add(scanDischargeDesc);
            var scanDischargeCheckbox = new CheckBox(I18n.get().cooldownScanDischargeCheckBox()) {{
                FontManager.get().setFont(this);
                setTextFill(Theme.current().normalTextColor());
                FXUtils.disableFocusColor(this);
            }};
            vbox.getChildren().add(scanDischargeCheckbox);
            var scanDischargeDebugCheckbox = new CheckBox(I18n.get().cooldownScanDischargeDebugCheckBox()) {{
                FontManager.get().setFont(this);
                setTextFill(Theme.current().normalTextColor());
                FXUtils.disableFocusColor(this);
            }};
            vbox.getChildren().add(scanDischargeDebugCheckbox);
            var scanDischargeUseNativeCaptureCheckbox = new CheckBox(I18n.get().cooldownScanDischargeUseNativeCaptureCheckBox()) {{
                FontManager.get().setFont(this);
                setTextFill(Theme.current().normalTextColor());
                FXUtils.disableFocusColor(this);
            }};
            vbox.getChildren().add(scanDischargeUseNativeCaptureCheckbox);
            var scanDischargeUseRoughCaptureCheckbox = new CheckBox(I18n.get().cooldownScanDischargeUseRoughCaptureCheckBox()) {{
                FontManager.get().setFont(this);
                setTextFill(Theme.current().normalTextColor());
                FXUtils.disableFocusColor(this);
            }};
            vbox.getChildren().add(scanDischargeUseRoughCaptureCheckbox);
            var scanDischargeResetConfigBtn = new FusionButton(I18n.get().cooldownScanDischargeResetBtn()) {{
                FontManager.get().setFont(getTextNode());
            }};
            vbox.getChildren().add(scanDischargeResetConfigBtn);
            vbox.getChildren().add(new VPadding(10));
            var hideWhenMouseEnterCheckBox = new CheckBox(I18n.get().hideWhenMouseEnterCheckBox()) {{
                FontManager.get().setFont(this);
                setTextFill(Theme.current().normalTextColor());
                FXUtils.disableFocusColor(this);
            }};
            vbox.getChildren().add(hideWhenMouseEnterCheckBox);
            var lockCDWindowPositionCheckBox = new CheckBox(I18n.get().lockCDWindowPositionCheckBox()) {{
                FontManager.get().setFont(this);
                setTextFill(Theme.current().normalTextColor());
                FXUtils.disableFocusColor(this);
            }};
            vbox.getChildren().add(lockCDWindowPositionCheckBox);
            var onlyShowFirstLineBuffCheckBox = new CheckBox(I18n.get().onlyShowFirstLineBuffCheckBox()) {{
                FontManager.get().setFont(this);
                setTextFill(Theme.current().normalTextColor());
                FXUtils.disableFocusColor(this);
            }};
            vbox.getChildren().add(onlyShowFirstLineBuffCheckBox);
            vbox.getChildren().add(new VPadding(10));
            var playAudioCheckBox = new CheckBox(I18n.get().cooldownPlayAudioCheckBox()) {{
                FontManager.get().setFont(this);
                setTextFill(Theme.current().normalTextColor());
                FXUtils.disableFocusColor(this);
            }};
            vbox.getChildren().add(playAudioCheckBox);
            vbox.getChildren().add(new VPadding(10));
            var applyDischargeForYingZhiCheckBox = new CheckBox(I18n.get().cooldownApplyDischargeForYingZhiCheckBox()) {{
                FontManager.get().setFont(this);
                setTextFill(Theme.current().normalTextColor());
                FXUtils.disableFocusColor(this);
            }};
            vbox.getChildren().add(applyDischargeForYingZhiCheckBox);
            var autoFillPianGuangLingYuSubSkillCheckbox = new CheckBox(I18n.get().cooldownAutoFillPianGuangLingYuSubSkillCheckbox()) {{
                FontManager.get().setFont(this);
                setTextFill(Theme.current().normalTextColor());
                FXUtils.disableFocusColor(this);
            }};
            vbox.getChildren().add(autoFillPianGuangLingYuSubSkillCheckbox);

            options.addListener((ob, old, now) -> {
                if (now == null) return;
                if (now.scanDischargeEnabled()) {
                    scanDischargeCheckbox.setSelected(true);
                }
                scanDischargeDebugCheckbox.setSelected(now.scanDischargeDebug);
                scanDischargeUseNativeCaptureCheckbox.setSelected(now.scanDischargeNativeCapture);
                scanDischargeUseRoughCaptureCheckbox.setSelected(now.scanDischargeRoughCapture);
                hideWhenMouseEnterCheckBox.setSelected(now.hideWhenMouseEnter);
                lockCDWindowPositionCheckBox.setSelected(now.lockCDWindowPosition);
                onlyShowFirstLineBuffCheckBox.setSelected(now.onlyShowFirstLineBuff);
                playAudioCheckBox.setSelected(now.playAudio);
                applyDischargeForYingZhiCheckBox.setSelected(now.applyDischargeForYingZhi);
                autoFillPianGuangLingYuSubSkillCheckbox.setSelected(now.autoFillPianGuangLingYuSubSkill);
            });
            scanDischargeCheckbox.setOnAction(e -> {
                var selected = scanDischargeCheckbox.isSelected();
                var opt = options.get();
                if (selected) {
                    if (opt.canEnableDischarge()) {
                        opt.scanDischarge = true;
                        saveConfig();
                        return;
                    }
                    chooseScanDischargeRectStep1(ok -> {
                        if (ok) {
                            opt.scanDischarge = true;
                            saveConfig();
                        } else {
                            scanDischargeCheckbox.setSelected(false);
                        }
                    });
                } else {
                    opt.scanDischarge = false;
                    saveConfig();
                }
            });
            scanDischargeDebugCheckbox.setOnAction(e -> {
                var opt = options.get();
                opt.scanDischargeDebug = scanDischargeDebugCheckbox.isSelected();
                saveConfig();
            });
            scanDischargeUseNativeCaptureCheckbox.setOnAction(e -> {
                var opt = options.get();
                opt.scanDischargeNativeCapture = scanDischargeUseNativeCaptureCheckbox.isSelected();
                if (scanDischargeUseNativeCaptureCheckbox.isSelected()) {
                    scanDischargeUseRoughCaptureCheckbox.setSelected(false);
                }
                saveConfig();
            });
            scanDischargeUseRoughCaptureCheckbox.setOnAction(e -> {
                var opt = options.get();
                opt.scanDischargeRoughCapture = scanDischargeUseRoughCaptureCheckbox.isSelected();
                if (scanDischargeUseRoughCaptureCheckbox.isSelected()) {
                    scanDischargeUseNativeCaptureCheckbox.setSelected(false);
                }
                saveConfig();
            });
            scanDischargeResetConfigBtn.setOnAction(e -> {
                var opt = options.get();
                opt.scanDischargeRect = null;
                opt.scanDischargeCapScale = 0;
                opt.scanDischargeCriticalPoints = null;
                opt.scanDischarge = false;
                saveConfig();
                scanDischargeCheckbox.setSelected(false);
            });
            hideWhenMouseEnterCheckBox.setOnAction(e -> {
                var opt = options.get();
                opt.hideWhenMouseEnter = hideWhenMouseEnterCheckBox.isSelected();
                saveConfig();
            });
            lockCDWindowPositionCheckBox.setOnAction(e -> {
                var opt = options.get();
                opt.lockCDWindowPosition = lockCDWindowPositionCheckBox.isSelected();
                saveConfig();
            });
            onlyShowFirstLineBuffCheckBox.setOnAction(e -> {
                var opt = options.get();
                opt.onlyShowFirstLineBuff = onlyShowFirstLineBuffCheckBox.isSelected();
                saveConfig();
            });
            applyDischargeForYingZhiCheckBox.setOnAction(e -> {
                var opt = options.get();
                opt.applyDischargeForYingZhi = applyDischargeForYingZhiCheckBox.isSelected();
                saveConfig();
            });
            playAudioCheckBox.setOnAction(e -> {
                var opt = options.get();
                opt.playAudio = playAudioCheckBox.isSelected();
                saveConfig();
            });
            autoFillPianGuangLingYuSubSkillCheckbox.setOnAction(e -> {
                var opt = options.get();
                opt.autoFillPianGuangLingYuSubSkill = autoFillPianGuangLingYuSubSkillCheckbox.isSelected();
                saveConfig();
            });
        }
    }

    private void saveConfig() {
        coolDownScene.saveConfig();
    }

    private void chooseScanDischargeRectStep1(Consumer<Boolean> cb) {
        SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().scanDischargeConfigureTips());
        TaskManager.get().execute(() -> {
            MiscUtils.threadSleep(3_000);
            Platform.runLater(() -> chooseScanDischargeRectStep2(cb));
        });
    }

    @SuppressWarnings("DuplicatedCode")
    private void chooseScanDischargeRectStep2(Consumer<Boolean> cb) {
        Screen screen = FXUtils.getScreenOf(coolDownScene.getNode().getScene().getWindow());
        if (screen == null) {
            SimpleAlert.showAndWait(Alert.AlertType.WARNING, "cannot find any display");
            cb.accept(false);
            return;
        }
        final Screen fScreen = screen;
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

        var scanDischargeRect = new MovableRect(I18n.get().positionOfDischargeTip());
        scanDischargeRect.setLayoutX(img.getWidth() * 2 / 3 / screen.getOutputScaleX());
        scanDischargeRect.setLayoutY(img.getHeight() * 2 / 3 / screen.getOutputScaleY());
        scanDischargeRect.setWidth(128);
        scanDischargeRect.setHeight(128);

        var desc = new Label(I18n.get().scanDischargeScreenDescription()) {{
            FontManager.get().setFont(this, settings -> settings.setSize(48));
            setTextFill(Color.RED);
        }};
        {
            var wh = FXUtils.calculateTextBounds(desc);
            desc.setLayoutX(0);
            desc.setLayoutY(img.getHeight() / screen.getOutputScaleY() - wh.getHeight() - 110);
        }

        imagePane.getChildren().addAll(desc, scanDischargeRect);

        imagePane.setOnKeyReleased(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ENTER) {
                if (calculatePointsAndStore(img, scanDischargeRect.makeRect(), fScreen)) {
                    cb.accept(true);
                } else {
                    Platform.runLater(() -> SimpleAlert.show(Alert.AlertType.WARNING, I18n.get().failedCalculatingCriticalPoints()));
                    cb.accept(false);
                }
                stage.close();
            } else if ((e.getCode() == javafx.scene.input.KeyCode.W && e.isControlDown())) {
                stage.close();
                cb.accept(false);
            }
        });

        stage.setScene(scene);
        Platform.runLater(imagePane::requestFocus);
        stage.showAndWait();
    }

    private boolean calculatePointsAndStore(Image img, Rect rect, Screen screen) {
        try {
            var wImg = new WritableImage(
                (int) (rect.w * screen.getOutputScaleX()),
                (int) (rect.h * screen.getOutputScaleY()));
            wImg.getPixelWriter().setPixels(0, 0,
                (int) (rect.w * screen.getOutputScaleX()),
                (int) (rect.h * screen.getOutputScaleY()),
                img.getPixelReader(),
                (int) (rect.x * screen.getOutputScaleX()),
                (int) (rect.y * screen.getOutputScaleY()));
            img = wImg;
        } catch (Exception e) {
            Logger.error("calculatePointsAndStore failure 1", e);
            return false;
        }

        var bImg = SwingFXUtils.fromFXImage(img, null);
        var opt = options.get();
        DischargeCheckContext ctx;
        if (opt.scanDischargeDebug) {
            var g = bImg.createGraphics();
            g.setPaint(new java.awt.Color(255, 0, 0));
            ctx = DischargeCheckContext.of(bImg, g);
        } else {
            ctx = DischargeCheckContext.of(bImg);
        }
        DischargeCheckAlgorithm.DischargeCheckResult result = null;
        if (ctx != null) {
            for (int extraEnsure = 2; extraEnsure >= 0; --extraEnsure) {
                var args = new SimpleDischargeCheckAlgorithm.Args();
                args.extraEnsure = extraEnsure;
                var algo = new SimpleDischargeCheckAlgorithm(args);
                algo.init(ctx);
                result = algo.check();
                if (result.p() >= 0.9) {
                    break;
                }
            }
        }
        if (opt.scanDischargeDebug) {
            var g = bImg.createGraphics();
            g.setPaint(new java.awt.Color(255, 0, 0));
            g.setFont(new Font(null, Font.BOLD, 16));
            if (result == null) {
                g.drawString("null", 10, 20);
            } else {
                g.drawString(Utils.roughFloatValueFormat.format(result.p() * 100) + "%", 10, 20);
            }
            bImg.flush();
            Utils.copyImageToClipboard(bImg);
        }
        if (result == null) {
            return false;
        }
        if (result.p() < 0.90) {
            return false;
        }
        if (result.p() == 1) {
            return false;
        }

        var pointAdjustFound = -1;
        var points = new ArrayList<io.vproxy.vfx.entity.Point>();
        int widthAfterCut = ctx.getMaxX() - ctx.getMinX();
        pointAdjustLoop:
        for (var pointAdjust : Arrays.asList(widthAfterCut / 24, widthAfterCut / 48, widthAfterCut / 96, widthAfterCut / 144, widthAfterCut / 192)) {
            points.clear();

            int midX = ctx.getInitialX();
            int topY = ctx.getInitialY() + pointAdjust;
            int leftX = ctx.getMinX() + pointAdjust;
            int rightX = ctx.getMaxX() - pointAdjust;
            int botY = ctx.getMaxY() - pointAdjust;

            var p0 = new io.vproxy.vfx.entity.Point(midX, topY);
            points.add(p0);
            double rightYDelta = (rightX - midX + 1) / Math.sqrt(3) * 0.98;
            var p2 = new io.vproxy.vfx.entity.Point(rightX, topY + rightYDelta);
            points.add(io.vproxy.vfx.entity.Point.midOf(p0, p2));
            points.add(p2);
            var p4 = new io.vproxy.vfx.entity.Point(rightX, botY - rightYDelta);
            points.add(io.vproxy.vfx.entity.Point.midOf(p2, p4));
            points.add(p4);
            var p6 = new io.vproxy.vfx.entity.Point(midX, botY);
            points.add(io.vproxy.vfx.entity.Point.midOf(p4, p6));
            points.add(p6);
            double leftYDelta = (midX - leftX + 1) / Math.sqrt(3) * 0.98;
            var p8 = new io.vproxy.vfx.entity.Point(leftX, botY - leftYDelta);
            points.add(io.vproxy.vfx.entity.Point.midOf(p6, p8));
            points.add(p8);
            var p10 = new io.vproxy.vfx.entity.Point(leftX, topY + leftYDelta);
            points.add(io.vproxy.vfx.entity.Point.midOf(p8, p10));
            points.add(p10);
            points.add(io.vproxy.vfx.entity.Point.midOf(p0, p10));
            try { // find the last critical point
                int n = 1;
                while (true) {
                    var argb = img.getPixelReader().getArgb(midX - 2 * n, topY + n);
                    if (!DischargeCheckContext.isChargeColor(argb)) {
                        break;
                    }
                    ++n;
                }
                points.add(new Point(midX - 2 * n, topY + n));
            } catch (Exception e) {
                Logger.error("calculatePointsAndStore failure 2", e);
                continue;
            }
            for (int i = 0; i < points.size() - 1; ++i) {
                var p = points.get(i);
                var rgb = img.getPixelReader().getArgb((int) p.x, (int) p.y);
                if (!DischargeCheckContext.isChargeColor(rgb)) {
                    continue pointAdjustLoop;
                }
            }
            pointAdjustFound = pointAdjust;
            break;
        }

        if (opt.scanDischargeDebug) {
            bImg = SwingFXUtils.fromFXImage(img, null);
            var g = bImg.createGraphics();
            int pointRadius = (int) (img.getWidth() / 15);
            for (int i = points.size() - 2; i >= 0; --i) {
                var p = points.get(i);
                var rgb = bImg.getRGB((int) p.x, (int) p.y);
                g.setPaint(new java.awt.Color(rgb));
                g.fillOval((int) (p.x - pointRadius), (int) (p.y - pointRadius), pointRadius * 2, pointRadius * 2);
            }
            g.setPaint(new java.awt.Color(255, 0, 0));
            g.setFont(new Font(null, Font.BOLD, 16));
            if (pointAdjustFound != -1) {
                g.drawString("adj:" + pointAdjustFound, 10, 20);
            } else {
                g.drawString("adj:null", 10, 20);
            }
            bImg.flush();
            final var fBImg = bImg;
            Platform.runLater(() -> Utils.copyImageToClipboard(fBImg));
        }

        if (pointAdjustFound == -1) {
            return false;
        }

        // cut rect and move the points
        int cutLeft = (int) Math.round(ctx.getMinX() / screen.getOutputScaleX());
        int cutTop = (int) Math.round(ctx.getInitialY() / screen.getOutputScaleY());
        int cutRight = (int) Math.round(rect.w - ctx.getMaxX() / screen.getOutputScaleX());
        int cutBot = (int) Math.round(rect.h - ctx.getMaxY() / screen.getOutputScaleY());
        opt.scanDischargeRect = new Rect(
            rect.x + cutLeft,
            rect.y + cutTop,
            rect.w - cutLeft - cutRight + 2,
            rect.h - cutTop - cutBot + 2);
        opt.scanDischargeCapScale = screen.getOutputScaleX();
        for (var p : points) {
            p.x -= (int) (cutLeft * screen.getOutputScaleX());
            if (p.x < 0) {
                p.x = 0;
            }
            p.y -= (int) (cutTop * screen.getOutputScaleY());
            if (p.y < 0) {
                p.y = 0;
            }
        }
        opt.scanDischargeCriticalPoints = points;
        return true;
    }
}
