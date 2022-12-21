package net.cassite.hottapcassistant.util;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.cassite.hottapcassistant.config.TofServerListConfig;
import net.cassite.hottapcassistant.entity.Key;
import net.cassite.hottapcassistant.feed.Feed;
import net.cassite.hottapcassistant.i18n.I18n;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class Utils {
    public static final DecimalFormat floatValueFormat = new DecimalFormat("0.000000");
    public static final DecimalFormat roughFloatValueFormat = new DecimalFormat("0.0");

    private Utils() {
    }

    public static String readValueOf(String key, String line) {
        line = line.trim();
        if (!line.startsWith(key)) {
            return null;
        }
        line = line.substring(key.length()).trim();
        if (!line.startsWith("=")) {
            return null;
        }
        return line.substring(1).trim();
    }

    public static Boolean booleanValue(String v) {
        if (v.equals("False")) {
            return false;
        } else if (v.equals("True")) {
            return true;
        } else {
            return null;
        }
    }

    public static String boolToString(boolean b) {
        return b ? "True" : "False";
    }

    public static void writeFile(Path file, String content) throws IOException {
        Logger.info("write to file: " + file);
        File f = file.toFile();
        if (f.exists()) {
            if (!f.isFile()) {
                throw new IOException(file + " is not a regular file");
            }
            File bak = new File(file + ".bak");
            boolean needBak = true;
            if (bak.exists()) {
                if (bak.isFile()) {
                    try {
                        //noinspection ResultOfMethodCallIgnored
                        bak.delete();
                    } catch (Throwable ignore) {
                    }
                } else {
                    needBak = false; // cannot make backup because the .bak is not a regular file
                }
            }
            if (needBak) {
                try {
                    //noinspection ResultOfMethodCallIgnored
                    f.renameTo(bak);
                } catch (Throwable ignore) {
                }
            }
        } else {
            var parent = f.getParentFile();
            if (!parent.exists()) {
                if (!parent.mkdirs()) {
                    throw new IOException("mkdirs " + parent + " failed");
                }
            }
        }
        Files.writeString(file, content);
    }

    private static volatile RobotWrapper robot;

    public static void execRobot(Consumer<RobotWrapper> f) {
        checkAndInitRobot();
        Platform.runLater(() -> f.accept(robot));
    }

    public static <T> T execRobotOnThread(Function<RobotWrapper, T> f) {
        checkAndInitRobot();
        boolean[] finished = new boolean[]{false};
        Object[] obj = new Object[]{null};
        Runnable r = () -> {
            var v = f.apply(robot);
            obj[0] = v;
            finished[0] = true;
        };
        if (Platform.isFxApplicationThread()) {
            r.run();
        } else {
            Platform.runLater(r);
        }
        while (!finished[0]) {
            try {
                //noinspection BusyWait
                Thread.sleep(1);
            } catch (InterruptedException ignore) {
            }
        }
        //noinspection unchecked
        return (T) obj[0];
    }

    public static void moveAndClickOnThread(double x, double y, Key key) {
        execRobotOnThread(r -> {
            r.mouseMove(x, y);
            return null;
        });
        delay(100);
        clickOnThread(key);
    }

    public static void clickOnThread(Key key) {
        execRobotOnThread(r -> {
            r.press(key);
            return null;
        });
        delay(50);
        execRobotOnThread(r -> {
            r.release(key);
            return null;
        });
    }

    public static void checkAndInitRobot() {
        if (robot == null) {
            synchronized (Utils.class) {
                if (robot == null) {
                    boolean[] done = new boolean[]{false};
                    Throwable[] ex = new Throwable[]{null};
                    Runnable r = () -> {
                        try {
                            robot = new RobotWrapper();
                        } catch (Throwable t) {
                            ex[0] = t;
                        } finally {
                            done[0] = true;
                        }
                    };
                    if (Platform.isFxApplicationThread()) {
                        r.run();
                    } else {
                        Platform.runLater(r);
                    }
                    while (true) {
                        try {
                            //noinspection BusyWait
                            Thread.sleep(1);
                        } catch (InterruptedException ignore) {
                        }
                        if (done[0]) {
                            if (ex[0] != null) {
                                new StackTraceAlert(ex[0]).show();
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    public static Rectangle2D calculateTextBounds(Label label) {
        Text text = new Text(label.getText());
        text.setFont(label.getFont());
        return calculateTextBounds(text);
    }

    public static Rectangle2D calculateTextBounds(Text text) {
        double textWidth;
        double textHeight;
        {
            textWidth = text.getLayoutBounds().getWidth();
            textHeight = text.getLayoutBounds().getHeight();
        }
        return new Rectangle2D(0, 0, textWidth, textHeight);
    }

    public static void runOnFX(Runnable r) {
        if (Platform.isFxApplicationThread()) {
            r.run();
        } else {
            Platform.runLater(r);
        }
    }

    public static boolean almostEquals(Color a, Color b) {
        return Math.abs(a.getRed() - b.getRed()) < 0.02 && Math.abs(a.getGreen() - b.getGreen()) < 0.02 && Math.abs(a.getBlue() - b.getBlue()) < 0.02;
    }

    public static boolean almostIn(Color color, Set<Color> colors) {
        for (var c : colors) {
            if (almostEquals(color, c)) return true;
        }
        return false;
    }

    public static void showWindow(Window window) {
        try {
            ((Stage) window).setIconified(false);
            ((Stage) window).setAlwaysOnTop(true);
            Platform.runLater(() -> ((Stage) window).setAlwaysOnTop(false));
        } catch (Throwable ignore) {
        }
    }

    public static void iconifyWindow(Window window) {
        try {
            ((Stage) window).setIconified(true);
        } catch (Throwable ignore) {
        }
    }

    public static float[] toHSB(Color color) {
        float[] ff = new float[3];
        java.awt.Color.RGBtoHSB((int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255), ff);
        return ff;
    }

    public static String readClassPath(String location) throws IOException {
        try (var inputStream = TofServerListConfig.class.getClassLoader().getResourceAsStream(location)) {
            if (inputStream == null) {
                Logger.warn("unable to find file " + location + " in classpath");
                return "";
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public static Image getWeaponImageFromClasspath(String name) {
        try {
            return ImageManager.get().load("images/weapons/" + name + ".png");
        } catch (Exception e) {
            Logger.error("failed loading image for weapon " + name, e);
            return null;
        }
    }

    public static Image getMatrixImageFromClasspath(String name) {
        try {
            return ImageManager.get().load("images/matrix/" + name + ".png");
        } catch (Exception e) {
            Logger.error("failed loading image for matrix " + name, e);
            return null;
        }
    }

    public static Image getBuffImageFromClasspath(String name) {
        try {
            return ImageManager.get().load("images/buff/" + name + ".png");
        } catch (Exception e) {
            Logger.error("failed loading image for buff " + name, e);
            return null;
        }
    }

    public static Image getRelicsImageFromClasspath(String name) {
        try {
            return ImageManager.get().load("images/relics/" + name + ".png");
        } catch (Exception e) {
            Logger.error("failed loading image for relics " + name, e);
            return null;
        }
    }

    public static long subtractLongGE0(long a, long b) {
        if (a < b) {
            return 0;
        } else {
            return a - b;
        }
    }

    public static boolean checkLock(String name) {
        return checkLock(name, true, true);
    }

    public static boolean checkLock(String name, boolean alert, boolean useFeed) {
        if (useFeed) {
            Boolean ret = null;
            if (name.equals("macro")) {
                ret = Feed.get().lockMacroPane;
            } else if (name.equals("fishing")) {
                ret = Feed.get().lockFishingPane;
            }
            if (ret != null) {
                if (!ret && alert) {
                    new SimpleAlert(Alert.AlertType.WARNING, I18n.get().toolIsLocked(name)).showAndWait();
                }
                return ret;
            }
        }

        InetAddress[] addrs;
        try {
            addrs = InetAddress.getAllByName(name + ".lock.hotta-pc-assistant.special.cassite.net");
        } catch (UnknownHostException ignore) {
            if (alert) {
                new SimpleAlert(Alert.AlertType.WARNING, I18n.get().toolIsLocked(name)).showAndWait();
            }
            return false;
        }
        for (var addr : addrs) {
            var bytes = addr.getAddress();
            if (bytes == null) continue;
            if (bytes.length != 4) continue;
            if (bytes[0] == 2 && bytes[1] == 2 && bytes[2] == 2 && bytes[3] == 2) {
                return true;
            }
        }
        if (alert) {
            new SimpleAlert(Alert.AlertType.WARNING, I18n.get().toolIsLocked(name)).showAndWait();
        }
        return false;
    }

    public static void delay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ignore) {
        }
    }

    public static Mat image2Mat(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        byte[] buffer = new byte[width * height * 4];

        var reader = image.getPixelReader();
        var format = WritablePixelFormat.getByteBgraInstance();
        reader.getPixels(0, 0, width, height, format, buffer, 0, width * 4);

        var mat = new Mat(height, width, CvType.CV_8UC4);
        mat.put(0, 0, buffer);
        return mat;
    }

    public static Mat[] image2MatWithMask(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        var wimg = new WritableImage(width, height);

        var reader = image.getPixelReader();
        var writer = wimg.getPixelWriter();
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int argb = reader.getArgb(x, y);
                if (((argb >> 24) & 0xff) == 0) {
                    writer.setArgb(x, y, 0xff000000);
                } else {
                    writer.setArgb(x, y, 0xffffffff);
                }
            }
        }

        return new Mat[]{image2Mat(image), image2Mat(wimg)};
    }

    public static Image mat2Image(Mat frame) {
        // create a temporary buffer
        var buffer = new MatOfByte();
        // encode the frame in the buffer, according to the PNG format
        Imgcodecs.imencode(".png", frame, buffer);
        // build and return an Image created from the image encoded in the
        // buffer
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }

    // return (x, y, p)
    public static double[] imageTemplateMatching(Image img, Mat subMat) {
        return imageTemplateMatching(img, subMat, null);
    }

    public static double[] imageTemplateMatching(Image img, Mat subMat, Mat mask) {
        var imgMat = Utils.image2Mat(img);
        if (imgMat.rows() < subMat.rows() || imgMat.cols() < subMat.cols()) {
            Logger.warn("invalid input, cannot run template matching");
            return new double[]{0, 0, 0};
        }

        var result = new Mat();
        result.create(imgMat.rows() - subMat.rows() + 1, imgMat.cols() - subMat.cols() + 1, CvType.CV_32FC1);

        if (mask == null) {
            Imgproc.matchTemplate(imgMat, subMat, result, Imgproc.TM_CCORR_NORMED);
        } else {
            Imgproc.matchTemplate(imgMat, subMat, result, Imgproc.TM_CCORR_NORMED, mask);
        }
        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
        var mmr = Core.minMaxLoc(result);

        Logger.info("template matching result: " + mmr.maxLoc.x + ", " + mmr.maxLoc.y + ", " + mmr.maxVal);
        return new double[]{mmr.maxLoc.x, mmr.maxLoc.y, mmr.maxVal};
    }

    public static Screen getScreenOf(Window window) {
        if (window == null) return null;
        var screenOb = Screen.getScreensForRectangle(window.getX(), window.getY(), window.getWidth(), window.getHeight());
        Screen screen;
        if (screenOb.isEmpty()) {
            screen = Screen.getPrimary();
        } else {
            screen = screenOb.get(0);
        }
        if (screen == null) {
            new SimpleAlert(Alert.AlertType.WARNING, "cannot find any display").showAndWait();
            return null;
        }
        return screen;
    }
}
