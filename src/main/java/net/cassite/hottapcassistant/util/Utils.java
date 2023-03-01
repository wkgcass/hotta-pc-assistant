package net.cassite.hottapcassistant.util;

import io.vproxy.base.util.LogType;
import io.vproxy.vfx.entity.input.Key;
import io.vproxy.vfx.manager.audio.AudioGroup;
import io.vproxy.vfx.manager.audio.AudioManager;
import io.vproxy.vfx.manager.audio.AudioWrapper;
import io.vproxy.vfx.manager.image.ImageManager;
import io.vproxy.vfx.ui.alert.SimpleAlert;
import io.vproxy.vfx.ui.alert.StackTraceAlert;
import io.vproxy.commons.util.IOUtils;
import io.vproxy.base.util.Logger;
import io.vproxy.vfx.util.MiscUtils;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.paint.Color;
import net.cassite.hottapcassistant.feed.Feed;
import net.cassite.hottapcassistant.i18n.I18n;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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

    public static boolean modifyHostsFile(Function<List<String>, List<String>> op) {
        var winDir = System.getenv("windir");
        if (winDir == null || winDir.isBlank()) {
            winDir = "C:\\Windows";
        }
        var f = new File(winDir + "\\System32\\Drivers\\etc\\hosts");
        if (!f.exists()) {
            Logger.alert("try to create hosts file: " + f);
            var hostsDir = f.getParentFile();
            if (hostsDir == null) {
                Logger.error(LogType.INVALID_EXTERNAL_DATA, "unable to find directory for hosts file: " + f);
                return false;
            }
            if (!hostsDir.exists()) {
                boolean ok = hostsDir.mkdirs();
                if (!ok) {
                    Logger.error(LogType.FILE_ERROR, "creating directory " + hostsDir + " for hosts file failed");
                    return false;
                }
            }
            boolean ok;
            try {
                ok = f.createNewFile();
            } catch (IOException e) {
                Logger.error(LogType.FILE_ERROR, "creating hosts file failed: " + f, e);
                return false;
            }
            if (!ok) {
                Logger.error(LogType.FILE_ERROR, "creating hosts file failed for unknown reason: " + f);
                return false;
            }
        } else if (!f.isFile()) {
            Logger.error(LogType.INVALID_EXTERNAL_DATA, f.getAbsolutePath() + " is not a file");
            return false;
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(f.toPath());
        } catch (IOException e) {
            Logger.error(LogType.FILE_ERROR, "reading " + f.getAbsolutePath() + " failed", e);
            return false;
        }
        lines = new ArrayList<>(lines);
        var backup = new ArrayList<>(lines);
        lines = op.apply(lines);
        if (lines.equals(backup)) { // no need to update because they are the same
            return true;
        }
        var str = String.join("\n", lines);
        try {
            IOUtils.writeFileWithBackup(f.getAbsolutePath(), str);
        } catch (Exception e) {
            Logger.error(LogType.FILE_ERROR, "writing " + f.getAbsolutePath() + " failed", e);
            return false;
        }
        return true;
    }

    private static volatile RobotWrapper robot;

    public static void execRobot(Consumer<RobotWrapper> f) {
        checkAndInitRobot();
        Platform.runLater(() -> f.accept(robot));
    }

    public static <T> T execRobotOnThread(Function<RobotWrapper, T> f) {
        checkAndInitRobot();
        return runOnFXAndReturn(() -> f.apply(robot));
    }

    public static <T> T execRobotDirectly(Function<RobotWrapper, T> f) {
        checkAndInitRobot();
        return f.apply(robot);
    }

    public static <T> T runOnFXAndReturn(Supplier<T> f) {
        boolean[] finished = new boolean[]{false};
        Object[] obj = new Object[]{null};
        Runnable r = () -> {
            obj[0] = f.get();
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
        MiscUtils.threadSleep(100);
        clickOnThread(key);
    }

    public static void clickOnThread(Key key) {
        execRobotOnThread(r -> {
            r.press(key);
            return null;
        });
        MiscUtils.threadSleep(50);
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
                                StackTraceAlert.show(I18n.get().initRobotFailed(), ex[0]);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    public static String readClassPath(String location) throws IOException {
        try (var inputStream = Utils.class.getClassLoader().getResourceAsStream(location)) {
            if (inputStream == null) {
                Logger.warn(LogType.SYS_ERROR, "unable to find file " + location + " in classpath");
                return "";
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public static Image getWeaponImageFromClasspath(String name) {
        try {
            return ImageManager.get().load("images/weapons/" + name + ".png");
        } catch (Exception e) {
            Logger.error(LogType.SYS_ERROR, "failed loading image for weapon " + name, e);
            return null;
        }
    }

    public static Image getMatrixImageFromClasspath(String name) {
        try {
            return ImageManager.get().load("images/matrix/" + name + ".png");
        } catch (Exception e) {
            Logger.error(LogType.SYS_ERROR, "failed loading image for matrix " + name, e);
            return null;
        }
    }

    public static Image getBuffImageFromClasspath(String name) {
        try {
            return ImageManager.get().load("images/buff/" + name + ".png");
        } catch (Exception e) {
            Logger.error(LogType.SYS_ERROR, "failed loading image for buff " + name, e);
            return null;
        }
    }

    public static Image getRelicsImageFromClasspath(String name) {
        try {
            return ImageManager.get().load("images/relics/" + name + ".png");
        } catch (Exception e) {
            Logger.error(LogType.SYS_ERROR, "failed loading image for relics " + name, e);
            return null;
        }
    }

    public static Image getSkillImageFromClasspath(String name) {
        try {
            return ImageManager.get().load("images/skills/" + name + ".png");
        } catch (Exception e) {
            Logger.error(LogType.SYS_ERROR, "failed loading image for skill " + name, e);
            return null;
        }
    }

    public static AudioWrapper getSkillAudio(String name, int index) {
        String n = "" + index;
        if (index < 10) {
            n = "0" + n;
        }
        var audio = AudioManager.get().loadAudio("/audio/simulacra/" + name + "/skill" + n + ".wav");
        if (audio == null) {
            return null;
        }
        return new AudioWrapper(audio);
    }

    public static AudioGroup getSkillAudioGroup(String name, int to) {
        return getSkillAudioGroup(name, 1, to);
    }

    public static AudioGroup getSkillAudioGroup(String name, int from, int to) {
        var array = new AudioWrapper[to - from + 1];
        for (int i = from; i <= to; ++i) {
            array[i - from] = getSkillAudio(name, i);
        }
        return new AudioGroup(array);
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
                    SimpleAlert.showAndWait(Alert.AlertType.WARNING, I18n.get().toolIsLocked(name));
                }
                return ret;
            }
        }

        InetAddress[] addrs;
        try {
            addrs = InetAddress.getAllByName(name + ".lock.hotta-pc-assistant.special.cassite.net");
        } catch (UnknownHostException ignore) {
            if (alert) {
                SimpleAlert.showAndWait(Alert.AlertType.WARNING, I18n.get().toolIsLocked(name));
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
            SimpleAlert.showAndWait(Alert.AlertType.WARNING, I18n.get().toolIsLocked(name));
        }
        return false;
    }

    public static void copyImageToClipboard(BufferedImage bImg) {
        var content = new ClipboardContent();
        content.putImage(SwingFXUtils.toFXImage(bImg, null));
        Clipboard.getSystemClipboard().setContent(content);
    }

    public static boolean almostIn(Color color, Set<Color> colors) {
        return MiscUtils.almostIn(color, colors, 0.04);
    }

    public static boolean almostEquals(Color a, Color b) {
        return MiscUtils.almostEquals(a, b, 0.04);
    }
}
