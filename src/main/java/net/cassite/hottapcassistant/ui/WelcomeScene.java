package net.cassite.hottapcassistant.ui;

import io.vproxy.base.util.LogType;
import io.vproxy.base.util.Logger;
import io.vproxy.vfx.control.dialog.VDialog;
import io.vproxy.vfx.control.dialog.VDialogButton;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.ui.alert.SimpleAlert;
import io.vproxy.vfx.ui.button.ImageButton;
import io.vproxy.vfx.ui.button.TransparentFusionButton;
import io.vproxy.vfx.ui.layout.HPadding;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.loading.LoadingFailure;
import io.vproxy.vfx.ui.pane.AbstractFusionPane;
import io.vproxy.vfx.ui.pane.TransparentContentFusionPane;
import io.vproxy.vfx.util.FXUtils;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import net.cassite.hottapcassistant.component.serverlist.UIServerChooser;
import net.cassite.hottapcassistant.config.AssistantConfig;
import net.cassite.hottapcassistant.config.TofServerListConfig;
import net.cassite.hottapcassistant.entity.*;
import net.cassite.hottapcassistant.feed.Feed;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.tool.PatchInfoBuilder;
import net.cassite.hottapcassistant.util.GlobalValues;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.*;

public class WelcomeScene extends AbstractMainScene {
    private final CheckBox useCNGameCheckBox = new CheckBox() {{
        FXUtils.disableFocusColor(this);
    }};
    private final CheckBox useGlobalGameCheckBox = new CheckBox() {{
        FXUtils.disableFocusColor(this);
    }};
    private final List<CheckBox> checkboxes = new ArrayList<>() {{
        add(useCNGameCheckBox);
        add(useGlobalGameCheckBox);
    }};
    private final TextField selectGameLocationInput;
    private final TextField selectSavedLocationInput;
    private final TextField selectGlobalServerGameLocationInput;

    private boolean isAltDown = false;

    public WelcomeScene() {
        enableAutoContentWidth();

        getNode().setBackground(Background.EMPTY);

        var stackPane = new StackPane();
        FXUtils.observeWidth(getContentPane(), stackPane);
        getContentPane().getChildren().add(stackPane);

        stackPane.setAlignment(Pos.CENTER);

        var vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);
        stackPane.getChildren().add(vbox);

        {
            vbox.setPadding(new Insets(50, 0, 0, 0));
        }

        var textPart = new VBox();
        {
            var pane = new TransparentContentFusionPane() {
                @Override
                protected AbstractFusionPane buildRootNode() {
                    return new TransparentContentFusionPaneImpl() {
                        @Override
                        protected Color normalColor() {
                            return new Color(1, 1, 1, 0);
                        }

                        @Override
                        protected Color hoverColor() {
                            return new Color(1, 1, 1, 0.4);
                        }
                    };
                }
            };
            pane.getNode().setMaxWidth(975);
            pane.getNode().setPrefWidth(975);
            pane.getNode().setMaxHeight(250);
            pane.getNode().setPrefHeight(250);

            vbox.getChildren().add(pane.getNode());
            pane.getContentPane().getChildren().add(textPart);
            FXUtils.observeWidthHeight(pane.getContentPane(), textPart);

            textPart.setAlignment(Pos.CENTER);
        }

        {
            var selectLocationGroup = new VBox();
            var selectLocationDesc = new Label(I18n.get().selectSavedLocationDescription()) {{
                FontManager.get().setFont(this);
            }};
            selectLocationGroup.setAlignment(Pos.CENTER);
            selectLocationGroup.getChildren().add(selectLocationDesc);
            var selectLocationLabel = new Label(I18n.get().selectSavedLocation()) {{
                FontManager.get().setFont(this);
            }};
            selectSavedLocationInput = new TextField() {{
                FontManager.get().setFont(this);
                FXUtils.disableFocusColor(this);
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
                } catch (Exception e) {
                    Logger.error(LogType.FILE_ERROR, "failed updating assistant config", e);
                }
                if (checkCurrentGameVersion()) {
                    swapSavedIfPossible();
                }
            });
            var selectLocationButton = new TransparentFusionButton(I18n.get().selectButton()) {{
                FontManager.get().setFont(getTextNode());
                getTextNode().setTextFill(Color.BLACK);
                setPrefWidth(45);
            }};
            selectLocationButton.setOnAction(e -> {
                if (!isAltDown && !selectSavedLocationInput.getText().isBlank()) {
                    SimpleAlert.showAndWait(Alert.AlertType.WARNING, I18n.get().alertChangeSavedDirectory());
                    return;
                }
                var chooser = new DirectoryChooser();
                File f = chooser.showDialog(getNode().getScene().getWindow());
                if (f == null) {
                    return;
                }
                if (!f.isDirectory()) {
                    SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().chosenWrongSavedDirectory());
                    return;
                }
                selectSavedLocationInput.setText(f.getAbsolutePath());
            });
            var selectLocationAutoButton = new TransparentFusionButton(I18n.get().autoSearchButton()) {{
                FontManager.get().setFont(getTextNode());
                getTextNode().setTextFill(Color.BLACK);
                setPrefWidth(72);
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
            textPart.getChildren().add(selectLocationGroup);
        }

        {
            textPart.getChildren().add(new VPadding(10));
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
                FontManager.get().setFont(this);
            }};
            selectLocationGroup.setAlignment(Pos.CENTER);
            selectLocationGroup.getChildren().add(selectLocationDesc);
            var selectLocationLabel = new Label(I18n.get().selectGameLocation()) {{
                FontManager.get().setFont(this);
            }};
            selectGameLocationInput = new TextField() {{
                FontManager.get().setFont(this);
                FXUtils.disableFocusColor(this);
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
                } catch (Exception e) {
                    Logger.error(LogType.FILE_ERROR, "failed updating assistant config", e);
                }
            });
            var selectLocationButton = new TransparentFusionButton(I18n.get().selectButton()) {{
                FontManager.get().setFont(getTextNode());
                getTextNode().setTextFill(Color.BLACK);
                setPrefWidth(45);
            }};
            selectLocationButton.setOnAction(e -> {
                var chooser = new FileChooser();
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("exe", "*.exe"));
                File f = chooser.showOpenDialog(getNode().getScene().getWindow());
                if (f == null) {
                    return;
                }
                if (!f.getName().equalsIgnoreCase("gameLauncher.exe")) {
                    SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().chosenWrongGameFile());
                    return;
                }
                if (!f.isFile()) {
                    SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().chosenWrongGameFile());
                    return;
                }
                selectGameLocationInput.setText(f.getParentFile().getAbsolutePath());
            });
            var selectLocationAutoButton = new TransparentFusionButton(I18n.get().autoSearchButton()) {{
                FontManager.get().setFont(getTextNode());
                getTextNode().setTextFill(Color.BLACK);
                setPrefWidth(72);
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
            textPart.getChildren().add(selectLocationGroup);
        }

        {
            textPart.getChildren().add(new VPadding(10));
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
                FontManager.get().setFont(this);
            }};
            selectLocationGroup.setAlignment(Pos.CENTER);
            selectLocationGroup.getChildren().add(selectLocationDesc);
            var selectLocationLabel = new Label(I18n.get().selectGlobalServerGameLocation()) {{
                FontManager.get().setFont(this);
            }};
            selectGlobalServerGameLocationInput = new TextField() {{
                FontManager.get().setFont(this);
                FXUtils.disableFocusColor(this);
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
                } catch (Exception e) {
                    Logger.error(LogType.FILE_ERROR, "failed updating assistant config", e);
                }
            });
            var selectLocationButton = new TransparentFusionButton(I18n.get().selectButton()) {{
                FontManager.get().setFont(getTextNode());
                getTextNode().setTextFill(Color.BLACK);
                setPrefWidth(45);
            }};
            selectLocationButton.setOnAction(e -> {
                var chooser = new FileChooser();
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("exe", "*.exe"));
                File f = chooser.showOpenDialog(getNode().getScene().getWindow());
                if (f == null) {
                    return;
                }
                if (!f.getName().equalsIgnoreCase("tof_launcher.exe")) {
                    SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().chosenWrongGlobalServerGameFile());
                    return;
                }
                if (!f.isFile()) {
                    SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().chosenWrongGlobalServerGameFile());
                    return;
                }
                var launcherDir = f.getParentFile();
                var tofDir = launcherDir.getParentFile();
                if (tofDir == null) {
                    SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().chosenWrongGlobalServerGameFileNoParentDir());
                    return;
                }
                selectGlobalServerGameLocationInput.setText(tofDir.getAbsolutePath());
            });
            var selectLocationAutoButton = new TransparentFusionButton(I18n.get().autoSearchButton()) {{
                FontManager.get().setFont(getTextNode());
                getTextNode().setTextFill(Color.BLACK);
                setPrefWidth(72);
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
            textPart.getChildren().add(selectLocationGroup);
        }

        {
            vbox.getChildren().add(new VPadding(50));
        }

        var buttonsPart = new VBox();
        {
            var buttonsPane = new TransparentContentFusionPane() {
                @Override
                protected AbstractFusionPane buildRootNode() {
                    return new TransparentContentFusionPaneImpl() {
                        @Override
                        protected Color normalColor() {
                            return Color.TRANSPARENT;
                        }

                        @Override
                        protected Color hoverColor() {
                            return Color.TRANSPARENT;
                        }

                        @Override
                        protected Color hoverBorderColor() {
                            return Color.TRANSPARENT;
                        }

                        @Override
                        protected Color normalBorderColor() {
                            return Color.TRANSPARENT;
                        }
                    };
                }
            };

            buttonsPane.getNode().setMaxWidth(500);
            buttonsPane.getNode().setPrefWidth(500);
            buttonsPane.getNode().setMaxHeight(280);
            buttonsPane.getNode().setPrefHeight(280);

            vbox.getChildren().add(buttonsPane.getNode());
            buttonsPane.getContentPane().getChildren().add(buttonsPart);
            FXUtils.observeWidthHeight(buttonsPane.getContentPane(), buttonsPart);

            buttonsPart.setAlignment(Pos.CENTER);
        }
        {
            var group = new Group();
            buttonsPart.getChildren().add(group);

            var downloadBtn = new ImageButton("images/downloadgame-btn/downloadgame", "png");
            downloadBtn.setScale(0.6);
            downloadBtn.setOnAction(e -> {
                var url = Feed.get().pmpDownloadUrl.get();
                if (url == null) {
                    url = "https://htapk.wmupd.com/webops/ht/HT_1.1.0.0830.exe";
                }
                try {
                    Desktop.getDesktop().browse(new URL(url).toURI());
                } catch (Throwable t) {
                    Logger.error(LogType.SYS_ERROR, "failed downloading game", t);
                    Clipboard.getSystemClipboard().setContent(Map.of(DataFormat.PLAIN_TEXT, url));
                    SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().openBrowserForDownloadingFailed(url));
                }
            });

            var launchBtn = new ImageButton("images/launchgame-btn/launchgame", "png");
            launchBtn.setScale(0.6);
            launchBtn.setOnAction(e -> {
                if (!GlobalValues.checkCNGamePath()) {
                    return;
                }
                useCNGameCheckBox.setSelected(true);
                var promise = PatchInfoBuilder.applyCNPatch(
                    Path.of(GlobalValues.gamePath.get(), "Client", "WindowsNoEditor", "Hotta", "Content", "PatchPaks"));
                promise.setHandler((v, err) -> {
                    if (err != null) {
                        Logger.error(LogType.FILE_ERROR, "failed applying patch", err);
                        if (err instanceof LoadingFailure lf) {
                            SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().applyPatchFailed() + ": " + lf.failedItem.name);
                        } else {
                            SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().applyPatchFailed());
                        }
                        return;
                    }
                    try {
                        Desktop.getDesktop().open(Path.of(GlobalValues.gamePath.get(), "gameLauncher.exe").toFile());
                    } catch (Throwable t) {
                        Logger.error(LogType.SYS_ERROR, "failed launching game", t);
                        SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().launchGameFailed());
                    }
                });
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
            sep1.setPadding(new Insets(40, 0, 40, 0));
            sep1.setOpacity(0.25);
            buttonsPart.getChildren().add(sep1);
        }

        {
            var group = new Group();
            buttonsPart.getChildren().add(group);

            var downloadBtn = new ImageButton("images/global-download-btn/download", "png");
            downloadBtn.setScale(0.5);
            downloadBtn.setOnAction(e -> {
                var url = Feed.get().tofMiniLoaderUrl.get();
                if (url == null) {
                    url = "https://www.toweroffantasy-global.com/download/TofMiniLoader_official.wg.intl.exe";
                }
                try {
                    Desktop.getDesktop().browse(new URL(url).toURI());
                } catch (Throwable t) {
                    Logger.error(LogType.SYS_ERROR, "failed downloading global server game", t);
                    Clipboard.getSystemClipboard().setContent(Map.of(DataFormat.PLAIN_TEXT, url));
                    SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().openBrowserForDownloadingFailed(url));
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

        getNode().setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ALT) {
                isAltDown = true;
            }
        });
        getNode().setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ALT) {
                isAltDown = false;
            }
        });

        Platform.runLater(this::initLocations);
    }

    @Override
    public String title() {
        return I18n.get().toolNameWelcome();
    }

    private boolean checkCurrentGameVersion() {
        GameAssistant config;
        try {
            config = GlobalValues.getGameAssistantConfig().readGameAssistant();
        } catch (IOException e) {
            Logger.error(LogType.FILE_ERROR, "failed retrieving assistant config when checking version", e);
            return false;
        }
        if (config.version != null) {
            return true;
        }
        var dialog = new VDialog<Integer>();
        dialog.setText(I18n.get().chooseGameVersionDesc());
        dialog.setButtons(Arrays.asList(
            new VDialogButton<>(I18n.get().chooseCNVersion(), 1),
            new VDialogButton<>(I18n.get().chooseGlobalVersion(), 2)
        ));

        var resOpt = dialog.showAndWait();
        if (resOpt.isEmpty()) {
            return false;
        }
        int res = resOpt.get();
        if (res == 1) {
            config.version = GameVersion.CN;
        } else if (res == 2) {
            config.version = GameVersion.Global;
        } else {
            return false;
        }

        try {
            GlobalValues.getGameAssistantConfig().writeGameAssistant(config);
        } catch (Exception e) {
            Logger.error(LogType.FILE_ERROR, "failed writing assistant config when checking version", e);
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
            SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().swapConfigFailed());
            return;
        }
        if (swapped) {
            var config = GlobalValues.getGameAssistantConfig();
            try {
                config.updateGameAssistant(a -> a.version = GlobalValues.useVersion.get());
            } catch (Exception e) {
                Logger.error(LogType.FILE_ERROR, "trying to update swapped assistant file failed", e);
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
                Logger.error(LogType.FILE_ERROR, "failed reading tof server list", e);
                SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().failedReadingTofServerList());
            }
            try {
                var ass = AssistantConfig.readAssistant();
                selectedServers = ass.lastValues.requireWritingHostsFileServerNames;
            } catch (Exception e) {
                Logger.error(LogType.FILE_ERROR, "failed reading enabled servers from last config", e);
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
                        SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().failedWritingHostsFile());
                    }
                    try {
                        AssistantConfig.updateAssistant(ass -> {
                            if (ass.lastValues == null) ass.lastValues = new AssistantLastValues();
                            ass.lastValues.requireWritingHostsFileServerNames = res.get().stream().map(e -> e.name).toList();
                        });
                    } catch (Exception e) {
                        Logger.error(LogType.FILE_ERROR, "failed saving hosts enabled servers to lastValues", e);
                    }
                }
            }
        }

        var promise = PatchInfoBuilder.applyGlobalPatch(
            Path.of(GlobalValues.globalServerGamePath.get(), "Hotta", "Content", "Paks"));
        promise.setHandler((v, err) -> {
            if (err != null) {
                Logger.error(LogType.FILE_ERROR, "failed applying patch", err);
                if (err instanceof LoadingFailure lf) {
                    SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().applyPatchFailed() + ": " + lf.failedItem.name);
                } else {
                    SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().applyPatchFailed());
                }
                return;
            }
            try {
                Desktop.getDesktop().open(Path.of(GlobalValues.globalServerGamePath.get(), "Launcher", "tof_launcher.exe").toFile());
            } catch (Throwable t) {
                Logger.error(LogType.SYS_ERROR, "failed launching global server game", t);
                SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().launchGameFailed());
            }
        });
    }

    private void initLocations() {
        Assistant a;
        try {
            a = AssistantConfig.readAssistant(true);
        } catch (Exception e) {
            Logger.error(LogType.FILE_ERROR, "reading assistant config failed in initLocation()", e);
            return;
        }
        if (a.lastValues != null) {
            var lastValues = a.lastValues;
            if (lastValues.savedPath != null && !lastValues.savedPath.isBlank() && new File(lastValues.savedPath).isDirectory()) {
                selectSavedLocationInput.setText(lastValues.savedPath);
            }
            if (lastValues.gamePath != null && !lastValues.gamePath.isBlank() && new File(lastValues.gamePath).isDirectory()) {
                selectGameLocationInput.setText(lastValues.gamePath);
            }
            if (lastValues.globalServerGamePath != null && !lastValues.globalServerGamePath.isBlank() && new File(lastValues.globalServerGamePath).isDirectory()) {
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
        final String htdir = "HTMobile";
        for (char c = 'C'; c <= 'Z'; ++c) {
            File f = new File(c + ":\\" + htdir + "\\gameLauncher.exe");
            if (f.isFile()) {
                selectGameLocationInput.setText(f.getParentFile().getAbsolutePath());
                return;
            }
        }
        final String hottadir = "Hotta";
        for (char c = 'C'; c <= 'Z'; ++c) {
            File f = new File(c + ":\\" + hottadir + "\\gameLauncher.exe");
            if (f.isFile()) {
                selectGameLocationInput.setText(f.getParentFile().getAbsolutePath());
                return;
            }
        }
        if (alert) {
            SimpleAlert.showAndWait(Alert.AlertType.WARNING, I18n.get().autoSearchFailed());
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
            SimpleAlert.showAndWait(Alert.AlertType.WARNING, I18n.get().autoSearchFailed());
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
            SimpleAlert.showAndWait(Alert.AlertType.WARNING, I18n.get().autoSearchFailed());
        }
    }
}
