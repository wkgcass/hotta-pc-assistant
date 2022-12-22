package net.cassite.hottapcassistant.discharge;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import net.cassite.hottapcassistant.util.Utils;

import java.awt.*;

public class DischargeDetector {
    private volatile Thread thread;
    private final int x;
    private final int y;
    private final int w;
    private final int h;
    private final boolean debug;
    private final Stabilizer stabilizer = new Stabilizer();

    public DischargeDetector(int x, int y, int w, int h, boolean debug) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.debug = debug;
    }

    public boolean isRunning() {
        return thread != null;
    }

    public boolean isFullCharge() {
        return stabilizer.isFullCharge();
    }

    public double getPercentage() {
        return stabilizer.getLastMax();
    }

    public void discharge() {
        stabilizer.discharge();
    }

    public void start() {
        synchronized (this) {
            if (thread != null) {
                throw new IllegalStateException();
            }
            stabilizer.reset();
            thread = new Thread(this::run);
            thread.start();
        }
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
            var img = Utils.robotAWTCapture(x, y, w, h);
            var bImg = Utils.convertToBufferedImage(img);
            DischargeCheckContext ctx;
            if (debug) {
                var g = bImg.createGraphics();
                g.setStroke(new BasicStroke());
                g.setPaint(new Color(255, 0, 0));
                ctx = DischargeCheckContext.of(bImg, g);
            } else {
                ctx = DischargeCheckContext.of(bImg);
            }
            DischargeCheckAlgorithm.DischargeCheckResult result;
            if (ctx == null) {
                result = new DischargeCheckAlgorithm.DischargeCheckResult(0, 0, null);
            } else {
                var algo = new SimpleDischargeCheckAlgorithm();
                algo.init(ctx);
                result = algo.check();
            }
            if (debug) {
                Platform.runLater(() -> {
                    var g = bImg.createGraphics();
                    g.setPaint(new Color(255, 0, 0));
                    g.setFont(new Font(null, Font.BOLD, 16));
                    g.drawString(Utils.roughFloatValueFormat.format(result.p() * 100) + "%", 10, 20);
                    bImg.flush();
                    var fxImg = SwingFXUtils.toFXImage(bImg, null);
                    var content = new ClipboardContent();
                    content.putImage(fxImg);
                    Clipboard.getSystemClipboard().setContent(content);
                });
            }
            stabilizer.add(result);
            if (thread == null) {
                break;
            }
            try {
                //noinspection BusyWait
                Thread.sleep(stabilizer.getSleepTime());
            } catch (InterruptedException ignore) {
            }
        }
    }
}
