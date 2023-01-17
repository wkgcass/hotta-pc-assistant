package net.cassite.hottapcassistant.util;

import io.vproxy.vfx.util.FXUtils;
import io.vproxy.vfx.util.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class RobotWrapper extends io.vproxy.vfx.robot.RobotWrapper {
    private static final Set<String> nativeCaptureErrors = new HashSet<>();

    public RobotWrapper() {
        super();
    }

    public RobotWrapper(boolean log) {
        super(log);
    }

    public BufferedImage nativeCapture(int x, int y, int width, int height, double scale) {
        BufferedImage bi = null;
        try {
            double doubleWidth = width * scale;
            int intWidth = (int) doubleWidth;
            if (doubleWidth != intWidth) {
                ++intWidth;
            }
            double doubleHeight = height * scale;
            int intHeight = (int) doubleHeight;
            if (doubleHeight != intHeight) {
                ++intHeight;
            }
            bi = JNAScreenShot.getScreenshot(new Rectangle((int) (x * scale), (int) (y * scale), intWidth, intHeight));
        } catch (Throwable t) {
            String msg = t.getMessage();
            if (msg == null) msg = "";
            if (nativeCaptureErrors.add(msg)) {
                Logger.error("failed to capture using jna", t);
            }
        }
        if (bi == null) {
            if (nativeCaptureErrors.add("null")) {
                Logger.error("failed to capture using jna: result is null");
            }
        }
        if (bi == null) {
            bi = FXUtils.convertToBufferedImage(awtCapture(x, y, width, height));
        } else {
            if (nativeCaptureErrors.add("x")) {
                Logger.info("native capture succeeded: " + bi);
            }
        }
        return bi;
    }
}
