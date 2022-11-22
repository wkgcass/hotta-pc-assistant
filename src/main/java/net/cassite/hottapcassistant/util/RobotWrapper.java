package net.cassite.hottapcassistant.util;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.robot.Robot;
import javafx.stage.Screen;
import net.cassite.hottapcassistant.entity.Key;

public class RobotWrapper {
    private final Robot robot;

    public RobotWrapper() {
        this.robot = new Robot();
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
        return robot.getScreenCapture(null, screen.getBounds());
    }

    public Image capture(double x, double y, int width, int height) {
        return capture(null, x, y, width, height);
    }

    public Image capture(WritableImage img, double x, double y, int width, int height) {
        Logger.debug("partial capture: (" + x + ", " + y + ") + (" + width + " * " + height + ")");
        return robot.getScreenCapture(img, x, y, width, height);
    }

    public void mouseMove(double x, double y) {
        Logger.debug("mouse move: (" + x + ", " + y + ")");
        robot.mouseMove(x, y);
    }
}
