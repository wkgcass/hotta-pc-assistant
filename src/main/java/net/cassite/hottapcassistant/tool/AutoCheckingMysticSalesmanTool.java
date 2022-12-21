package net.cassite.hottapcassistant.tool;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import kotlin.Pair;
import net.cassite.hottapcassistant.component.HPadding;
import net.cassite.hottapcassistant.component.setting.Setting;
import net.cassite.hottapcassistant.config.SettingConfig;
import net.cassite.hottapcassistant.entity.Key;
import net.cassite.hottapcassistant.entity.KeyCode;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.ui.EnterCheck;
import net.cassite.hottapcassistant.util.*;
import org.opencv.core.Mat;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AutoCheckingMysticSalesmanTool extends AbstractTool implements Tool, EnterCheck {
    @Override
    public String buildName() {
        return I18n.get().toolName("auto-checking-mystic-salesman");
    }

    @Override
    public Image buildIcon() {
        return ImageManager.get().load("/images/icon/mystic-salesman.jpg");
    }

    @Override
    protected Stage buildStage() {
        var stage = new Stage();
        var pane = new Pane();
        var scene = new Scene(pane);

        var vbox = new VBox();
        pane.getChildren().add(vbox);
        vbox.setAlignment(Pos.CENTER);
        stage.widthProperty().addListener((ob, old, now) -> {
            if (now == null) return;
            vbox.setPrefWidth(now.doubleValue());
        });

        {
            var label = new Label(I18n.get().autoCheckingMysticSalesmanDesc()) {{
                FontManager.setNoto(this);
            }};
            vbox.getChildren().add(label);
        }

        {
            var selectScriptLabel = new Label(I18n.get().autoCheckingMysticSalesmanCallbackScriptLabel()) {{
                FontManager.setFont(this);
            }};
            var selectScriptInput = new TextField() {{
                FontManager.setFont(this);
            }};
            selectScriptInput.setEditable(false);
            selectScriptInput.setPrefWidth(450);
            selectScriptInput.textProperty().addListener((ob, old, now) -> {
                if (now == null) return;
                scriptPath = now;
            });
            var selectScriptButton = new Button(I18n.get().selectButton()) {{
                FontManager.setFont(this);
            }};
            selectScriptButton.setOnAction(e -> {
                var chooser = new FileChooser();
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ps1", "*.ps1"));
                File f = chooser.showOpenDialog(stage);
                if (f == null) {
                    return;
                }
                selectScriptInput.setText(f.getAbsolutePath());
            });
            var selectScriptHBox = new HBox(selectScriptLabel,
                new HPadding(2),
                selectScriptInput,
                new HPadding(2),
                selectScriptButton);
            vbox.getChildren().add(selectScriptHBox);
        }

        {
            var startBtn = new Button(I18n.get().autoCheckingMysticSalesmanStartBtn());
            startBtn.setPrefWidth(120);
            startBtn.setOnAction(e -> start());

            var stopBtn = new Button(I18n.get().autoCheckingMysticSalesmanStopBtn());
            stopBtn.setPrefWidth(120);
            stopBtn.setDisable(true);
            startBtn.setOnAction(e -> stop());

            vbox.getChildren().add(new HBox(startBtn, new HPadding(4), stopBtn));

            state.addListener((ob, old, now) -> {
                if (now == null) return;
                if (now == State.stopped) {
                    startBtn.setDisable(false);
                    stopBtn.setDisable(true);
                } else {
                    startBtn.setDisable(true);
                    stopBtn.setDisable(false);
                }
            });
        }

        stage.setScene(scene);
        stage.setWidth(725);
        stage.setHeight(300);

        return stage;
    }

    private enum State {
        stopped(0, 0),
        timer(17_000, 0),
        openLauncher(0, 0),
        waitForLauncher(1_000, 30),
        launch(0, 0),
        waitForAnnouncement(5_000, 10),
        clickCloseAnnouncement(0, 0),
        waitForLogin(5_00, 10),
        login(0, 0),
        waitForMenu(5_000, 10),
        clickMenu(0, 0),
        waitForHousingChooser(500, 10),
        clickHousing(0, 0),
        waitForHousing(500, 10),
        clickReward(500, 5),
        scanForMystic(500, 5),
        callback(0, 0),
        exit(0, 0),
        exiting(1_000, 20),
        ;
        final int sleepTime;
        final int threshold;

        State(int sleepTime, int threshold) {
            this.sleepTime = sleepTime;
            this.threshold = threshold;
        }
    }

    private String scriptPath;
    private final SimpleObjectProperty<State> state = new SimpleObjectProperty<>(State.stopped);
    private Thread thread;
    private Screen screen;

    private void start() {
        if (state.get() != State.stopped) return;
        if (!GlobalValues.checkGamePath()) {
            return;
        }
        if (scriptPath == null || scriptPath.isBlank()) {
            new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().autoCheckingMysticSalesmanScriptPathNotSet());
            return;
        }

        screen = Utils.getScreenOf(stage);
        if (screen == null) {
            Logger.error("cannot find screen when starting auto-checking-mystic-salesman");
            return;
        }

        if (!prepareConfig()) {
            return;
        }

        state.set(State.openLauncher);
        var thread = new Thread(this::run);
        this.thread = thread;
        thread.start();
    }

    private boolean prepareConfig() {
        var settingsPath = Path.of(GlobalValues.savedPath.get(), "Config", "WindowsNoEditor", "GameUserSettings.ini").toString();
        var config = SettingConfig.ofSaved(settingsPath, GlobalValues.getGameAssistantConfig());
        List<Setting> settings;
        try {
            settings = config.read();
        } catch (IOException e) {
            Logger.error("failed reading settings config", e);
            new SimpleAlert(Alert.AlertType.ERROR, I18n.get().autoCheckingMysticSalesmanFailedReadingSettingsConfig());
            return false;
        }
        for (var s : settings) {
            if ("ResolutionSizeX".equals(s.name)) {
                s.value = 1280;
            } else if ("ResolutionSizeY".equals(s.name)) {
                s.value = 720;
            } else if ("FullscreenMode".equals(s.name)) {
                s.value = 2;
            }
        }
        try {
            config.write(settings);
        } catch (IOException e) {
            Logger.error("failed writing settings config", e);
            new SimpleAlert(Alert.AlertType.ERROR, I18n.get().autoCheckingMysticSalesmanFailedWritingSettingsConfig());
            return false;
        }
        var configPath = Path.of(GlobalValues.gamePath.get(), "WmGpLaunch", "UserData", "Config", "Config.ini");
        List<String> configIni = null;
        try {
            configIni = Files.readAllLines(configPath);
        } catch (IOException e) {
            Logger.error("failed reading config.ini", e);
            // fallthrough
        }
        if (configIni != null) {
            boolean modified = false;
            for (int i = 0; i < configIni.size(); i++) {
                String line = configIni.get(i);
                if (line.isBlank()) continue;
                line = line.trim();
                if (!line.startsWith("Resolution_0")) continue;
                line = line.substring("Resolution_0".length()).trim();
                if (!line.startsWith("=")) continue;
                line = line.substring(1).trim();
                if (!line.equals("1280x720")) {
                    configIni.set(i, "Resolution_0=1280x720");
                    modified = true;
                }
                break;
            }
            if (modified) {
                try {
                    Files.writeString(configPath, String.join("\n", configIni));
                } catch (IOException e) {
                    Logger.error("failed saving config.ini", e);
                    // fallthrough
                }
            }
        }
        return true;
    }

    private void stop() {
        state.set(State.stopped);
        var thread = this.thread;
        this.thread = null;
        if (thread != null) {
            thread.interrupt();
        }
    }

    private void run() {
        Logger.info("auto checking mystics salesman thread begin");

        int[] counter = new int[]{0};
        state.addListener((ob, old, now) -> {
            if (old != now) counter[0] = 0;
        });

        while (true) {
            if (state.get() == State.stopped) {
                break;
            }

            if (state.get() != State.timer) {
                waitForLauncher(); // the game might crash at any step, if crashes, go back to the launcher step
            }

            if (state.get() == State.stopped) {
                break;
            }

            switch (state.get()) {
                case timer -> timer();
                case openLauncher -> openLauncher();
                case waitForLauncher -> waitForLauncher();
                case launch -> doLaunch();
                case waitForAnnouncement -> waitForAnnouncement();
                case clickCloseAnnouncement -> clickCloseAnnouncement();
                case waitForLogin -> waitForLogin();
                case login -> login();
                case waitForMenu -> waitForMenu();
                case clickMenu -> clickMenu();
                case waitForHousingChooser -> waitForHousingChooser();
                case clickHousing -> clickHousing();
                case waitForHousing -> waitForHousing();
                case clickReward -> clickReward();
                case scanForMystic -> scanForMystic();
                case callback -> callback();
                case exit -> exit();
                case exiting -> exiting();
            }

            if (state.get() == State.stopped) {
                break;
            }

            if (counter[0] > state.get().threshold) {
                switch (state.get()) {
                    case waitForLauncher -> waitForLauncherTimeout();
                    case waitForAnnouncement -> waitForAnnouncementTimeout();
                    case waitForLogin -> waitForLoginTimeout();
                    case waitForMenu -> waitForMenuTimeout();
                    case waitForHousingChooser -> waitForHousingChooserTimeout();
                    case waitForHousing -> waitForHousingTimeout();
                    case clickReward -> clickRewardTimeout();
                    case scanForMystic -> scanForMysticTimeout();
                    case exiting -> exitingTimeout();
                }
            }

            if (state.get() == State.stopped) {
                break;
            }

            try {
                //noinspection BusyWait
                Thread.sleep(state.get().sleepTime);
            } catch (InterruptedException ignore) {
            }
        }

        Logger.info("auto checking mystics salesman thread end");
    }

    private static final Mat launcherStartButton1x = loadMat("launcher-start-button-1x");
    private static final Mat launcherStartButton15x = loadMat("launcher-start-button-1.5x");
    private static final Mat launcherStartButton2x = loadMat("launcher-start-button-2x");
    private static final Mat announcementCloseButton = loadMat("announcement-close-button");
    private static final Mat loginScreen = loadMat("login-screen");
    private static final Mat[] menuIcon = loadMatWithMask("menu-icon");
    private static final Mat housingButton = loadMat("housing-button");
    private static final Mat housingLinImage = loadMat("housing-lin");
    private static final Mat[] rewardButton = loadMatWithMask("reward-button");
    private static final Mat[] mysticSalesmanIcon = loadMatWithMask("mystic-salesman-icon");

    private static Mat loadMat(String name) {
        var img = ImageManager.get().load("/images/templates/" + name);
        return Utils.image2Mat(img);
    }

    private static Mat[] loadMatWithMask(String name) {
        var img = ImageManager.get().load("/images/templates/" + name);
        return Utils.image2MatWithMask(img);
    }

    private double[] scanFor(Mat[] mat, double threshold) {
        var img = Utils.execRobotOnThread(r -> r.captureScreen(screen));
        var match = Utils.imageTemplateMatching(img, mat[0], mat[1]);
        if (match[2] < threshold) {
            return null;
        }
        return match;
    }

    private double[] scanFor(Mat[] mats) {
        return scanFor(mats, 0.999);
    }

    private double[] scanFor(Mat mat, double threshold) {
        return scanFor(new Mat[]{mat, null}, threshold);
    }

    private double[] scanFor(Mat mat) {
        return scanFor(mat, 0.999);
    }

    private void killGameProcess() {
        try {
            Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", "taskkill /IM \"QRSL.exe\" /F"});
        } catch (IOException e) {
            Logger.error("failed to kill game process", e);
        }
    }

    private void timer() {
        var now = ZonedDateTime.now();
        if (now.getMinute() == 1) {
            state.set(State.openLauncher);
        } else { // keep screen on
            var pos = Utils.execRobotOnThread(RobotWrapper::getMousePosition);
            var d = ThreadLocalRandom.current().nextDouble();
            if (d < 0.5) {
                Utils.execRobot(r -> r.mouseMove(pos.getX() - 1, pos.getY() - 1));
            } else {
                Utils.execRobot(r -> r.mouseMove(pos.getX() + 1, pos.getY() + 1));
            }
        }
    }

    private void openLauncher() {
        try {
            Desktop.getDesktop().open(Path.of(GlobalValues.gamePath.get(), "gameLauncher.exe").toFile());
        } catch (Throwable t) {
            Logger.error("failed launching game", t);
            stop();
            return;
        }
        state.set(State.waitForLauncher);
    }

    private void clickAt(double[] pos, Mat mat) {
        Utils.moveAndClickOnThread(pos[0] + mat.cols() / 2d, pos[1] + mat.rows() / 2d, new Key(MouseButton.PRIMARY));
    }

    private Pair<double[], Mat> scanForLauncherStartButton() {
        var pos = scanFor(launcherStartButton1x);
        if (pos != null) return new Pair<>(pos, launcherStartButton1x);
        pos = scanFor(launcherStartButton15x);
        if (pos != null) return new Pair<>(pos, launcherStartButton15x);
        pos = scanFor(launcherStartButton2x);
        if (pos != null) return new Pair<>(pos, launcherStartButton2x);
        return null;
    }

    private void waitForLauncher() {
        if (scanForLauncherStartButton() != null) {
            state.set(State.launch);
        }
    }

    private void waitForLauncherTimeout() {
        Logger.warn("waiting for launcher timeout");
        state.set(State.openLauncher);
    }

    private void doLaunch() {
        var pos = scanForLauncherStartButton();
        if (pos == null) {
            state.set(State.waitForLauncher);
        } else {
            clickAt(pos.component1(), pos.component2());
            state.set(State.waitForLogin);
        }
    }

    private void waitForAnnouncement() {
        if (scanFor(announcementCloseButton) != null) {
            state.set(State.clickCloseAnnouncement);
        }
    }

    private void waitForAnnouncementTimeout() {
        Logger.warn("waiting for announcement timeout");
        state.set(State.exit);
    }

    private void clickCloseAnnouncement() {
        var pos = scanFor(announcementCloseButton);
        if (pos == null) {
            state.set(State.waitForAnnouncement);
        } else {
            clickAt(pos, announcementCloseButton);
        }
    }

    private void waitForLogin() {
        if (scanFor(loginScreen, 0.9) != null) {
            state.set(State.login);
        }
    }

    private void waitForLoginTimeout() {
        Logger.warn("waiting for login timeout");
        state.set(State.exit);
    }

    private void login() {
        var pos = scanFor(loginScreen);
        if (pos == null) {
            state.set(State.waitForLogin);
        } else {
            clickAt(pos, loginScreen);
            state.set(State.waitForMenu);
        }
    }

    private void waitForMenu() {
        Utils.clickOnThread(new Key(KeyCode.ESCAPE));
        Utils.delay(500);
        if (scanFor(menuIcon) != null) {
            state.set(State.clickMenu);
        }
    }

    private void waitForMenuTimeout() {
        Logger.warn("waiting for menu timeout");
        state.set(State.exit);
    }

    private void clickMenu() {
        var pos = scanFor(menuIcon);
        if (pos == null) {
            state.set(State.waitForMenu);
        } else {
            clickAt(pos, menuIcon[0]);
            state.set(State.waitForHousingChooser);
        }
    }

    private void waitForHousingChooser() {
        if (scanFor(housingButton) != null) {
            state.set(State.clickHousing);
        }
    }

    private void waitForHousingChooserTimeout() {
        Logger.warn("waiting for housing chooser timeout");
        state.set(State.exit);
    }

    private void clickHousing() {
        var pos = scanFor(housingButton);
        if (pos == null) {
            state.set(State.waitForHousingChooser);
        } else {
            clickAt(pos, housingButton);
            state.set(State.waitForHousing);
        }
    }

    private void waitForHousing() {
        if (scanFor(housingLinImage) != null) {
            state.set(State.clickReward);
        }
    }

    private void waitForHousingTimeout() {
        Logger.warn("waiting for housing timeout");
        state.set(State.exit);
    }

    private void clickReward() {
        var pos = scanFor(rewardButton);
        if (pos != null) {
            clickAt(pos, rewardButton[0]);
            state.set(State.scanForMystic);
        }
    }

    private void clickRewardTimeout() {
        Logger.warn("unable to find reward button");
        state.set(State.scanForMystic);
    }

    private void scanForMystic() {
        var pos = scanFor(mysticSalesmanIcon);
        if (pos != null) {
            Logger.info("mystic salesman is found!!");
            state.set(State.callback);
        }
    }

    private void scanForMysticTimeout() {
        Logger.info("mystic salesman is not showing");
        state.set(State.exit);
    }

    private void callback() {
        var scriptPath = this.scriptPath;
        if (scriptPath == null || scriptPath.isBlank()) {
            Logger.warn("script path is null, skip calling script");
        } else {
            try {
                Runtime.getRuntime().exec(new String[]{
                    "C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe",
                    scriptPath,
                });
                Logger.info("executing script succeeded: " + scriptPath);
            } catch (IOException e) {
                Logger.error("failed executing script: " + scriptPath, e);
            }
        }
        state.set(State.exit);
    }

    private void exit() {
        killGameProcess();
        state.set(State.exiting);
    }

    private void exiting() {
        if (scanForLauncherStartButton() != null) {
            state.set(State.launch);
        }
    }

    private void exitingTimeout() {
        Logger.warn("exiting timeout");
        state.set(State.launch);
    }

    @Override
    protected void terminate0() {
        stop();
        scriptPath = null;
    }

    @Override
    public boolean enterCheck(boolean skipGamePathCheck) {
        return GlobalValues.checkGamePath();
    }
}
