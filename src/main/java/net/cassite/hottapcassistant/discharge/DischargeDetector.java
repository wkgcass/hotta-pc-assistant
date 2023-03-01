package net.cassite.hottapcassistant.discharge;

import io.vproxy.base.util.LogType;
import io.vproxy.vfx.entity.Point;
import io.vproxy.vfx.entity.Rect;
import io.vproxy.vfx.util.FXUtils;
import io.vproxy.base.util.Logger;
import io.vproxy.vfx.util.MiscUtils;
import io.vproxy.vfx.util.imagewrapper.BufferedImageBox;
import io.vproxy.vfx.util.imagewrapper.FXWritableImageBox;
import io.vproxy.vfx.util.imagewrapper.ImageBox;
import javafx.application.Platform;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import net.cassite.hottapcassistant.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class DischargeDetector {
    private volatile Thread thread;
    private final Rect cap;
    private final double capScale;
    private final List<Point> points;
    private final boolean debug;
    private final boolean nativeCapture;
    private final boolean roughCapture;
    private final WritableImage imgBuffer;

    private int lastDetectedPoints = 0;
    private volatile boolean isPaused = false;
    private boolean fullCharge = false;
    private int skipDetectionCount = 0;
    private final Stabilizer stabilizer = new Stabilizer();

    public DischargeDetector(Rect cap, double capScale, List<Point> points, boolean nativeCapture, boolean roughCapture, boolean debug) {
        this.cap = cap;
        this.capScale = capScale;
        this.points = points;
        this.debug = debug;
        this.nativeCapture = nativeCapture;
        this.roughCapture = roughCapture;
        if (roughCapture) {
            imgBuffer = new WritableImage((int) cap.w, (int) cap.h);
        } else {
            imgBuffer = null;
        }
    }

    public boolean isRunning() {
        return thread != null;
    }

    public boolean isFullCharge() {
        return fullCharge;
    }

    public double getPercentage() {
        if (lastDetectedPoints == 0 || lastDetectedPoints == 1)
            return 0;
        if (lastDetectedPoints >= 13) return 1;
        return (lastDetectedPoints - 1) / 12d;
    }

    public void discharge() {
        if (fullCharge) {
            fullCharge = false;
        } else {
            lastDetectedPoints = 0;
        }
        skipDetectionCount = 50;
    }

    public void start() {
        synchronized (this) {
            if (thread != null) {
                throw new IllegalStateException();
            }
            reset();
            thread = new Thread(this::run);
            thread.start();
        }
    }

    private void reset() {
        lastDetectedPoints = 0;
        fullCharge = false;
        skipDetectionCount = 0;
        stabilizer.reset();
        isPaused = false;
    }

    public void stop() {
        synchronized (this) {
            var thread = this.thread;
            this.thread = null;
            if (thread == null) return;
            thread.interrupt();
        }
    }

    public void pause() {
        if (!isRunning()) {
            return;
        }
        isPaused = true;
    }

    public void resume() {
        if (!isRunning()) {
            throw new IllegalStateException();
        }
        isPaused = false;
    }

    private void run() {
        long lastBeginTs = System.currentTimeMillis();
        boolean lastPaused = false;
        while (thread != null) {
            if (isPaused) {
                lastPaused = true;
                MiscUtils.threadSleep(500);
                continue;
            }
            long now = System.currentTimeMillis();
            if (lastPaused) {
                lastPaused = false;
                lastBeginTs = now;
            }
            if (now - lastBeginTs < 25) {
                MiscUtils.threadSleep(25 - (now - lastBeginTs));
            } else if (now - lastBeginTs >= 25) {
                Logger.warn(LogType.ALERT, "last discharge detection cost too much time: " + (now - lastBeginTs) + "ms");
            }
            lastBeginTs = System.currentTimeMillis();
            if (skipDetectionCount > 0) {
                --skipDetectionCount;
                continue;
            }

            long beforeCap = System.currentTimeMillis();
            ImageBox bImg;
            if (nativeCapture) {
                bImg = new BufferedImageBox(Utils.execRobotDirectly(r -> r.nativeCapture((int) cap.x, (int) cap.y, (int) cap.w, (int) cap.h, capScale)));
            } else if (roughCapture) {
                bImg = new FXWritableImageBox(
                    (WritableImage) Utils.execRobotOnThread(r -> r.capture(imgBuffer, cap.x, cap.y, (int) cap.w, (int) cap.h, false)),
                    capScale);
            } else {
                var img = Utils.execRobotDirectly(r -> r.awtCapture((int) cap.x, (int) cap.y, (int) cap.w, (int) cap.h));
                bImg = new BufferedImageBox(FXUtils.convertToBufferedImage(img));
            }
            long afterCap = System.currentTimeMillis();
            if (afterCap - beforeCap >= 24) {
                Logger.warn(LogType.ALERT, "screen capture costs too much time: " + (afterCap - beforeCap) + "ms");
            }

            long beforeExtraCheck = System.currentTimeMillis();
            var chargeColorCount = 0;
            var whiteCount = 0;
            var totalCheckedPixels = 0;
            var step = bImg.getWidth() / 100;
            if (step < 10) {
                step = 10;
            }
            for (int x = 0, w = bImg.getWidth(); x < w; x += step) {
                for (int y = 0, h = bImg.getHeight(); y < h; y += step) {
                    var c = bImg.getRGB(x, y) & 0xffffff;
                    if (DischargeCheckContext.isChargeColor(c)) {
                        ++chargeColorCount;
                    }
                    if (c == 0xffffff || c == 0xf5f6f7 /* tim */) {
                        ++whiteCount;
                    }
                    ++totalCheckedPixels;
                }
            }
            var chargeColorPercentage = chargeColorCount / (double) (totalCheckedPixels);
            var whitePercentage = whiteCount / (double) (totalCheckedPixels);
            long afterExtraCheck = System.currentTimeMillis();
            if (afterExtraCheck - beforeExtraCheck > 2) {
                Logger.warn(LogType.ALERT, "extra check costs too much time: " + (afterExtraCheck - beforeExtraCheck) + "ms");
            }

            long beforeMatchingPoints = System.currentTimeMillis();
            var colors = new ArrayList<Integer>(points.size());
            for (var p : points) {
                var rgb = bImg.getRGB((int) p.x, (int) p.y);
                colors.add(rgb);
            }
            if (debug) {
                var sb = new StringBuilder();
                sb.append("=================\n");
                for (int i = 0; i < colors.size(); i++) {
                    if (i < 10) {
                        sb.append("0");
                    }
                    sb.append(i);
                    int c = colors.get(i);
                    var r = (c >> 16) & 0xff;
                    var g = (c >> 8) & 0xff;
                    var b = c & 0xff;
                    sb.append(" ").append("r:").append(r).append(", g:").append(g).append(", b:").append(b).append("\n");
                }
                sb.append("=================");
                assert Logger.lowLevelDebug(sb.toString());
            }
            int matchCount = 0;
            for (; matchCount < points.size(); ++matchCount) {
                var c = colors.get(matchCount);
                if (roughCapture) {
                    if (!DischargeCheckContext.isChargeColorRough(c)) {
                        break;
                    }
                } else {
                    if (!DischargeCheckContext.isChargeColor(c)) {
                        break;
                    }
                }
            }
            long afterMatchingPoints = System.currentTimeMillis();
            if (afterMatchingPoints - beforeMatchingPoints > 1) {
                Logger.warn(LogType.ALERT, "matching points costs too much time: " + (afterMatchingPoints - beforeMatchingPoints) + "ms");
            }

            if (debug) {
                int pointRadius = bImg.getWidth() / 15;
                var g = bImg.createGraphics();
                for (var i = 0; i < colors.size() - 1; ++i) {
                    var p = points.get(i);
                    g.setPaint(colors.get(i));
                    g.fillOval((int) (p.x - pointRadius), (int) (p.y - pointRadius), pointRadius * 2, pointRadius * 2);
                }
                g.setPaint(0xff0000);
                g.setFont(null, true, 24);
                g.drawString("match:" + matchCount, 10, 20);
                g.drawString("c-p:" + Utils.roughFloatValueFormat.format(chargeColorPercentage * 100) + "%", 10, 40);
                g.drawString("w-p:" + Utils.roughFloatValueFormat.format(whitePercentage * 100) + "%", 10, 60);
                g.flush();

                Platform.runLater(() -> {
                    var fxImg = bImg.toFXImage();
                    var content = new ClipboardContent();
                    content.putImage(fxImg);
                    Clipboard.getSystemClipboard().setContent(content);
                });
            }

            if (chargeColorPercentage > 0.4) {
                continue;
            }
            if (whitePercentage > 0.2) {
                continue;
            }

            long beforeStabilizer = System.currentTimeMillis();
            matchCount = stabilizer.add(matchCount);
            long afterStabilizer = System.currentTimeMillis();
            if (afterStabilizer - beforeStabilizer > 1) {
                Logger.warn(LogType.ALERT, "stabilizer costs too much time: " + (afterStabilizer - beforeStabilizer) + "ms");
            }
            if (matchCount == -1) {
                continue;
            }

            if (thread == null) {
                break;
            }
            if (matchCount == 13) {
                fullCharge = true;
            } else if (lastDetectedPoints > matchCount && lastDetectedPoints > 7) {
                fullCharge = true;
            }
            if (matchCount == 13) {
                lastDetectedPoints = 0;
            } else {
                lastDetectedPoints = matchCount;
            }
        }
    }
}
