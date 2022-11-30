package net.cassite.hottapcassistant.util;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.cassite.hottapcassistant.config.TofServerListConfig;

import java.io.File;
import java.io.IOException;
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
        double textWidth;
        double textHeight;
        {
            Text text = new Text(label.getText());
            text.setFont(label.getFont());
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
}
