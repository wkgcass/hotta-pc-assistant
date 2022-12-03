package net.cassite.hottapcassistant.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;
import net.cassite.hottapcassistant.component.HPadding;
import net.cassite.hottapcassistant.component.ImageButton;
import net.cassite.hottapcassistant.component.VPadding;
import net.cassite.hottapcassistant.component.serverlist.UIServerChooser;
import net.cassite.hottapcassistant.config.AssistantConfig;
import net.cassite.hottapcassistant.config.TofServerListConfig;
import net.cassite.hottapcassistant.entity.*;
import net.cassite.hottapcassistant.feed.Feed;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.FontManager;
import net.cassite.hottapcassistant.util.GlobalValues;
import net.cassite.hottapcassistant.util.Logger;
import net.cassite.hottapcassistant.util.SimpleAlert;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.*;

public class WelcomePane extends StackPane {
    private final CheckBox useCNGameCheckBox = new CheckBox();
    private final CheckBox useGlobalGameCheckBox = new CheckBox();
    private final List<CheckBox> checkboxes = new ArrayList<>() {{
        add(useCNGameCheckBox);
        add(useGlobalGameCheckBox);
    }};
    private final TextField selectGameLocationInput;
    private final TextField selectSavedLocationInput;
    private final TextField selectGlobalServerGameLocationInput;

    private boolean isAltDown = false;

    public WelcomePane() {
        setBackground(new Background(new BackgroundImage(
            new Image("images/bg/bg0.jpg"),
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            BackgroundSize.DEFAULT
        )));
        Feed.updated.addListener((ob, old, now) -> updateBg());
        updateBg();
        setAlignment(Pos.CENTER);

        var vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);
        getChildren().add(vbox);

        {
            vbox.setPadding(new Insets(50, 0, 0, 0));
        }

        {
            var selectLocationGroup = new VBox();
            var selectLocationDesc = new Label(I18n.get().selectSavedLocationDescription()) {{
                FontManager.setFont(this);
            }};
            selectLocationGroup.setAlignment(Pos.CENTER);
            selectLocationGroup.getChildren().add(selectLocationDesc);
            var selectLocationLabel = new Label(I18n.get().selectSavedLocation()) {{
                FontManager.setFont(this);
            }};
            selectSavedLocationInput = new TextField() {{
                FontManager.setFont(this);
            }};
            selectSavedLocationInput.setEditable(false);
            selectSavedLocationInput.setPrefWidth(500);
            selectSavedLocationInput.textProperty().addListener((ob, old, now) -> {
                if (now == null) return;
                GlobalValues.savedPath.set(now);
                try {
                    AssistantConfig.updateAssistant(a -> {
                        if (a.lastValues == null) a.lastValues = AssistantLastValues.empty();
                        a.lastValues.savedPath = now;
                    });
                } catch (IOException e) {
                    Logger.error("failed updating assistant config", e);
                }
                if (checkCurrentGameVersion()) {
                    swapSavedIfPossible();
                }
            });
            var selectLocationButton = new Button(I18n.get().selectButton()) {{
                FontManager.setFont(this);
            }};
            selectLocationButton.setOnAction(e -> {
                var chooser = new DirectoryChooser();
                File f = chooser.showDialog(this.getScene().getWindow());
                if (f == null) {
                    return;
                }
                if (!f.isDirectory()) {
                    new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().chosenWrongSavedDirectory()).showAndWait();
                    return;
                }
                selectSavedLocationInput.setText(f.getAbsolutePath());
            });
            var selectLocationAutoButton = new Button(I18n.get().autoSearchButton()) {{
                FontManager.setFont(this);
            }};
            selectLocationAutoButton.setOnAction(e -> autoSearchSavedPath(true));
            var selectLocationHBox = new HBox(selectLocationLabel,
                new HPadding(2),
                selectSavedLocationInput,
                new HPadding(2),
                selectLocationButton,
                new HPadding(2),
                selectLocationAutoButton);
            selectLocationHBox.setAlignment(Pos.CENTER);
            selectLocationGroup.getChildren().add(selectLocationHBox);
            vbox.getChildren().add(selectLocationGroup);
        }

        {
            vbox.getChildren().add(new VPadding(10));
        }

        {
            var selectLocationGroup = new VBox();
            useCNGameCheckBox.setDisable(true);
            useCNGameCheckBox.setSelected(true);
            useCNGameCheckBox.selectedProperty().addListener((ob, old, now) -> {
                if (now == null) return;
                if (Objects.equals(old, now)) return;
                if (now) {
                    GlobalValues.useVersion.set(GameVersion.CN);
                    unselectAllCheckBoxesExcept(useCNGameCheckBox);
                    swapSavedIfPossible();
                }
            });
            var selectLocationDesc = new Label(I18n.get().selectGameLocationDescription()) {{
                FontManager.setFont(this);
            }};
            selectLocationGroup.setAlignment(Pos.CENTER);
            selectLocationGroup.getChildren().add(selectLocationDesc);
            var selectLocationLabel = new Label(I18n.get().selectGameLocation()) {{
                FontManager.setFont(this);
            }};
            selectGameLocationInput = new TextField() {{
                FontManager.setFont(this);
            }};
            selectGameLocationInput.setEditable(false);
            selectGameLocationInput.setPrefWidth(500);
            selectGameLocationInput.textProperty().addListener((ob, old, now) -> {
                if (now == null) return;
                GlobalValues.gamePath.set(now);
                useCNGameCheckBox.setDisable(false);
                try {
                    AssistantConfig.updateAssistant(a -> {
                        if (a.lastValues == null) a.lastValues = AssistantLastValues.empty();
                        a.lastValues.gamePath = now;
                    });
                } catch (IOException e) {
                    Logger.error("failed updating assistant config", e);
                }
            });
            var selectLocationButton = new Button(I18n.get().selectButton()) {{
                FontManager.setFont(this);
            }};
            selectLocationButton.setOnAction(e -> {
                var chooser = new FileChooser();
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("exe", "*.exe"));
                File f = chooser.showOpenDialog(this.getScene().getWindow());
                if (f == null) {
                    return;
                }
                if (!f.getName().equalsIgnoreCase("gameLauncher.exe")) {
                    new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().chosenWrongGameFile()).showAndWait();
                    return;
                }
                if (!f.isFile()) {
                    new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().chosenWrongGameFile()).showAndWait();
                    return;
                }
                selectGameLocationInput.setText(f.getParentFile().getAbsolutePath());
            });
            var selectLocationAutoButton = new Button(I18n.get().autoSearchButton()) {{
                FontManager.setFont(this);
            }};
            selectLocationAutoButton.setOnAction(e -> autoSearchGamePath(true));
            var selectLocationHBox = new HBox(
                useCNGameCheckBox,
                new HPadding(2),
                selectLocationLabel,
                new HPadding(2),
                selectGameLocationInput,
                new HPadding(2),
                selectLocationButton,
                new HPadding(2),
                selectLocationAutoButton);
            selectLocationHBox.setAlignment(Pos.CENTER);
            selectLocationGroup.getChildren().add(selectLocationHBox);
            vbox.getChildren().add(selectLocationGroup);
        }

        {
            vbox.getChildren().add(new VPadding(10));
        }

        {
            var selectLocationGroup = new VBox();
            useGlobalGameCheckBox.setDisable(true);
            useGlobalGameCheckBox.selectedProperty().addListener((ob, old, now) -> {
                if (now == null) return;
                if (Objects.equals(old, now)) return;
                if (now) {
                    GlobalValues.useVersion.set(GameVersion.Global);
                    unselectAllCheckBoxesExcept(useGlobalGameCheckBox);
                    swapSavedIfPossible();
                }
            });
            var selectLocationDesc = new Label(I18n.get().selectGlobalServerGameLocationDescription()) {{
                FontManager.setFont(this);
            }};
            selectLocationGroup.setAlignment(Pos.CENTER);
            selectLocationGroup.getChildren().add(selectLocationDesc);
            var selectLocationLabel = new Label(I18n.get().selectGlobalServerGameLocation()) {{
                FontManager.setFont(this);
            }};
            selectGlobalServerGameLocationInput = new TextField() {{
                FontManager.setFont(this);
            }};
            selectGlobalServerGameLocationInput.setEditable(false);
            selectGlobalServerGameLocationInput.setPrefWidth(500);
            selectGlobalServerGameLocationInput.textProperty().addListener((ob, old, now) -> {
                if (now == null) return;
                GlobalValues.globalServerGamePath.set(now);
                useGlobalGameCheckBox.setDisable(false);
                if (selectGameLocationInput.getText().isBlank()) {
                    useGlobalGameCheckBox.setSelected(true);
                }
                try {
                    AssistantConfig.updateAssistant(a -> {
                        if (a.lastValues == null) a.lastValues = AssistantLastValues.empty();
                        a.lastValues.globalServerGamePath = now;
                    });
                } catch (IOException e) {
                    Logger.error("failed updating assistant config", e);
                }
            });
            var selectLocationButton = new Button(I18n.get().selectButton()) {{
                FontManager.setFont(this);
            }};
            selectLocationButton.setOnAction(e -> {
                var chooser = new FileChooser();
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("exe", "*.exe"));
                File f = chooser.showOpenDialog(this.getScene().getWindow());
                if (f == null) {
                    return;
                }
                if (!f.getName().equalsIgnoreCase("tof_launcher.exe")) {
                    new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().chosenWrongGlobalServerGameFile()).showAndWait();
                    return;
                }
                if (!f.isFile()) {
                    new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().chosenWrongGlobalServerGameFile()).showAndWait();
                    return;
                }
                var launcherDir = f.getParentFile();
                var tofDir = launcherDir.getParentFile();
                if (tofDir == null) {
                    new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().chosenWrongGlobalServerGameFileNoParentDir()).showAndWait();
                    return;
                }
                selectGlobalServerGameLocationInput.setText(tofDir.getAbsolutePath());
            });
            var selectLocationAutoButton = new Button(I18n.get().autoSearchButton()) {{
                FontManager.setFont(this);
            }};
            selectLocationAutoButton.setOnAction(e -> autoSearchGlobalServerGamePath(true));
            var selectLocationHBox = new HBox(
                useGlobalGameCheckBox,
                new HPadding(2),
                selectLocationLabel,
                new HPadding(2),
                selectGlobalServerGameLocationInput,
                new HPadding(2),
                selectLocationButton,
                new HPadding(2),
                selectLocationAutoButton);
            selectLocationHBox.setAlignment(Pos.CENTER);
            selectLocationGroup.getChildren().add(selectLocationHBox);
            vbox.getChildren().add(selectLocationGroup);
        }

        {
            var sep1 = new Separator();
            sep1.setPadding(new Insets(50, 0, 50, 0));
            vbox.getChildren().add(sep1);
        }

        {
            var group = new Group();
            vbox.getChildren().add(group);

            var downloadBtn = new ImageButton("images/downloadgame-btn/downloadgame", "png");
            downloadBtn.setScale(0.6);
            downloadBtn.setOnAction(e -> {
                var url = Feed.get().pmpDownloadUrl;
                if (url == null) {
                    url = "https://pmpcdn1.wmupd.com/pmp/client/PMP_1.0.7.0125.exe";
                }
                try {
                    Desktop.getDesktop().browse(new URL(url).toURI());
                } catch (Throwable t) {
                    Logger.error("failed downloading game", t);
                    Clipboard.getSystemClipboard().setContent(Map.of(DataFormat.PLAIN_TEXT, url));
                    new SimpleAlert(Alert.AlertType.ERROR, I18n.get().openBrowserForDownloadingFailed(url)).showAndWait();
                }
            });

            var launchBtn = new ImageButton("images/launchgame-btn/launchgame", "png");
            launchBtn.setScale(0.6);
            launchBtn.setOnAction(e -> {
                if (!GlobalValues.checkCNGamePath()) {
                    return;
                }
                useCNGameCheckBox.setSelected(true);
                try {
                    Desktop.getDesktop().open(Path.of(GlobalValues.gamePath.get(), "gameLauncher.exe").toFile());
                } catch (Throwable t) {
                    Logger.error("failed launching game", t);
                    new SimpleAlert(Alert.AlertType.ERROR, I18n.get().launchGameFailed()).showAndWait();
                }
            });
            group.getChildren().add(downloadBtn);
            selectGameLocationInput.textProperty().addListener((ob, old, now) -> {
                group.getChildren().clear();
                if (now != null && !now.isBlank()) {
                    group.getChildren().add(launchBtn);
                } else {
                    group.getChildren().add(downloadBtn);
                }
            });
        }

        {
            var sep1 = new Separator();
            sep1.setPadding(new Insets(50, 0, 50, 0));
            vbox.getChildren().add(sep1);
        }

        {
            var group = new Group();
            vbox.getChildren().add(group);

            var downloadBtn = new ImageButton("images/global-download-btn/download", "png");
            downloadBtn.setScale(0.5);
            downloadBtn.setOnAction(e -> {
                var url = Feed.get().tofMiniLoaderUrl;
                if (url == null) {
                    url = "https://www.toweroffantasy-global.com/download/TofMiniLoader_official.wg.intl.exe";
                }
                try {
                    Desktop.getDesktop().browse(new URL(url).toURI());
                } catch (Throwable t) {
                    Logger.error("failed downloading global server game", t);
                    Clipboard.getSystemClipboard().setContent(Map.of(DataFormat.PLAIN_TEXT, url));
                    new SimpleAlert(Alert.AlertType.ERROR, I18n.get().openBrowserForDownloadingFailed(url)).showAndWait();
                }
            });

            var launchBtn = new ImageButton("images/global-launch-btn/launch", "png");
            launchBtn.setScale(0.5);
            launchBtn.setOnAction(e -> launchGlobalServer(isAltDown));
            group.getChildren().add(downloadBtn);
            selectGlobalServerGameLocationInput.textProperty().addListener((ob, old, now) -> {
                group.getChildren().clear();
                if (now != null && !now.isBlank()) {
                    group.getChildren().add(launchBtn);
                } else {
                    group.getChildren().add(downloadBtn);
                }
            });
        }

        setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ALT) {
                isAltDown = true;
            }
        });
        setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ALT) {
                isAltDown = false;
            }
        });

        Platform.runLater(this::initLocations);
    }

    private boolean checkCurrentGameVersion() {
        GameAssistant config;
        try {
            config = GlobalValues.getGameAssistantConfig().readGameAssistant();
        } catch (IOException e) {
            Logger.error("failed retrieving assistant config when checking version", e);
            return false;
        }
        if (config.version != null) {
            return true;
        }
        var dialog = new Dialog<ButtonType>();
        dialog.initStyle(StageStyle.UTILITY);
        dialog.getDialogPane().setContent(new Label(I18n.get().chooseGameVersionDesc()) {{
            FontManager.setFont(this);
        }});
        var cnVersion = new ButtonType(I18n.get().chooseCNVersion(), ButtonBar.ButtonData.OK_DONE);
        var globalVersion = new ButtonType(I18n.get().chooseGlobalVersion(), ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(cnVersion, globalVersion);

        var resOpt = dialog.showAndWait();
        if (resOpt.isEmpty()) {
            return false;
        }
        var res = resOpt.get();
        if (res == cnVersion) {
            config.version = GameVersion.CN;
        } else if (res == globalVersion) {
            config.version = GameVersion.Global;
        } else {
            return false;
        }

        try {
            GlobalValues.getGameAssistantConfig().writeGameAssistant(config);
        } catch (IOException e) {
            Logger.error("failed writing assistant config when checking version", e);
            return false;
        }
        return true;
    }

    private void unselectAllCheckBoxesExcept(CheckBox box) {
        for (var c : checkboxes) {
            if (c == box) continue;
            c.setSelected(false);
        }
    }

    private void swapSavedIfPossible() {
        if (GlobalValues.savedPath.get() == null) {
            return;
        }
        boolean swapped;
        try {
            swapped = GlobalValues.swapConfig();
        } catch (Exception e) {
            new SimpleAlert(Alert.AlertType.ERROR, I18n.get().swapConfigFailed()).showAndWait();
            return;
        }
        if (swapped) {
            var config = GlobalValues.getGameAssistantConfig();
            try {
                config.updateGameAssistant(a -> a.version = GlobalValues.useVersion.get());
            } catch (IOException e) {
                Logger.error("trying to update swapped assistant file failed", e);
            }
        }
    }

    private void launchGlobalServer(boolean showHostsHack) {
        if (!GlobalValues.checkGlobalServerGamePath()) {
            return;
        }
        useGlobalGameCheckBox.setSelected(true);

        if (showHostsHack) {
            List<TofServer> servers = null;
            List<String> selectedServers = null;
            try {
                servers = TofServerListConfig.read();
            } catch (IOException e) {
                Logger.error("failed reading tof server list", e);
                new SimpleAlert(Alert.AlertType.ERROR, I18n.get().failedReadingTofServerList()).showAndWait();
            }
            try {
                var ass = AssistantConfig.readAssistant();
                selectedServers = ass.lastValues.requireWritingHostsFileServerNames;
            } catch (IOException e) {
                Logger.error("failed reading enabled servers from last config", e);
            }
            if (selectedServers == null) selectedServers = Collections.emptyList();
            final var fSelectedServers = selectedServers;
            if (servers != null) {
                servers.forEach(e -> {
                    if (fSelectedServers.contains(e.name)) {
                        e.selected = true;
                    }
                });
                var res = new UIServerChooser(servers).showAndWait();
                if (res.isPresent()) {
                    var b = TofServerListConfig.setHosts(res.get());
                    if (!b) {
                        new SimpleAlert(Alert.AlertType.ERROR, I18n.get().failedWritingHostsFile()).showAndWait();
                    }
                    try {
                        AssistantConfig.updateAssistant(ass -> {
                            if (ass.lastValues == null) ass.lastValues = new AssistantLastValues();
                            ass.lastValues.requireWritingHostsFileServerNames = res.get().stream().map(e -> e.name).toList();
                        });
                    } catch (IOException e) {
                        Logger.error("failed saving hosts enabled servers to lastValues", e);
                    }
                }
            }
        }

        try {
            Desktop.getDesktop().open(Path.of(GlobalValues.globalServerGamePath.get(), "Launcher", "tof_launcher.exe").toFile());
        } catch (Throwable t) {
            Logger.error("failed launching global server game", t);
            new SimpleAlert(Alert.AlertType.ERROR, I18n.get().launchGameFailed()).showAndWait();
        }
    }

    private void updateBg() {
        var bg = Feed.get().introBg;
        if (bg == null) {
            return;
        }
        setBackground(new Background(new BackgroundImage(
            bg,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            BackgroundSize.DEFAULT
        )));
    }

    private void initLocations() {
        Assistant a;
        try {
            a = AssistantConfig.readAssistant();
        } catch (IOException e) {
            Logger.error("reading assistant config failed in initLocation()", e);
            return;
        }
        if (a.lastValues != null) {
            var lastValues = a.lastValues;
            if (lastValues.savedPath != null && !lastValues.savedPath.isBlank()) {
                selectSavedLocationInput.setText(lastValues.savedPath);
            }
            if (lastValues.gamePath != null && !lastValues.gamePath.isBlank()) {
                selectGameLocationInput.setText(lastValues.gamePath);
            }
            if (lastValues.globalServerGamePath != null && !lastValues.globalServerGamePath.isBlank()) {
                selectGlobalServerGameLocationInput.setText(lastValues.globalServerGamePath);
            }
        }

        if (selectSavedLocationInput.getText() == null || selectSavedLocationInput.getText().isBlank()) {
            autoSearchSavedPath(false);
        }
        if (selectGameLocationInput.getText() == null || selectGameLocationInput.getText().isBlank()) {
            autoSearchGamePath(false);
        }
        if (selectGlobalServerGameLocationInput.getText() == null || selectGlobalServerGameLocationInput.getText().isBlank()) {
            autoSearchGlobalServerGamePath(false);
        }
    }

    private void autoSearchGamePath(boolean alert) {
        final String wmdir = "WanmeiGameAssistant\\games\\HTMobile";
        for (char c = 'C'; c <= 'Z'; ++c) {
            File f = new File(c + ":\\" + wmdir + "\\gameLauncher.exe");
            if (f.isFile()) {
                selectGameLocationInput.setText(f.getParentFile().getAbsolutePath());
                return;
            }
        }
        if (alert) {
            new SimpleAlert(Alert.AlertType.WARNING, I18n.get().autoSearchFailed()).showAndWait();
        }
    }

    private void autoSearchSavedPath(boolean alert) {
        var home = System.getProperty("user.home");
        var res = Path.of(home, "AppData", "Local", "Hotta", "Saved").toString();
        if (new File(res).isDirectory()) {
            selectSavedLocationInput.setText(res);
            return;
        }
        if (alert) {
            new SimpleAlert(Alert.AlertType.WARNING, I18n.get().autoSearchFailed()).showAndWait();
        }
    }

    private void autoSearchGlobalServerGamePath(boolean alert) {
        final String launcherDir = "Tower Of Fantasy\\Launcher";
        for (char c = 'C'; c <= 'Z'; ++c) {
            File f = new File(c + ":\\" + launcherDir + "\\tof_launcher.exe");
            if (f.isFile()) {
                selectGlobalServerGameLocationInput.setText(f.getParentFile().getParentFile().getAbsolutePath());
                return;
            }
        }
        if (alert) {
            new SimpleAlert(Alert.AlertType.WARNING, I18n.get().autoSearchFailed()).showAndWait();
        }
    }
}
