package net.cassite.hottapcassistant.multi;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.cassite.hottapcassistant.component.HPadding;
import net.cassite.hottapcassistant.component.ImageButton;
import net.cassite.hottapcassistant.component.VPadding;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.tool.MultiHottaInstance;
import net.cassite.hottapcassistant.util.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class MultiHottaInstanceStage extends Stage {
    private final MultiHottaInstance tool;
    private HottaLauncherProxyServer proxyServer = null;

    private final TextField selectBetaLocationInput;
    private final TextField selectOnlineLocationInput;
    private final TextField advBranchInput;
    private final TextField resVersionInput;
    private final TextField resSubVersionInput;
    private final TextField clientVersionInput;

    public MultiHottaInstanceStage(MultiHottaInstance tool) {
        this.tool = tool;

        var pane = new Pane();
        var scene = new Scene(pane);
        setScene(scene);

        selectBetaLocationInput = new TextField() {{
            FontManager.setFont(this);
        }};
        selectBetaLocationInput.setEditable(false);
        selectBetaLocationInput.setPrefWidth(500);
        var selectBetaLocationButton = new Button(I18n.get().selectButton()) {{
            FontManager.setFont(this);
        }};
        selectBetaLocationButton.setOnAction(e -> selectLocation(selectBetaLocationInput));

        selectOnlineLocationInput = new TextField() {{
            FontManager.setFont(this);
            if (GlobalValues.gamePath.get() != null) {
                setText(GlobalValues.gamePath.get());
            }
        }};
        selectOnlineLocationInput.setEditable(false);
        selectOnlineLocationInput.setPrefWidth(500);
        var selectOnlineLocationButton = new Button(I18n.get().selectButton()) {{
            FontManager.setFont(this);
        }};
        selectOnlineLocationButton.setOnAction(e -> selectLocation(selectOnlineLocationInput));

        advBranchInput = new TextField() {{
            FontManager.setFont(this);
            setText("AdvLaunch24");
        }};
        resVersionInput = new TextField() {{
            FontManager.setFont(this);
            setText("2.4");
        }};
        resSubVersionInput = new TextField() {{
            FontManager.setFont(this);
            setText("2.4.3");
        }};
        clientVersionInput = new TextField() {{
            FontManager.setFont(this);
        }};
        var launchBtn = new ImageButton("images/launchgame-btn/launchgame", "png") {{
            setScale(0.7);
        }};
        launchBtn.setOnAction(e -> launch());

        pane.getChildren().add(new HBox(
            new HPadding(30),
            new VBox(
                new VPadding(20),
                new Label(I18n.get().selectGameLocationDescriptionWithoutAutoSearching()) {{
                    FontManager.setFont(this);
                }},
                new VPadding(10),
                new HBox(
                    new Label(I18n.get().selectBetaGameLocation()) {{
                        FontManager.setFont(this);
                    }},
                    new HPadding(5),
                    selectBetaLocationInput,
                    new HPadding(5),
                    selectBetaLocationButton
                ) {{
                    setAlignment(Pos.CENTER);
                }},
                new VPadding(10),
                new HBox(
                    new Label(I18n.get().selectOnlineGameLocation()) {{
                        FontManager.setFont(this);
                    }},
                    new HPadding(5),
                    selectOnlineLocationInput,
                    new HPadding(5),
                    selectOnlineLocationButton
                ) {{
                    setAlignment(Pos.CENTER);
                }},
                new Separator() {{
                    setPadding(new Insets(10, 0, 10, 0));
                }},
                new HBox(
                    new Label(I18n.get().multiInstanceAdvBranch()) {{
                        FontManager.setFont(this);
                        setPrefWidth(80);
                        setPadding(new Insets(5, 0, 0, 0));
                    }},
                    new HPadding(5),
                    advBranchInput
                ),
                new VPadding(10),
                new HBox(
                    new Label(I18n.get().multiInstanceResourceVersion()) {{
                        FontManager.setFont(this);
                        setPrefWidth(80);
                        setPadding(new Insets(5, 0, 0, 0));
                    }},
                    new HPadding(5),
                    resVersionInput
                ),
                new VPadding(10),
                new HBox(
                    new Label(I18n.get().multiInstanceResourceSubVersion()) {{
                        FontManager.setFont(this);
                        setPrefWidth(80);
                        setPadding(new Insets(5, 0, 0, 0));
                    }},
                    new HPadding(5),
                    resSubVersionInput
                ),
                new VPadding(10),
                new HBox(
                    new Label(I18n.get().multiInstanceClientVersion()) {{
                        FontManager.setFont(this);
                        setPrefWidth(80);
                        setPadding(new Insets(5, 0, 0, 0));
                    }},
                    new HPadding(5),
                    clientVersionInput
                ),
                new Separator() {{
                    setPadding(new Insets(20, 0, 20, 0));
                }},
                new HBox(launchBtn) {{
                    setAlignment(Pos.CENTER);
                }},
                new VPadding(5),
                new Hyperlink(I18n.get().multiInstanceSaveCaCert()) {{
                    FontManager.setFont(this, 12);
                    setOnAction(e -> {
                        var chooser = new FileChooser();
                        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("crt", "*.crt"));
                        chooser.setInitialFileName("hotta-ca.crt");
                        File f = chooser.showSaveDialog(this.getScene().getWindow());
                        if (f == null) {
                            return;
                        }
                        try {
                            Utils.writeFile(f.toPath(), Certs.CA_CERT);
                        } catch (IOException ex) {
                            Logger.error("failed saving ca cert file", ex);
                            new SimpleAlert(Alert.AlertType.ERROR, I18n.get().failedSavingCaCertFile()).showAndWait();
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
        if (config.resVersion != null) {
            resVersionInput.setText(config.resVersion);
        }
        if (config.resSubVersion != null) {
            resSubVersionInput.setText(config.resSubVersion);
        }
        if (config.clientVersion != null) {
            clientVersionInput.setText(config.clientVersion);
        }
    }

    private void selectLocation(TextField input) {
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
        input.setText(f.getParentFile().getAbsolutePath());
    }

    private MultiHottaInstanceConfig buildConfig() {
        var config = new MultiHottaInstanceConfig();
        config.betaPath = selectBetaLocationInput.getText();
        config.onlinePath = selectOnlineLocationInput.getText();
        config.advBranch = advBranchInput.getText();
        config.resVersion = resVersionInput.getText();
        config.resSubVersion = resSubVersionInput.getText();
        config.clientVersion = clientVersionInput.getText();
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
            new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().multiInstanceEmptyFieldAlert()).showAndWait();
            return;
        }
        var clientPath = Path.of(config.betaPath, "Client");
        var clientFile = clientPath.toFile();
        if (!clientFile.exists()) {
            var onlineClientPath = Path.of(config.onlinePath, "Client");
            try {
                MultiHottaInstanceFlow.makeLink(clientPath.toAbsolutePath().toString(), onlineClientPath.toAbsolutePath().toString());
            } catch (IOException e) {
                Logger.error("making link file failed", e);
                new SimpleAlert(Alert.AlertType.ERROR, I18n.get().multiInstanceCannotMakeLink()).showAndWait();
                return;
            }
        }
        if (!MultiHottaInstanceFlow.setHostsFile()) {
            Logger.error("setting hosts file failed");
            new SimpleAlert(Alert.AlertType.ERROR, I18n.get().multiInstanceCannotSetHostsFile()).showAndWait();
            return;
        }
        if (proxyServer == null) {
            var proxyServer = new HottaLauncherProxyServer(config.advBranch, config.resVersion, config.resSubVersion, config.clientVersion);
            try {
                proxyServer.start();
            } catch (Exception e) {
                proxyServer.destroy();
                Logger.error("launching proxy server failed", e);
                new SimpleAlert(Alert.AlertType.ERROR, I18n.get().multiInstanceLaunchProxyServerFailed()).showAndWait();
                return;
            }
            this.proxyServer = proxyServer;
        }
        try {
            Desktop.getDesktop().open(Path.of(config.betaPath, "gameLauncher.exe").toFile());
        } catch (IOException e) {
            Logger.error("failed launching game", e);
            new SimpleAlert(Alert.AlertType.ERROR, I18n.get().launchGameFailed()).showAndWait();
        }

        tool.save(config);
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
