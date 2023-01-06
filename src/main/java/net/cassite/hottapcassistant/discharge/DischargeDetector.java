package net.cassite.hottapcassistant.discharge;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import net.cassite.hottapcassistant.entity.Point;
import net.cassite.hottapcassistant.entity.Rect;
import net.cassite.hottapcassistant.util.Logger;
import net.cassite.hottapcassistant.util.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DischargeDetector {
    private volatile Thread thread;
    private final Rect cap;
    private final List<Point> points;
    private final boolean debug;

    private int lastDetectedPoints = 0;
    private boolean fullCharge = false;
    private int skipDetectionCount = 0;
    private final Stabilizer stabilizer = new Stabilizer();

    public DischargeDetector(Rect cap, List<Point> points, boolean debug) {
        this.cap = cap;
        this.points = points;
        this.debug = debug;
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
        fullCharge = false;
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
    }

    public void stop() {
        synchronized (this) {
            var thread = this.thread;
            this.thread = null;
            if (thread == null) return;
            thread.interrupt();
        }
    }

    private void run() {
        long lastBeginTs = System.currentTimeMillis();
        while (thread != null) {
            long now = System.currentTimeMillis();
            if (now - lastBeginTs < 25) {
                Utils.delay(25 - (now - lastBeginTs));
            } else if (now - lastBeginTs >= 25) {
                Logger.warn("last discharge detection cost too much time: " + (now - lastBeginTs) + "ms");
            }
            lastBeginTs = System.currentTimeMillis();
            if (skipDetectionCount > 0) {
                --skipDetectionCount;
                continue;
            }

            long beforeCap = System.currentTimeMillis();
            var img = Utils.robotAWTCapture((int) cap.x, (int) cap.y, (int) cap.w, (int) cap.h);
            var bImg = Utils.convertToBufferedImage(img);
            long afterCap = System.currentTimeMillis();
            if (afterCap - beforeCap >= 24) {
                Logger.warn("screen capture costs too much time: " + (afterCap - beforeCap) + "ms");
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
                Logger.warn("extra check costs too much time: " + (afterExtraCheck - beforeExtraCheck) + "ms");
            }

            long beforeMatchingPoints = System.currentTimeMillis();
            var colors = new ArrayList<Integer>(points.size());
            for (var p : points) {
                var rgb = bImg.getRGB((int) p.x, (int) p.y);
                colors.add(rgb);
            }
            int matchCount = 0;
            for (; matchCount < points.size(); ++matchCount) {
                var c = colors.get(matchCount);
                if (!DischargeCheckContext.isChargeColor(c)) {
                    break;
                }
            }
            long afterMatchingPoints = System.currentTimeMillis();
            if (afterMatchingPoints - beforeMatchingPoints > 1) {
                Logger.warn("matching points costs too much time: " + (afterMatchingPoints - beforeMatchingPoints) + "ms");
            }

            if (debug) {
                int pointRadius = img.getWidth(null) / 15;
                var g = bImg.createGraphics();
                for (var i = 0; i < colors.size() - 1; ++i) {
                    var p = points.get(i);
                    g.setPaint(new Color(colors.get(i)));
                    g.fillOval((int) (p.x - pointRadius), (int) (p.y - pointRadius), pointRadius * 2, pointRadius * 2);
                }
                g.setPaint(new Color(255, 0, 0));
                g.setFont(new Font(null, Font.BOLD, 16));
                g.drawString("match:" + matchCount, 10, 20);
                g.drawString("c-p:" + Utils.roughFloatValueFormat.format(chargeColorPercentage * 100) + "%", 10, 40);
                g.drawString("w-p:" + Utils.roughFloatValueFormat.format(whitePercentage * 100) + "%", 10, 60);
                bImg.flush();

                Platform.runLater(() -> {
                    var fxImg = SwingFXUtils.toFXImage(bImg, null);
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
                Logger.warn("stabilizer costs too much time: " + (afterStabilizer - beforeStabilizer) + "ms");
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
