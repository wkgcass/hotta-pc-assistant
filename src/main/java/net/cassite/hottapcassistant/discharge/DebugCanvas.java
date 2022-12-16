package net.cassite.hottapcassistant.discharge;

import java.awt.*;

public class DebugCanvas {
    private final Graphics2D g;
    private int x;
    private int y;

    public DebugCanvas(Graphics2D g) {
        this.g = g;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void lineTo(int x, int y) {
        g.drawLine(this.x, this.y, x, y);
        this.x = x;
        this.y = y;
    }
}
