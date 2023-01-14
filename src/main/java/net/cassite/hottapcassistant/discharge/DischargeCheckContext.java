package net.cassite.hottapcassistant.discharge;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DischargeCheckContext {
    private static final int chargeColor = 0xE0E0E0;
    private static final int chargeRed = (chargeColor >> 16) & 0xff;
    private static final int chargeGreen = (chargeColor >> 8) & 0xff;
    private static final int chargeBlue = chargeColor & 0xff;

    private final BufferedImage img;
    private final DebugCanvas canvas;
    private final int width;
    private final int height;
    private final int initialX;
    private final int initialY;

    private int x;
    private int y;

    private int maxX;
    private int minX;
    private int maxY;
    private int xWhenReachingMaxY;

    private int movedCount = 0;

    private DischargeCheckContext(BufferedImage img, int[] begin, Graphics2D canvas) {
        this.img = img;
        this.width = img.getWidth();
        this.height = img.getHeight();
        if (canvas == null) {
            this.canvas = null;
        } else {
            this.canvas = new DebugCanvas(canvas);
            this.canvas.setPosition(begin[0], begin[1]);
        }

        x = begin[0];
        y = begin[1];
        initialX = x;
        initialY = y;
        maxX = x;
        minX = x;
        maxY = y;
        xWhenReachingMaxY = x;
    }

    public static DischargeCheckContext of(BufferedImage image) {
        return of(image, null);
    }

    public static DischargeCheckContext of(BufferedImage image, Graphics2D canvas) {
        var width = image.getWidth();
        var height = image.getHeight();

        var begin = getBeginPosition(width, height, image);
        if (begin == null) return null;
        return new DischargeCheckContext(image, begin, canvas);
    }

    public static boolean isChargeColor(int color) {
        return isChargeColor(color, 22);
    }

    public static boolean isChargeColorRough(int color) {
        return isChargeColor(color, 30);
    }

    private static boolean isChargeColor(int color, int delta) {
        int r = (color >> 16) & 0xff;
        if (Math.abs(chargeRed - r) > delta) return false;
        int g = (color >> 8) & 0xff;
        if (Math.abs(chargeGreen - g) > delta) return false;
        int b = color & 0xff;
        //noinspection RedundantIfStatement
        if (Math.abs(chargeBlue - b) > delta) return false;
        return true;
    }

    private static int[] getBeginPosition(int width, int height, BufferedImage img) {
        int mid = width / 2;
        int range = 40;
        if (mid + range / 2 >= width || mid - range / 2 < 0) {
            return null;
        }

        int beginX = -1;
        int beginY = -1;
        out:
        for (int y = 0; y < height; ++y) {
            for (int x = mid + range / 2, end = mid - range / 2; x >= end; --x) {
                var color = img.getRGB(x, y);
                if (isChargeColor(color)) {
                    beginX = x;
                    beginY = y;
                    break out;
                }
            }
        }
        if (beginX == -1) return null;
        return new int[]{beginX, beginY};
    }

    public boolean moveUp() {
        return moveUp(1);
    }

    public boolean moveUp(int n) {
        if (!canMoveUp(n)) {
            return false;
        }
        y -= n;
        ++movedCount;
        drawCanvas();
        return true;
    }

    public boolean canMoveUp(int n) {
        if (y <= n - 1) {
            return false;
        }
        return isChargeColor(img.getRGB(x, y - n));
    }

    public int moveUpWithin(int max, int extraEnsure) {
        out:
        for (int i = 1; i <= max; ++i) {
            if (canMoveUp(i)) {
                for (int j = 0; j < extraEnsure; ++j) {
                    if (!canMoveUp(i + j + 1)) {
                        continue out;
                    }
                }
                moveUp(i);
                return i;
            }
        }
        return -1;
    }

    public boolean moveDown() {
        return moveDown(1);
    }

    public boolean moveDown(int n) {
        if (!canMoveDown(n)) {
            return false;
        }
        y += n;
        if (y > maxY) {
            maxY = y;
            xWhenReachingMaxY = x;
        }
        ++movedCount;
        drawCanvas();
        return true;
    }

    public boolean canMoveDown(int n) {
        if (y >= height - n) {
            return false;
        }
        return isChargeColor(img.getRGB(x, y + n));
    }

    public int moveDownWithin(int max, int extraEnsure) {
        out:
        for (int i = 1; i <= max; ++i) {
            if (canMoveDown(i)) {
                for (int j = 0; j < extraEnsure; ++j) {
                    if (!canMoveDown(i + j + 1)) {
                        continue out;
                    }
                }
                moveDown(i);
                return i;
            }
        }
        return -1;
    }

    public boolean moveLeft() {
        return moveLeft(1);
    }

    public boolean moveLeft(int n) {
        if (!canMoveLeft(n)) {
            return false;
        }
        x -= n;
        if (x < minX) {
            minX = x;
        }
        ++movedCount;
        drawCanvas();
        return true;
    }

    public boolean canMoveLeft(int n) {
        if (x <= n - 1) {
            return false;
        }
        return isChargeColor(img.getRGB(x - n, y));
    }

    public int moveLeftWithin(int max, int extraEnsure) {
        out:
        for (int i = 1; i <= max; ++i) {
            if (canMoveLeft(i)) {
                for (int j = 0; j < extraEnsure; ++j) {
                    if (!canMoveLeft(i + j + 1)) {
                        continue out;
                    }
                }
                moveLeft(i);
                return i;
            }
        }
        return -1;
    }

    public boolean moveRight() {
        return moveRight(1);
    }

    public boolean moveRight(int n) {
        if (!canMoveRight(n)) {
            return false;
        }
        x += n;
        if (x > maxX) {
            maxX = x;
        }
        ++movedCount;
        drawCanvas();
        return true;
    }

    public boolean canMoveRight(int n) {
        if (x >= width - n) {
            return false;
        }
        return isChargeColor(img.getRGB(x + n, y));
    }

    public int moveRightWithin(int max, int extraEnsure) {
        out:
        for (int i = 1; i <= max; ++i) {
            if (canMoveRight(i)) {
                for (int j = 0; j < extraEnsure; ++j) {
                    if (!canMoveRight(i + j + 1)) {
                        continue out;
                    }
                }
                moveRight(i);
                return i;
            }
        }
        return -1;
    }

    private void drawCanvas() {
        if (canvas == null) return;
        canvas.lineTo(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int[] getXY() {
        return new int[]{x, y};
    }

    public void setPosition(int x, int y) {
        this.x = x;
        if (maxX < x) {
            maxX = x;
        }
        if (minX > x) {
            minX = x;
        }
        this.y = y;
        if (maxY < y) {
            maxY = y;
            xWhenReachingMaxY = x;
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMinX() {
        return minX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getInitialX() {
        return initialX;
    }

    public int getInitialY() {
        return initialY;
    }

    public boolean isCloseToInitial() {
        return isCloseTo(initialX, initialY, x, y);
    }

    private static boolean isCloseTo(int ax, int ay, int bx, int by) {
        return (ax - bx) * (ax - bx) + (ay - by) * (ay - by) < 25;
    }

    public double[] calculatePercentage(int x, int y) {
        if (movedCount > 100) {
            if (isCloseTo(initialX, initialY, x, y)) return new double[]{1};
            if (maxY - y > 20) return new double[]{calculatePercentageWithCentralPoint(x, y)};
        } else {
            if (isCloseTo(initialX, initialY, x, y)) return new double[]{0};
        }
        var l = Math.sqrt((x - initialX) * (x - initialX) + (y - initialY) * (y - initialY));
        var n = Math.asin((y - initialY) / l);
        var degree = n / Math.PI * 180;
        if (Math.abs(degree - 30) < 10) {
            return new double[]{0, 1 / 6d};
        }
        if (Math.abs(degree - 180) < 10) {
            return new double[]{0.5};
        } else if (degree > 180) { // invalid state
            return new double[]{0};
        }
        return new double[]{calculatePercentageWithDegree(degree)};
    }

    private double calculatePercentageWithCentralPoint(double x, double y) {
        double ox = (initialX + xWhenReachingMaxY) / 2d;
        double oy = (initialY + maxY) / 2d;
        var l = Math.sqrt((ox - x) * (ox - x) + (oy - y) * (oy - y));
        var n = Math.asin((y - oy) / l);
        var degree = n / Math.PI * 180;

        degree = 180 - degree;

        degree += 90;
        return degree / 360;
    }

    private double calculatePercentageWithDegree(double degree) {
        return ((degree - 30) / 60) * 2 / 6d + 1 / 6d;
    }
}
