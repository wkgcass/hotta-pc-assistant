package net.cassite.hottapcassistant.multi;

import io.vproxy.base.util.LogType;
import io.vproxy.base.util.Logger;
import io.vproxy.base.util.callback.Callback;
import io.vproxy.commons.util.IOUtils;
import io.vproxy.vfx.animation.AnimationGraphBuilder;
import io.vproxy.vfx.animation.AnimationNode;
import io.vproxy.vfx.control.dialog.VDialog;
import io.vproxy.vfx.control.dialog.VDialogButton;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.manager.task.TaskManager;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.alert.SimpleAlert;
import io.vproxy.vfx.ui.alert.StackTraceAlert;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.button.ImageButton;
import io.vproxy.vfx.ui.layout.HPadding;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.loading.LoadingItem;
import io.vproxy.vfx.ui.loading.LoadingStage;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import io.vproxy.vfx.util.MiscUtils;
import io.vproxy.vfx.util.algebradata.DoubleData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
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

import static net.cassite.hottapcassistant.multi.MultiHottaInstanceFlow.DEFAULT_RES_SUB_VERSION;

public class MultiHottaInstanceScene extends ToolScene {
    private final MultiHottaInstance tool;
    private HottaLauncherProxyServer proxyServer = null;
    private DNSHijacker dnsHijacker = null;

    private final TextField selectBetaLocationInput;
    private final TextField selectOnlineLocationInput;
    private final TextField selectOnlineModLocationInput;
    private final TextField advBranchInput;
    private final TextField onlineBranchInput;
    private final TextField onlineModBranchInput;
    private final TextField onlineVersionInput;
    private final CheckBox isHandlingAdvCheckBox;
    private int disableTipsVersion;
    private final FusionButton launchOnlineModButton;
    private final FusionButton launchBetaButton;
    private final ImageButton launchBtn;

    public MultiHottaInstanceScene(MultiHottaInstance tool) {
        this.tool = tool;
        enableAutoContentWidthHeight();

        var pane = new Pane();
        FXUtils.observeWidthHeightCenter(getContentPane(), pane);
        getContentPane().getChildren().add(pane);

        selectBetaLocationInput = new TextField() {{
            FontManager.get().setFont(this);
        }};
        selectBetaLocationInput.setPrefWidth(450);
        var selectBetaLocationButton = new FusionButton(I18n.get().selectButton()) {{
            setPrefWidth(48);
            setPrefHeight(35);
        }};
        selectBetaLocationButton.setOnAction(e -> selectLocation(selectBetaLocationInput));
        launchBetaButton = new FusionButton(I18n.get().multiInstanceSingleLaunchButton()) {{
            setPrefWidth(48);
            setPrefHeight(35);
        }};
        launchBetaButton.setDisable(true);
        launchBetaButton.setOnAction(_ -> launchGame(selectBetaLocationInput.getText()));

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

        selectOnlineModLocationInput = new TextField() {{
            FontManager.get().setFont(this);
        }};
        selectOnlineModLocationInput.setPrefWidth(450);
        var selectOnlineModLocationButton = new FusionButton(I18n.get().selectButton()) {{
            setPrefWidth(48);
            setPrefHeight(35);
        }};
        selectOnlineModLocationButton.setOnAction(_ -> selectLocation(selectOnlineModLocationInput));
        launchOnlineModButton = new FusionButton(I18n.get().multiInstanceSingleLaunchButton()) {{
            setPrefWidth(48);
            setPrefHeight(35);
        }};
        launchOnlineModButton.setDisable(true);
        launchOnlineModButton.setOnAction(_ -> launchGame(selectOnlineModLocationInput.getText()));

        advBranchInput = new TextField() {{
            FontManager.get().setFont(this);
            setText("AdvLaunch24");
        }};
        onlineBranchInput = new TextField() {{
            FontManager.get().setFont(this);
            setText("Windows35");
        }};
        onlineModBranchInput = new TextField() {{
            FontManager.get().setFont(this);
            setText("Windows30");
        }};

        onlineVersionInput = new TextField() {{
            FontManager.get().setFont(this);
            setText("");
        }};

        isHandlingAdvCheckBox = new CheckBox(I18n.get().multiInstanceIsHandlingAdvCheckBox()) {{
            FontManager.get().setFont(this);
            setTextFill(Theme.current().normalTextColor());
            FXUtils.disableFocusColor(this);
            setSelected(true);
        }};

        launchBtn = new ImageButton("images/launch-btn/launch", "png") {{
            setScale(0.2);
        }};
        launchBtn.setOnAction(e -> launch());

        pane.getChildren().add(new HBox(
            new HPadding(30),
            new VBox(
                new VPadding(20),
                new ThemeLabel(I18n.get().selectGameLocationDescriptionWithoutAutoSearching()),
                new VPadding(10),
                new HBox(
                    new ThemeLabel(I18n.get().selectOnlineGameLocation()) {{
                        setPrefWidth(150);
                    }},
                    new HPadding(5),
                    selectOnlineLocationInput,
                    new HPadding(5),
                    selectOnlineLocationButton
                ) {{
                    setAlignment(Pos.CENTER);
                }},
                new VPadding(10),
                new HBox(
                    new ThemeLabel(I18n.get().selectBetaGameLocation()) {{
                        setPrefWidth(150);
                    }},
                    new HPadding(5),
                    selectBetaLocationInput,
                    new HPadding(5),
                    launchBetaButton,
                    new HPadding(5),
                    selectBetaLocationButton
                ) {{
                    setAlignment(Pos.CENTER);
                }},
                new VPadding(10),
                new HBox(
                    new ThemeLabel(I18n.get().selectOnlineModGameLocation()) {{
                        setPrefWidth(150);
                    }},
                    new HPadding(5),
                    selectOnlineModLocationInput,
                    new HPadding(5),
                    launchOnlineModButton,
                    new HPadding(5),
                    selectOnlineModLocationButton
                ) {{
                    setAlignment(Pos.CENTER);
                }},
                new VPadding(20),
                new HBox(
                    new ThemeLabel(I18n.get().multiInstanceAdvBranch()) {{
                        setPadding(new Insets(5, 0, 0, 0));
                        setPrefWidth(100);
                    }},
                    new HPadding(5),
                    advBranchInput,
                    new HPadding(20),
                    new ThemeLabel(I18n.get().multiInstanceOnlineBranch()) {{
                        setPadding(new Insets(5, 0, 0, 0));
                        setPrefWidth(100);
                    }},
                    new HPadding(5),
                    onlineBranchInput
                ),
                new VPadding(20),
                new HBox(
                    new ThemeLabel(I18n.get().multiInstanceOnlineVersion()) {{
                        setPadding(new Insets(5, 0, 0, 0));
                        setPrefWidth(100);
                    }},
                    new HPadding(5),
                    onlineVersionInput,
                    new HPadding(20),
                    new ThemeLabel(I18n.get().multiInstanceOnlineModBranch()) {{
                        setPadding(new Insets(5, 0, 0, 0));
                        setPrefWidth(100);
                    }},
                    new HPadding(5),
                    onlineModBranchInput
                ),
                new VPadding(20),
                new HBox(
                    isHandlingAdvCheckBox
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
        config.clearInvalidFields();
        if (config.betaPath != null) {
            selectBetaLocationInput.setText(config.betaPath);
        }
        if (config.onlinePath != null) {
            selectOnlineLocationInput.setText(config.onlinePath);
        }
        if (config.onlineModPath != null) {
            selectOnlineModLocationInput.setText(config.onlineModPath);
        }
        if (config.advBranch != null) {
            advBranchInput.setText(config.advBranch);
        }
        if (config.onlineBranch != null) {
            if (config.onlineBranchVersion >= MultiHottaInstanceConfig.CURRENT_ONLINE_BRANCH_VERSION) {
                onlineBranchInput.setText(config.onlineBranch);
            }
        }
        if (config.onlineModBranch != null) {
            onlineModBranchInput.setText(config.onlineModBranch);
        }
        if (config.onlineVersion != null) {
            onlineVersionInput.setText(config.onlineVersion);
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
        config.onlineModPath = selectOnlineModLocationInput.getText();
        config.advBranch = advBranchInput.getText();
        config.onlineBranch = onlineBranchInput.getText();
        config.onlineModBranch = onlineModBranchInput.getText();
        config.onlineVersion = onlineVersionInput.getText();
        config.disableTips = disableTipsVersion;
        config.onlineBranchVersion = MultiHottaInstanceConfig.CURRENT_ONLINE_BRANCH_VERSION;
        config.clearInvalidFields();
        return config;
    }

    public void launch(boolean launchMod) {
        _launch(launchMod);
    }

    private void launch() {
        _launch(false);
    }

    private synchronized void _launch(boolean launchMod) {
        var config = buildConfig();
        var configErr = config.configValidation();
        if (configErr != null) {
            SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().multiInstanceInvalidFieldAlert(configErr));
            return;
        }
        if (config.onlineModPath == null && launchMod) {
            SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().multiInstanceNoModPath());
            return;
        }
        var items = new ArrayList<LoadingItem>();
        items.add(new LoadingItem(1, I18n.get().multiInstanceLaunchStep("lock"),
            MultiHottaInstanceFlow::checkLock));
        items.add(new LoadingItem(1, I18n.get().multiInstanceLaunchStep("clientVersion"), () -> {
            try {
                MultiHottaInstanceFlow.setClientVersion(config.onlinePath,
                    config.onlineVersion == null ? DEFAULT_RES_SUB_VERSION : config.onlineVersion);
            } catch (IOException e) {
                Logger.error(LogType.FILE_ERROR, "failed retrieving client version", e);
                FXUtils.runOnFX(() ->
                    SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().multiInstanceFailedSettingClientVersion()));
                return false;
            }
            return true;
        }));
        if (config.betaPath != null) {
            items.add(new LoadingItem(1, I18n.get().multiInstanceLaunchStep("UserData"), () ->
                doReplaceUserData(config.betaPath, config)
            ));
        }
        if (config.onlineModPath != null) {
            items.add(new LoadingItem(1, I18n.get().multiInstanceLaunchStep("UserData2"), () ->
                doReplaceUserData(config.onlineModPath, config)
            ));
        }
        if (config.betaPath != null) {
            items.add(new LoadingItem(1, I18n.get().multiInstanceLaunchStep("ResList.xml"), () ->
                doWriteResListXml(config.betaPath, config)
            ));
        }
        if (config.onlineModPath != null) {
            items.add(new LoadingItem(1, I18n.get().multiInstanceLaunchStep("ResList.xml-2"), () ->
                doWriteResListXml(config.onlineModPath, config)
            ));
        }
        if (config.betaPath != null) {
            items.add(new LoadingItem(1, I18n.get().multiInstanceLaunchStep("config.xml"), () ->
                doWriteConfigXml(config.betaPath, config)
            ));
        }
        if (config.onlineModPath != null) {
            items.add(new LoadingItem(1, I18n.get().multiInstanceLaunchStep("config.xml-2"), () ->
                doWriteConfigXml(config.onlineModPath, config)
            ));
        }
        if (config.betaPath != null) {
            items.add(new LoadingItem(1, I18n.get().multiInstanceLaunchStep("Client"), () ->
                doMkLink(config, config.betaPath)
            ));
        }
        if (config.onlineModPath != null) {
            items.add(new LoadingItem(1, I18n.get().multiInstanceLaunchStep("Client2"), () ->
                doMkLink(config, config.onlineModPath)
            ));
        }
        items.add(new LoadingItem(1, I18n.get().multiInstanceLaunchStep("resolve"), () -> {
            if (!MultiHottaInstanceFlow.resolveHosts()) {
                Logger.error(LogType.FILE_ERROR, "resolving hosts failed");
                FXUtils.runOnFX(() ->
                    SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().multiInstanceResolvingFailed()));
                return false;
            }
            return true;
        }));
        items.add(new LoadingItem(1, I18n.get().multiInstanceLaunchStep("server"), () -> {
            if (proxyServer != null) {
                return true;
            }
            var proxyServer = new HottaLauncherProxyServer(
                MultiHottaInstanceFlow.resolvedHosts, config,
                isHandlingAdvCheckBox.selectedProperty());
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
        items.add(new LoadingItem(1, I18n.get().multiInstanceLaunchStep("hijack"), () -> {
            if (dnsHijacker != null) {
                return true;
            }
            var hijacker = new DNSHijacker(MultiHottaInstanceFlow.resolvedHosts);
            try {
                hijacker.start();
            } catch (Exception e) {
                hijacker.destroy();
                Logger.error(LogType.SYS_ERROR, "launching dns hijacker failed", e);
                FXUtils.runOnFX(() ->
                    SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().multiInstanceLaunchDNSHijackerFailed()));
                return false;
            }
            this.dnsHijacker = hijacker;
            return true;
        }));
        items.add(new LoadingItem(1, I18n.get().multiInstanceLaunchStep("flush-dns"),
            MultiHottaInstanceFlow::flushDNS));

        if (launchMod) {
            items.add(new LoadingItem(1, I18n.get().multiInstanceLaunchStep("launch-mod"), () ->
                launchGame(config.onlineModPath)));
        }

        var loadingStage = new LoadingStage(I18n.get().toolName("multi-hotta-instance"));
        loadingStage.setItems(items);
        loadingStage.setInterval(120);
        loadingStage.load(Callback.ofFunction((err, _) -> {
            if (err != null && err.failedItem != null) {
                Logger.error(LogType.ALERT, "initiating multi-hotta-instance failed at " + err.failedItem.name, err);
                String errMsg = I18n.get().loadingFailedErrorMessage(err.failedItem);
                if (err.getCause() != null) {
                    StackTraceAlert.showAndWait(errMsg, err.getCause());
                } else {
                    SimpleAlert.showAndWait(Alert.AlertType.ERROR, errMsg);
                }
                return;
            }
            if (err != null) {
                return;
            }

            launchBetaButton.setDisable(false);
            launchOnlineModButton.setDisable(false);
            launchBtn.setMouseTransparent(true);
            {
                var begin = new AnimationNode<>("begin", new DoubleData(1));
                var end = new AnimationNode<>("end", new DoubleData(0));
                var g = AnimationGraphBuilder.simpleTwoNodeGraph(begin, end, 500)
                    .setApply((_, _, data) -> launchBtn.setOpacity(data.value))
                    .build(begin);
                g.play(end);
            }
            tool.save(config);

            final int CURRENT_TIPS_VERSION = 4;

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

    private boolean doWriteConfigXml(String path, MultiHottaInstanceConfig config) throws Exception {
        try {
            MultiHottaInstanceFlow.writeConfigXml(path, config.resVersion(), config.resSubVersion());
        } catch (IOException e) {
            Logger.error(LogType.FILE_ERROR, "failed writing config.xml", e);
            FXUtils.runOnFX(() ->
                SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().multiInstanceFailedWritingConfigXml()));
            return false;
        }
        return true;
    }

    private boolean doWriteResListXml(String path, MultiHottaInstanceConfig config) throws Exception {
        try {
            MultiHottaInstanceFlow.writeResListXml(path, config.resSubVersion());
        } catch (IOException e) {
            Logger.error(LogType.FILE_ERROR, "failed writing ResList.xml", e);
            FXUtils.runOnFX(() ->
                SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().multiInstanceFailedWritingResListXml()));
            return false;
        }
        return true;
    }

    private void launchGame(String path) {
        if (path == null || path.isBlank()) {
            return; // do nothing
        }
        try {
            Desktop.getDesktop().open(
                Path.of(path, "gameLauncher.exe").toFile());
        } catch (IOException e) {
            Logger.error(LogType.SYS_ERROR, "failed launching game", e);
            FXUtils.runOnFX(() ->
                SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().launchGameFailed()));
        }
    }

    private boolean doMkLink(MultiHottaInstanceConfig config, String dst) {
        var dstClient = Path.of(dst, "Client");
        var clientFile = dstClient.toFile();
        if (clientFile.exists()) {
            return true;
        }
        var onlineClientPath = Path.of(config.onlinePath, "Client");
        try {
            MultiHottaInstanceFlow.makeLink(dstClient.toAbsolutePath().toString(), onlineClientPath.toAbsolutePath().toString());
        } catch (Exception e) {
            Logger.error(LogType.SYS_ERROR, "making link file failed", e);
            FXUtils.runOnFX(() ->
                SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().multiInstanceCannotMakeLink()));
            return false;
        }
        return true;
    }

    private boolean doReplaceUserData(String dst, MultiHottaInstanceConfig config) {
        try {
            MultiHottaInstanceFlow.replaceUserDataDir(dst, config.onlinePath);
        } catch (IOException e) {
            Logger.error(LogType.FILE_ERROR, "failed replacing UserData", e);
            FXUtils.runOnFX(() ->
                SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().multiInstanceFailedReplacingUserDataDir()));
            return false;
        }
        return true;
    }

    public void terminate() {
        var proxyServer = this.proxyServer;
        this.proxyServer = null;
        if (proxyServer != null) {
            proxyServer.destroy();
        }
        var dnsHijacker = this.dnsHijacker;
        this.dnsHijacker = null;
        if (dnsHijacker != null) {
            dnsHijacker.destroy();
        }
        var ok = MultiHottaInstanceFlow.unsetHostsFile();
        if (!ok) {
            SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().clearHostsFailed());
        }
    }
}
