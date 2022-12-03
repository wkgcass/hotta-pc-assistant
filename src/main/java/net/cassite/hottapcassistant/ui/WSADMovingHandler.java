package net.cassite.hottapcassistant.ui;

import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class WSADMovingHandler {
    private final double xSpeed;
    private final double ySpeed;
    private final Consumer<double[]> xySetter;
    private final Supplier<double[]> xyGetter;
    private long time;
    private boolean w;
    private boolean s;
    private boolean wsIsW;
    private boolean a;
    private boolean d;
    private boolean adIsA;
    private AnimationTimer timer = null;
    private double x;
    private double y;

    public WSADMovingHandler(double xSpeed, double ySpeed, Consumer<double[]> xySetter, Supplier<double[]> xyGetter) {
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.xySetter = xySetter;
        this.xyGetter = xyGetter;
    }

    public void onPressed(KeyCode code) {
        switch (code) {
            case W -> {
                if (!w) {
                    w = true;
                    wsIsW = true;
                    record();
                }
            }
            case S -> {
                if (!s) {
                    s = true;
                    wsIsW = false;
                    record();
                }
            }
            case A -> {
                if (!a) {
                    a = true;
                    adIsA = true;
                    record();
                }
            }
            case D -> {
                if (!d) {
                    d = true;
                    adIsA = false;
                    record();
                }
            }
            default -> {
                return;
            }
        }
        if (w || s || a || d) {
            if (timer == null) {
                timer = new Timer();
                timer.start();
            }
        }
    }

    private void record() {
        time = System.nanoTime();
        var xy = xyGetter.get();
        x = xy[0];
        y = xy[1];
    }

    public void onReleased(KeyCode code) {
        switch (code) {
            case W -> {
                if (w) {
                    w = false;
                    wsIsW = false;
                    record();
                }
            }
            case S -> {
                if (s) {
                    s = false;
                    wsIsW = true;
                    record();
                }
            }
            case A -> {
                if (a) {
                    a = false;
                    adIsA = false;
                    record();
                }
            }
            case D -> {
                if (d) {
                    d = false;
                    adIsA = true;
                    record();
                }
            }
            default -> {
                return;
            }
        }
        if (!w && !s && !a && !d) {
            var timer = this.timer;
            this.timer = null;
            if (timer != null) {
                timer.stop();
            }
        }
        if (!w && !s && !a && !d) {
            time = 0;
        }
    }

    private class Timer extends AnimationTimer {
        @Override
        public void handle(long now) {
            if (time == 0) {
                return;
            }
            long delta = now - time;
            if (delta < 0) return;
            double x = WSADMovingHandler.this.x;
            double y = WSADMovingHandler.this.y;
            if (w || s) {
                if (wsIsW) {
                    y -= ySpeed * delta / 1_000_000;
                } else {
                    y += ySpeed * delta / 1_000_000;
                }
            }
            if (a || d) {
                if (adIsA) {
                    x -= xSpeed * delta / 1_000_000;
                } else {
                    x += xSpeed * delta / 1_000_000;
                }
            }
            xySetter.accept(new double[]{x, y});
        }
    }
}
