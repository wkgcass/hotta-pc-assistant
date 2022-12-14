package net.cassite.hottapcassistant.fish;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import net.cassite.hottapcassistant.entity.AssistantFishing;
import net.cassite.hottapcassistant.entity.Key;
import net.cassite.hottapcassistant.util.Logger;
import net.cassite.hottapcassistant.util.TaskManager;
import net.cassite.hottapcassistant.util.Utils;

import java.util.Set;
import java.util.function.Consumer;

public class FishRobot {
    private static final float[] GreenLightColor = Utils.toHSB(Color.color(200f / 255, 253f / 255, 225f / 255));
    private static final float[] GreenLightColor2 = Utils.toHSB(Color.color(112f / 255, 250f / 255, 183f / 255));
    private static final Color YellowSliderColor = Color.color(255f / 255, 176f / 255, 64f / 255);
    private static final Set<Color> PositionBarColors = Set.of(
        Color.color(1, 1, 1),
        Color.color(153f / 255, 128f / 255, 124f / 255),
        Color.color(202f / 255, 171f / 255, 162f / 255));
    private static final Color FishStaminaColor = Color.color(140f / 255, 1, 1);
    private static final Color FishStaminaEmptyColor = Color.color(73f / 255, 73f / 255, 73f / 255);

    public enum Status {
        STOPPED(0), STOPPING(0), BEGIN(1000),
        WAITING_FOR_CASTING(1000), WAITING_FOR_BITE(500),
        MANAGING_POS(50), BEFORE_REELING(500),
        AFTER_REELING(3000),
        FAILED(3000),
        ;
        final int delay;

        Status(int delay) {
            this.delay = delay;
        }

        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        public boolean isStopped() {
            return this == STOPPED;
        }
    }

    private final Consumer<Status> statusInformer;
    private final Consumer<Double> percentageInformer;
    private Status status = Status.STOPPED;
    private AssistantFishing config = null;
    private double captureXOffset;
    private double captureYOffset;

    private final WritableImage buf1 = new WritableImage(3, 3);
    private WritableImage buf2;
    private WritableImage buf3;

    private Key leftKey;
    private Key rightKey;

    private boolean isPressingLeft;
    private boolean isPressingRight;
    private int totalManagingCount = 0;
    private int staminaDrainManagingCount = 0;
    private int posNotFoundCount = 0;
    private final FishingRecognitionDisplay display = new FishingRecognitionDisplay();

    public FishRobot(Consumer<Status> statusInformer, Consumer<Double> percentageInformer) {
        this.statusInformer = statusInformer;
        this.percentageInformer = percentageInformer;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isRunning() {
        return !status.isStopped();
    }

    public void start(AssistantFishing fishing, Screen screen, Key leftKey, Key rightKey) {
        if (!status.isStopped()) {
            throw new IllegalStateException(status.name());
        }
        setStatus(Status.BEGIN);

        this.config = fishing;
        var bounds = screen.getBounds();
        captureXOffset = bounds.getMinX();
        captureYOffset = bounds.getMinY();

        buf2 = new WritableImage((int) fishing.posBarRect.w, (int) fishing.posBarRect.h);
        buf3 = new WritableImage((int) fishing.fishStaminaRect.w, (int) fishing.fishStaminaRect.h);

        this.leftKey = leftKey;
        this.rightKey = rightKey;

        display.doShow(config);

        TaskManager.execute(this::exec);
    }

    public void stop() {
        if (!status.isStopped() && status != Status.STOPPING) {
            setStatus(Status.STOPPING);
            Utils.runOnFX(display::close);
        }
    }

    private void exec() {
        loop:
        while (true) {
            try {
                switch (status) {
                    case STOPPED:
                    case STOPPING:
                        break loop;
                    case BEGIN:
                    case FAILED:
                        setStatus(Status.WAITING_FOR_CASTING);
                        break;
                    case WAITING_FOR_CASTING:
                        cast();
                        break;
                    case WAITING_FOR_BITE:
                        waitForBite();
                        break;
                    case MANAGING_POS:
                        fishing();
                        break;
                    case BEFORE_REELING:
                        pullUp();
                        break;
                    case AFTER_REELING:
                        finishing();
                        break;
                    default:
                        Logger.error("unexpected status: " + status + ", will break loop!");
                        break loop;
                }
            } catch (Throwable t) {
                Logger.error("got exception in exec loop, please report a bug", t);
                break;
            }
            try {
                //noinspection BusyWait
                Thread.sleep(status.delay);
            } catch (InterruptedException ignore) {
            }
        }
        setStatus(Status.STOPPED);
    }

    private void setStatus(Status status) {
        this.status = status;
        Platform.runLater(() -> {
            statusInformer.accept(status);
            if (status != Status.WAITING_FOR_BITE && status != Status.MANAGING_POS) {
                display.hidePosBar();
            }
        });
    }

    private boolean notTheSame(float[] a, Color bb) {
        float[] b = Utils.toHSB(bb);
        return !(Math.abs(a[0] - b[0]) < 0.2)
            || !(Math.abs(a[1] - b[1]) < 0.2)
            || !(Math.abs(a[2] - b[2]) < 0.2);
    }

    private void cast() {
        final double captureWidth = buf1.getWidth();
        final double captureHeight = buf1.getHeight();
        var img = Utils.execRobotOnThread(r -> r.capture(buf1,
            captureXOffset + config.fishingPoint.x - captureWidth / 2,
            captureYOffset + config.fishingPoint.y - (captureHeight - 1),
            (int) captureWidth, (int) captureHeight));
        var imgW = (int) img.getWidth();
        var imgH = (int) img.getHeight();
        var reader = img.getPixelReader();
        var allGreen = true;
        for (int x = 0; x < imgW; ++x) {
            for (int y = 0; y < imgH; ++y) {
                var color = reader.getColor(x, y);
                if (notTheSame(GreenLightColor, color) && notTheSame(GreenLightColor2, color)) {
                    allGreen = false;
                    break;
                }
            }
        }
        if (allGreen) {
            clickCast();
            setStatus(Status.WAITING_FOR_BITE);
        }
    }

    private void clickCast() {
        Utils.execRobot(r -> r.mouseMove(config.castingPoint.x, config.castingPoint.y));
        try {
            Thread.sleep(10);
        } catch (InterruptedException ignore) {
        }
        Utils.execRobot(r -> r.press(new Key(MouseButton.PRIMARY)));
        try {
            Thread.sleep(10);
        } catch (InterruptedException ignore) {
        }
        Utils.execRobot(r -> r.release(new Key(MouseButton.PRIMARY)));
    }

    private void waitForBite() {
        var img = Utils.execRobotOnThread(r -> r.capture(buf2,
            captureXOffset + config.posBarRect.x,
            captureXOffset + config.posBarRect.y,
            (int) config.posBarRect.w,
            (int) config.posBarRect.h
        ));
        var bar = findBar(img);
        displayPosBar(bar, -1);
        int midPos = midOf(bar);
        if (midPos < 0) {
            return;
        }
        setStatus(Status.MANAGING_POS);
    }

    private void displayPosBar(int[] bar, int pos) {
        Utils.runOnFX(() -> {
            if (bar != null) {
                display.updatePosBar(pos, bar[0], bar[1]);
            } else {
                display.updatePosBar(pos, -1, -1);
            }
        });
    }

    private int midOf(int[] arr) {
        if (arr == null) {
            return -1;
        }
        return (arr[0] + arr[1]) / 2;
    }

    private int[] findBar(Image img) {
        boolean found = false;
        int leftX = 0;
        int rightX = 0;
        int leftY = 0;
        int rightY = 0;

        var reader = img.getPixelReader();
        var imgW = (int) img.getWidth();
        var imgH = (int) img.getHeight();
        outer:
        for (int x = 0; x < imgW; ++x) {
            for (int y = 0; y < imgH; ++y) {
                var color = reader.getColor(x, y);
                if (Utils.almostEquals(color, YellowSliderColor)) {
                    if (checkBarLeftToRight(x, y, img)) {
                        leftX = x;
                        leftY = y;
                        found = true;
                        break outer;
                    }
                }
            }
        }
        if (!found) {
            return null;
        }
        outer:
        for (int x = imgW - 1; x >= 0; --x) {
            for (int y = 0; y < imgH; ++y) {
                var color = reader.getColor(x, y);
                if (Utils.almostEquals(color, YellowSliderColor)) {
                    if (checkBarRightToLeft(x, y, img)) {
                        rightX = x;
                        rightY = y;
                        break outer;
                    }
                }
            }
        }
        if (Math.abs(leftY - rightY) > 2) {
            return null;
        }
        return new int[]{leftX, rightX};
    }

    private boolean checkBarLeftToRight(final int xx, final int yy, Image img) {
        final int width = 10;
        final int height = 2;

        var imgW = (int) img.getWidth();
        var imgH = (int) img.getHeight();
        var reader = img.getPixelReader();
        if (xx + width >= imgW) return false;
        if (yy + height >= imgH) return false;
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                var color = reader.getColor(xx + x, yy + y);
                if (!Utils.almostEquals(color, YellowSliderColor)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkBarRightToLeft(final int xx, final int yy, Image img) {
        final int width = 10;
        final int height = 2;

        var imgH = (int) img.getHeight();

        var reader = img.getPixelReader();
        if (xx - width < 0) return false;
        if (yy + height >= imgH) return false;
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                var color = reader.getColor(xx - x, yy + y);
                if (!Utils.almostEquals(color, YellowSliderColor)) {
                    return false;
                }
            }
        }
        return true;
    }

    private int findPos(Image img) {
        var imgW = (int) img.getWidth();
        var imgH = (int) img.getHeight();
        var reader = img.getPixelReader();
        for (int x = 0; x < imgW; ++x) {
            for (int y = 0; y < imgH; ++y) {
                var color = reader.getColor(x, y);
                if (Utils.almostIn(color, PositionBarColors)) {
                    if (checkPos(x, y, img)) {
                        return x;
                    }
                }
            }
        }
        return -1;
    }

    private boolean checkPos(final int xx, final int yy, Image img) {
        final int height = 2;

        var imgH = (int) img.getHeight();
        if (yy + height >= imgH) return false;
        var reader = img.getPixelReader();
        for (int y = 1; y < height; ++y) {
            var color = reader.getColor(xx, yy + y);
            if (!Utils.almostIn(color, PositionBarColors)) {
                return false;
            }
        }
        return true;
    }

    private void fishing() {
        ++totalManagingCount;

        var img = Utils.execRobotOnThread(r -> r.capture(buf3,
            captureXOffset + config.fishStaminaRect.x,
            captureXOffset + config.fishStaminaRect.y,
            (int) config.fishStaminaRect.w,
            (int) config.fishStaminaRect.h
        ));

        double p;
        {
            int s = pixelCount(img, FishStaminaColor);
            int e = pixelCount(img, FishStaminaEmptyColor);
            if (s == 0 && e == 0) {
                if (totalManagingCount > 50) {
                    exitManaging();
                    setStatus(Status.FAILED);
                }
                return;
            }
            p = ((double) s) / (s + e);
        }

        if (p < 0.01 && totalManagingCount > 50) {
            ++staminaDrainManagingCount;
            if (staminaDrainManagingCount > 20) {
                exitManaging();
                setStatus(Status.BEFORE_REELING);
                return;
            }
        }
        Utils.runOnFX(() -> percentageInformer.accept(p));

        img = Utils.execRobotOnThread(r -> r.capture(buf2,
            captureXOffset + config.posBarRect.x,
            captureXOffset + config.posBarRect.y,
            (int) config.posBarRect.w,
            (int) config.posBarRect.h
        ));

        var posbar = findBar(img);
        int bar = midOf(posbar);
        int pos = findPos(img);
        displayPosBar(posbar, pos);
        if (bar == -1) {
            return;
        }

        if (pos == -1) {
            // it's ok because it's not easy to track
            // let it move anyway
            ++posNotFoundCount;
            if (posNotFoundCount >= 20) {
                if (isPressingLeft) {
                    moveRight();
                } else {
                    moveLeft();
                }
            } else {
                if (!isPressingLeft && !isPressingRight) {
                    moveLeft();
                }
            }
        } else {
            posNotFoundCount = 0;
            if (pos < bar) {
                moveRight();
            } else {
                moveLeft();
            }
        }
    }

    private void exitManaging() {
        totalManagingCount = 0;
        staminaDrainManagingCount = 0;
        posNotFoundCount = 0;
        releaseLeftOrRight();
    }

    private int pixelCount(Image img, Color color) {
        int cnt = 0;
        var imgW = (int) img.getWidth();
        var imgH = (int) img.getHeight();
        var reader = img.getPixelReader();
        for (int x = 0; x < imgW; ++x) {
            for (int y = 0; y < imgH; ++y) {
                var readerColor = reader.getColor(x, y);
                if (Utils.almostEquals(readerColor, color)) {
                    ++cnt;
                }
            }
        }
        return cnt;
    }

    @SuppressWarnings("DuplicatedCode")
    private void moveLeft() {
        if (isPressingLeft) {
            return;
        }
        if (isPressingRight) {
            Utils.execRobot(r -> r.release(rightKey));
            isPressingRight = false;
        }
        isPressingLeft = true;
        Utils.execRobot(r -> r.press(leftKey));
        try {
            Thread.sleep(10);
        } catch (InterruptedException ignore) {
        }
    }

    @SuppressWarnings("DuplicatedCode")
    private void moveRight() {
        if (isPressingRight) {
            return;
        }
        if (isPressingLeft) {
            Utils.execRobot(r -> r.release(leftKey));
            isPressingLeft = false;
        }
        isPressingRight = true;
        Utils.execRobot(r -> r.press(rightKey));
        try {
            Thread.sleep(10);
        } catch (InterruptedException ignore) {
        }
    }

    private void releaseLeftOrRight() {
        if (isPressingLeft) {
            Utils.execRobot(r -> r.release(leftKey));
            isPressingLeft = false;
        }
        if (isPressingRight) {
            Utils.execRobot(r -> r.release(rightKey));
            isPressingRight = false;
        }
    }

    private void pullUp() {
        clickCast();
        setStatus(Status.AFTER_REELING);
    }

    private void finishing() {
        Utils.execRobot(r -> r.mouseMove(config.fishingPoint.x, config.castingPoint.y));
        try {
            Thread.sleep(10);
        } catch (InterruptedException ignore) {
        }
        Utils.execRobot(r -> r.press(new Key(MouseButton.PRIMARY)));
        try {
            Thread.sleep(10);
        } catch (InterruptedException ignore) {
        }
        Utils.execRobot(r -> r.release(new Key(MouseButton.PRIMARY)));

        setStatus(Status.BEGIN);
    }
}
