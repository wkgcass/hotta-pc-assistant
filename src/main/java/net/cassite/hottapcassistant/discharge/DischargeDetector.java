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
                Utils.delay(10);
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
                int pointRadius = img.getWidth(null) / 15;
                var g = bImg.createGraphics();
                for (var i = 0; i < colors.size() - 1; ++i) {
                    var p = points.get(i);
                    g.setPaint(new Color(colors.get(i)));
                    g.fillOval((int) (p.x - pointRadius), (int) (p.y - pointRadius), pointRadius * 2, pointRadius * 2);
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

            matchCount = stabilizer.add(matchCount);
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
            Utils.delay(10);
        }
    }
}
