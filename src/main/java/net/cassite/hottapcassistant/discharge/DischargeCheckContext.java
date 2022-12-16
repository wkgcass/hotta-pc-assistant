package net.cassite.hottapcassistant.discharge;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

public class DischargeCheckContext {
    private static final int chargeColor = 0xF1F9FF;
    private static final int chargeRed = (chargeColor >> 16) & 0xff;
    private static final int chargeGreen = (chargeColor >> 8) & 0xff;
    private static final int chargeBlue = chargeColor & 0xff;
    private static final int nonFullChargeColor = 0xE3E1E0;
    private static final int nonFullChargeRed = (nonFullChargeColor >> 16) & 0xff;
    private static final int nonFullChargeGreen = (nonFullChargeColor >> 8) & 0xff;
    private static final int nonFullChargeBlue = nonFullChargeColor & 0xff;

    private final PixelReader img;
    private final Canvas canvas;
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
    private int fullChargeColorCount = 0;
    private int nonFullChargeColorCount = 0;

    private DischargeCheckContext(Image img, int[] begin, Canvas canvas) {
        this.img = img.getPixelReader();
        this.width = (int) img.getWidth();
        this.height = (int) img.getHeight();
        this.canvas = canvas;

        x = begin[0];
        y = begin[1];
        initialX = x;
        initialY = y;
        maxX = x;
        minX = x;
        maxY = y;
        xWhenReachingMaxY = x;
    }

    public static DischargeCheckContext of(Image image) {
        return of(image, null);
    }

    public static DischargeCheckContext of(Image image, Canvas canvas) {
        var img = image.getPixelReader();
        var width = (int) image.getWidth();
        var height = (int) image.getHeight();

        var begin = getBeginPosition(width, height, img);
        if (begin == null) return null;
        return new DischargeCheckContext(image, begin, canvas);
    }

    private boolean isChargeColor(int color) {
        if (nonFullChargeColorCount == 0 && fullChargeColorCount > 50) return isFullChargeColor(color);
        if (nonFullChargeColorCount > 50 && fullChargeColorCount == 0) return isNonFullChargeColor(color);
        if (nonFullChargeColorCount != 0 && fullChargeColorCount != 0) {
            var d = nonFullChargeColorCount / (double) fullChargeColorCount;
            if (d > 10) {
                return isNonFullChargeColor(color);
            } else if (d < 1 / 10d) {
                return isFullChargeColor(color);
            }
        }

        if (isNonFullChargeColor(color)) {
            ++nonFullChargeColorCount;
            return true;
        } else if (isFullChargeColor(color)) {
            ++fullChargeColorCount;
            return true;
        }
        return false;
    }

    @SuppressWarnings({"RedundantIfStatement", "DuplicatedCode"})
    private static boolean isNonFullChargeColor(int color) {
        final int delta = 22;
        int r = (color >> 16) & 0xff;
        if (Math.abs(nonFullChargeRed - r) > delta) return false;
        int g = (color >> 8) & 0xff;
        if (Math.abs(nonFullChargeGreen - g) > delta) return false;
        int b = color & 0xff;
        if (Math.abs(nonFullChargeBlue - b) > delta) return false;
        return true;
    }

    @SuppressWarnings({"RedundantIfStatement", "DuplicatedCode"})
    private static boolean isFullChargeColor(int color) {
        final int delta = 20;
        int r = (color >> 16) & 0xff;
        if (Math.abs(chargeRed - r) > delta) return false;
        int g = (color >> 8) & 0xff;
        if (Math.abs(chargeGreen - g) > delta) return false;
        int b = color & 0xff;
        if (Math.abs(chargeBlue - b) > delta) return false;
        return true;
    }

    private static int[] getBeginPosition(int width, int height, PixelReader img) {
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
                var color = img.getArgb(x, y);
                if (isFullChargeColor(color) || isNonFullChargeColor(color)) {
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
        return isChargeColor(img.getArgb(x, y - n));
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
        return isChargeColor(img.getArgb(x, y + n));
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
        return isChargeColor(img.getArgb(x - n, y));
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
        return isChargeColor(img.getArgb(x + n, y));
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
        var g = canvas.getGraphicsContext2D();
        g.lineTo(x, y);
        g.stroke();
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

    public double calculatePercentage(int x, int y) {
        if (movedCount > 100) {
            if (isCloseTo(initialX, initialY, x, y)) return 1;
            return calculatePercentageWithCentralPoint(x, y);
        } else {
            if (isCloseTo(initialX, initialY, x, y)) return 0;
        }
        var l = Math.sqrt((x - initialX) * (x - initialX) + (y - initialY) * (y - initialY));
        var n = Math.asin((y - initialY) / l);
        var degree = n / Math.PI * 180;
        if (Math.abs(degree - 30) < 10) {
            return 0;
        }
        if (Math.abs(degree - 180) < 10) {
            return 0.5;
        } else if (degree > 180) { // invalid state
            return 0;
        }
        return calculatePercentageWithDegree(degree);
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
