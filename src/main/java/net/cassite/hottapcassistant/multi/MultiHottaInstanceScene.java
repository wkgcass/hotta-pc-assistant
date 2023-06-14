package net.cassite.hottapcassistant.multi;

import io.vproxy.base.util.LogType;
import io.vproxy.base.util.Logger;
import io.vproxy.base.util.callback.Callback;
import io.vproxy.commons.util.IOUtils;
import io.vproxy.vfx.control.dialog.VDialog;
import io.vproxy.vfx.control.dialog.VDialogButton;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.manager.task.TaskManager;
import io.vproxy.vfx.ui.alert.SimpleAlert;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.button.ImageButton;
import io.vproxy.vfx.ui.layout.HPadding;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.loading.LoadingItem;
import io.vproxy.vfx.ui.loading.LoadingStage;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import io.vproxy.vfx.util.MiscUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.tool.MultiHottaInstance;
import net.cassite.hottapcassistant.tool.ToolScene;
import net.cassite.hottapcassistant.util.GlobalValues;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static net.cassite.hottapcassistant.multi.MultiHottaInstanceFlow.RES_SUB_VERSION;
import static net.cassite.hottapcassistant.multi.MultiHottaInstanceFlow.RES_VERSION;

public class MultiHottaInstanceScene extends ToolScene {
    private final MultiHottaInstance tool;
    private HottaLauncherProxyServer proxyServer = null;

    private final TextField selectBetaLocationInput;
    private final TextField selectOnlineLocationInput;
    private final TextField advBranchInput;
    private int disableTipsVersion;

    public MultiHottaInstanceScene(MultiHottaInstance tool) {
        this.tool = tool;
        enableAutoContentWidthHeight();

        var pane = new Pane();
        FXUtils.observeWidthHeightCenter(getContentPane(), pane);
        getContentPane().getChildren().add(pane);

        selectBetaLocationInput = new TextField() {{
            FontManager.get().setFont(this);
        }};
        selectBetaLocationInput.setEditable(false);
        selectBetaLocationInput.setPrefWidth(500);
        var selectBetaLocationButton = new FusionButton(I18n.get().selectButton()) {{
            setPrefWidth(48);
            setPrefHeight(35);
        }};
        selectBetaLocationButton.setOnAction(e -> selectLocation(selectBetaLocationInput));

        selectOnlineLocationInput = new TextField() {{
            FontManager.get().setFont(this);
            if (GlobalValues.gamePath.get() != null) {
                setText(GlobalValues.gamePath.get());
            }
        }};
        selectOnlineLocationInput.setEditable(false);
        selectOnlineLocationInput.setPrefWidth(500);
        var selectOnlineLocationButton = new FusionButton(I18n.get().selectButton()) {{
            setPrefWidth(48);
            setPrefHeight(35);
        }};
        selectOnlineLocationButton.setOnAction(e -> selectLocation(selectOnlineLocationInput));

        advBranchInput = new TextField() {{
            FontManager.get().setFont(this);
            setText("AdvLaunch24");
        }};
        var launchBtn = new ImageButton("images/launchgame-btn/launchgame", "png") {{
            setScale(0.7);
        }};
        launchBtn.setOnAction(e -> launch());

        pane.getChildren().add(new HBox(
            new HPadding(30),
            new VBox(
                new VPadding(20),
                new ThemeLabel(I18n.get().selectGameLocationDescriptionWithoutAutoSearching()),
                new VPadding(10),
                new HBox(
                    new ThemeLabel(I18n.get().selectBetaGameLocation()),
                    new HPadding(5),
                    selectBetaLocationInput,
                    new HPadding(5),
                    selectBetaLocationButton
                ) {{
                    setAlignment(Pos.CENTER);
                }},
                new VPadding(10),
                new HBox(
                    new ThemeLabel(I18n.get().selectOnlineGameLocation()),
                    new HPadding(5),
                    selectOnlineLocationInput,
                    new HPadding(5),
                    selectOnlineLocationButton
                ) {{
                    setAlignment(Pos.CENTER);
                }},
                new VPadding(20),
                new HBox(
                    new ThemeLabel(I18n.get().multiInstanceAdvBranch()) {{
                        setPrefWidth(80);
                        setPadding(new Insets(5, 0, 0, 0));
                    }},
                    new HPadding(5),
                    advBranchInput
                ),
                new VPadding(40),
                new HBox(launchBtn) {{
                    setAlignment(Pos.CENTER);
                }},
                new VPadding(3),
                new Hyperlink(I18n.get().multiInstanceTutorialLink()) {{
                    FontManager.get().setFont(this, settings -> settings.setSize(12));
                    setOnAction(e -> {
                        var url = "https://www.acfun.cn/v/ac40665592";
                        try {
                            Desktop.getDesktop().browse(new URL(url).toURI());
                        } catch (Throwable t) {
                            Logger.error(LogType.SYS_ERROR, "failed opening multi-hotta-instances tutorial link", t);
                            Clipboard.getSystemClipboard().setContent(Map.of(DataFormat.PLAIN_TEXT, url));
                            SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().multiInstancesOpenBrowserForTutorialFailed(url));
                        }
                    });
                }},
                new VPadding(2),
                new Hyperlink(I18n.get().multiInstanceSaveCaCert()) {{
                    FontManager.get().setFont(this, settings -> settings.setSize(12));
                    setOnAction(e -> {
                        var chooser = new FileChooser();
                        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("crt", "*.crt"));
                        chooser.setInitialFileName("hotta-ca.crt");
                        File f = chooser.showSaveDialog(this.getScene().getWindow());
                        if (f == null) {
                            return;
                        }
                        try {
                            IOUtils.writeFileWithBackup(f.getAbsolutePath(), Certs.CA_CERT);
                        } catch (Exception ex) {
                            Logger.error(LogType.FILE_ERROR, "failed saving ca cert file", ex);
                            SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().failedSavingCaCertFile());
                        }
                    });
                }},
                new VPadding(10)
            ),
            new HPadding(30)
        ));
    }

    public void init(MultiHottaInstanceConfig config) {
        config.clearEmptyFields();
        if (config.betaPath != null) {
            selectBetaLocationInput.setText(config.betaPath);
        }
        if (config.onlinePath != null) {
            selectOnlineLocationInput.setText(config.onlinePath);
        }
        if (config.advBranch != null) {
            advBranchInput.setText(config.advBranch);
        }
        disableTipsVersion = config.disableTips;
    }

    private void selectLocation(TextField input) {
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
        input.setText(f.getParentFile().getAbsolutePath());
    }

    private MultiHottaInstanceConfig buildConfig() {
        var config = new MultiHottaInstanceConfig();
        config.betaPath = selectBetaLocationInput.getText();
        config.onlinePath = selectOnlineLocationInput.getText();
        config.advBranch = advBranchInput.getText();
        config.disableTips = disableTipsVersion;
        config.clearEmptyFields();
        return config;
    }

    /*
     * will take the following steps:
     * 1. check client path, make link if not exists
     * 2. modify hosts file, add proxy rules
     * 3. launch proxy server
     * 4. launch beta game
     */
    private synchronized void launch() {
        var config = buildConfig();
        if (config.hasEmptyField()) {
            SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().multiInstanceEmptyFieldAlert());
            return;
        }
        var clientVersion = new String[]{null};
        var items = new ArrayList<LoadingItem>();
        items.add(new LoadingItem(1, I18n.get().multiInstanceLaunchStep("clientVersion"), () -> {
            String v;
            try {
                v = MultiHottaInstanceFlow.readClientVersion(config.onlinePath);
            } catch (IOException e) {
                Logger.error(LogType.FILE_ERROR, "failed retrieving client version", e);
                FXUtils.runOnFX(() ->
                    SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().multiInstanceFailedRetrievingClientVersion()));
                return false;
            }
            clientVersion[0] = v;
            return true;
        }));
        items.add(new LoadingItem(1, I18n.get().multiInstanceLaunchStep("UserData"), () -> {
            try {
                MultiHottaInstanceFlow.replaceUserDataDir(config.betaPath, config.onlinePath);
            } catch (IOException e) {
                Logger.error(LogType.FILE_ERROR, "failed replacing UserData", e);
                FXUtils.runOnFX(() ->
                    SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().multiInstanceFailedReplacingUserDataDir()));
                return false;
            }
            return true;
        }));
        items.add(new LoadingItem(1, I18n.get().multiInstanceLaunchStep("ResList.xml"), () -> {
            try {
                MultiHottaInstanceFlow.writeResListXml(config.betaPath, RES_SUB_VERSION);
            } catch (IOException e) {
                Logger.error(LogType.FILE_ERROR, "failed writing ResList.xml", e);
                FXUtils.runOnFX(() ->
                    SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().multiInstanceFailedWritingResListXml()));
                return false;
            }
            return true;
        }));
        items.add(new LoadingItem(1, I18n.get().multiInstanceLaunchStep("config.xml"), () -> {
            try {
                MultiHottaInstanceFlow.writeConfigXml(config.betaPath, RES_VERSION, RES_SUB_VERSION);
            } catch (IOException e) {
                Logger.error(LogType.FILE_ERROR, "failed writing config.xml", e);
                FXUtils.runOnFX(() ->
                    SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().multiInstanceFailedWritingConfigXml()));
                return false;
            }
            return true;
        }));
        items.add(new LoadingItem(1, I18n.get().multiInstanceLaunchStep("Client"), () -> {
            var clientPath = Path.of(config.betaPath, "Client");
            var clientFile = clientPath.toFile();
            if (clientFile.exists()) {
                return true;
            }
            var onlineClientPath = Path.of(config.onlinePath, "Client");
            try {
                MultiHottaInstanceFlow.makeLink(clientPath.toAbsolutePath().toString(), onlineClientPath.toAbsolutePath().toString());
            } catch (IOException e) {
                Logger.error(LogType.SYS_ERROR, "making link file failed", e);
                FXUtils.runOnFX(() ->
                    SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().multiInstanceCannotMakeLink()));
                return false;
            }
            return true;
        }));
        items.add(new LoadingItem(1, I18n.get().multiInstanceLaunchStep("hosts"), () -> {
            if (!MultiHottaInstanceFlow.setHostsFile()) {
                Logger.error(LogType.FILE_ERROR, "setting hosts file failed");
                FXUtils.runOnFX(() ->
                    SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().multiInstanceCannotSetHostsFile()));
                return false;
            }
            return true;
        }));
        items.add(new LoadingItem(1, I18n.get().multiInstanceLaunchStep("server"), () -> {
            if (proxyServer != null) {
                return true;
            }
            var proxyServer = new HottaLauncherProxyServer(config.advBranch, RES_VERSION, RES_SUB_VERSION, clientVersion[0]);
            try {
                proxyServer.start();
            } catch (Exception e) {
                proxyServer.destroy();
                Logger.error(LogType.SYS_ERROR, "launching proxy server failed", e);
                FXUtils.runOnFX(() ->
                    SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().multiInstanceLaunchProxyServerFailed()));
                return false;
            }
            this.proxyServer = proxyServer;
            return true;
        }));
        items.add(new LoadingItem(1, I18n.get().multiInstanceLaunchStep("launch"), () -> {
            try {
                Desktop.getDesktop().open(Path.of(config.betaPath, "gameLauncher.exe").toFile());
            } catch (IOException e) {
                Logger.error(LogType.SYS_ERROR, "failed launching game", e);
                FXUtils.runOnFX(() ->
                    SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().launchGameFailed()));
                return false;
            }
            MiscUtils.threadSleep(1_000);
            return true;
        }));
        var loadingStage = new LoadingStage(I18n.get().toolName("multi-hotta-instance"));
        loadingStage.setItems(items);
        loadingStage.setInterval(120);
        loadingStage.load(Callback.ofIgnoreExceptionFunction(v -> {
            tool.save(config);

            final int CURRENT_TIPS_VERSION = 1;

            if (config.disableTips >= CURRENT_TIPS_VERSION) {
                return;
            }
            var dialog = new VDialog<Integer>();
            dialog.setText(I18n.get().multiInstanceTips());
            var ignoreBtnRef = new VDialogButton<>(I18n.get().multiInstanceConfirmAndDisableTipsButton(), 2);
            dialog.setButtons(Arrays.asList(
                ignoreBtnRef,
                new VDialogButton<>(I18n.get().alertOkButton(), 1)
            ));

            var ignoreBtn = ignoreBtnRef.getButton();
            ignoreBtn.setDisable(true);

            TaskManager.get().execute(() -> {
                for (int i = 3; i >= 0; --i) {
                    final int fi = i;
                    FXUtils.runOnFX(() ->
                        ignoreBtn.setText(I18n.get().multiInstanceConfirmAndDisableTipsButton() + " (" + fi + ")"));
                    MiscUtils.threadSleep(1_000);
                }
                FXUtils.runOnFX(() -> {
                    ignoreBtn.setText(I18n.get().multiInstanceConfirmAndDisableTipsButton());
                    ignoreBtn.setDisable(false);
                });
            });

            var res = dialog.showAndWait();
            if (res.isPresent() && res.get() == 2) {
                config.disableTips = CURRENT_TIPS_VERSION;
                disableTipsVersion = CURRENT_TIPS_VERSION;
                tool.save(config);
            }
        }));
    }

    public void terminate() {
        var proxyServer = this.proxyServer;
        this.proxyServer = null;
        if (proxyServer != null) {
            proxyServer.destroy();
        }
        MultiHottaInstanceFlow.unsetHostsFile();
    }
}
