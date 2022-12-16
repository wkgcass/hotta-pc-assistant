package net.cassite.hottapcassistant.util;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.robot.Robot;
import javafx.stage.Screen;
import net.cassite.hottapcassistant.entity.Key;

import java.awt.*;

public class RobotWrapper {
    private final Robot robot;
    private final java.awt.Robot awtRobot;

    public RobotWrapper() {
        this.robot = new Robot();
        java.awt.Robot awtRobot;
        try {
            awtRobot = new java.awt.Robot();
        } catch (AWTException e) {
            Logger.error("failed creating awt robot", e);
            awtRobot = null;
        }
        this.awtRobot = awtRobot;
    }

    public void press(Key key) {
        if (key.button != null) {
            robot.mousePress(key.button);
            Logger.debug("mouse press: " + key);
        } else if (key.key != null) {
            robot.keyPress(key.key.java);
            Logger.debug("key press: " + key);
        }
    }

    public void release(Key key) {
        if (key.button != null) {
            robot.mouseRelease(key.button);
            Logger.debug("mouse release: " + key);
        } else if (key.key != null) {
            robot.keyRelease(key.key.java);
            Logger.debug("key release: " + key);
        }
    }

    public Image captureScreen(Screen screen) {
        Logger.debug("screen capture: " + screen);
        var bounds = screen.getBounds();
        return capture0(null, bounds.getMinX(), bounds.getMinY(), (int) bounds.getWidth(), (int) bounds.getHeight());
    }

    public Image capture(double x, double y, int width, int height) {
        return capture(null, x, y, width, height);
    }

    public Image capture(WritableImage img, double x, double y, int width, int height) {
        Logger.debug("partial capture: (" + x + ", " + y + ") + (" + width + " * " + height + ")");
        return capture0(img, x, y, width, height);
    }

    private Image capture0(WritableImage img, double x, double y, int width, int height) {
        if (awtRobot != null) {
            var mi = awtRobot.createMultiResolutionScreenCapture(new Rectangle((int) x, (int) y, width, height));
            var ls = mi.getResolutionVariants();
            if (!ls.isEmpty()) {
                var i = ls.get(ls.size() - 1);
                boolean useAwt = false;
                if (img == null) {
                    useAwt = true;
                } else {
                    var w = i.getWidth(null);
                    var h = i.getHeight(null);
                    if (w <= img.getWidth() && h <= img.getHeight()) {
                        useAwt = true;
                    }
                }
                if (useAwt) {
                    Logger.info("using awt captured image");
                    return SwingFXUtils.toFXImage(Utils.convertToBufferedImage(i), img);
                }
            }
        }
        Logger.info("using javafx captured image");
        return robot.getScreenCapture(img, x, y, width, height);
    }

    public java.awt.Image awtCapture(int x, int y, int width, int height) {
        var mi = awtRobot.createMultiResolutionScreenCapture(new Rectangle(x, y, width, height));
        var ls = mi.getResolutionVariants();
        if (ls.isEmpty()) return null;
        return ls.get(ls.size() - 1);
    }

    public void mouseMove(double x, double y) {
        Logger.debug("mouse move: (" + x + ", " + y + ")");
        robot.mouseMove(x, y);
    }

    public Point2D getMousePosition() {
        return robot.getMousePosition();
    }
}
