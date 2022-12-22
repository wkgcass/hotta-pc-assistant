package net.cassite.hottapcassistant.discharge;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import net.cassite.hottapcassistant.entity.Point;
import net.cassite.hottapcassistant.entity.Rect;
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
        return switch (lastDetectedPoints) {
            case 0, 1 -> 0;
            case 2 -> 1 / 6d;
            case 3 -> 2 / 6d;
            case 4 -> 3 / 6d;
            case 5 -> 4 / 6d;
            case 6 -> 5 / 6d;
            case 7 -> 5.5 / 6d;
            default -> 1;
        };
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
    }

    public void stop() {
        synchronized (this) {
            var thread = this.thread;
            this.thread = null;
            if (thread == null) return;
            try {
                thread.join();
            } catch (InterruptedException ignore) {
            }
        }
    }

    private void run() {
        while (thread != null) {
            if (skipDetectionCount > 0) {
                --skipDetectionCount;
                Utils.delay(20);
                continue;
            }
            var img = Utils.robotAWTCapture((int) cap.x, (int) cap.y, (int) cap.h, (int) cap.h);
            var bImg = Utils.convertToBufferedImage(img);
            var colors = new ArrayList<Integer>(points.size());
            for (var p : points) {
                var rgb = bImg.getRGB((int) p.x, (int) p.y);
                colors.add(rgb);
            }
            if (debug) {
                var g = bImg.createGraphics();
                for (var i = 0; i < colors.size(); ++i) {
                    var p = points.get(i);
                    g.setPaint(new Color(colors.get(i)));
                    g.fillOval((int) (p.x - 2), (int) (p.y - 2), 4, 4);
                }
            }
            int matchCount = 0;
            for (; matchCount < points.size(); ++matchCount) {
                var c = colors.get(matchCount);
                if (!DischargeCheckContext.isChargeColor(c)) {
                    break;
                }
            }
            if (debug) {
                final int fMatchCount = matchCount;
                Platform.runLater(() -> {
                    var g = bImg.createGraphics();
                    g.setPaint(new Color(255, 0, 0));
                    g.setFont(new Font(null, Font.BOLD, 16));
                    g.drawString("match:" + fMatchCount, 10, 20);
                    bImg.flush();
                    var fxImg = SwingFXUtils.toFXImage(bImg, null);
                    var content = new ClipboardContent();
                    content.putImage(fxImg);
                    Clipboard.getSystemClipboard().setContent(content);
                });
            }

            if (thread == null) {
                break;
            }
            if (lastDetectedPoints > matchCount && lastDetectedPoints >= 3) {
                fullCharge = true;
            }
            lastDetectedPoints = matchCount;
            Utils.delay(20);
        }
    }
}
