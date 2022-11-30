package net.cassite.hottapcassistant.ui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import net.cassite.hottapcassistant.component.HPadding;
import net.cassite.hottapcassistant.component.ImageButton;
import net.cassite.hottapcassistant.component.VPadding;
import net.cassite.hottapcassistant.feed.Feed;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.FontManager;
import net.cassite.hottapcassistant.util.GlobalValues;
import net.cassite.hottapcassistant.util.SimpleAlert;
import net.cassite.hottapcassistant.util.Utils;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;

public class WelcomePane extends StackPane {
    private final TextField selectGameLocationInput;
    private final TextField selectSavedLocationInput;

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
                GlobalValues.GamePath = now;
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
            var selectLocationHBox = new HBox(selectLocationLabel,
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
                GlobalValues.SavedPath = now;
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
            var sep1 = new Separator();
            sep1.setPadding(new Insets(50, 0, 50, 0));
            vbox.getChildren().add(sep1);
        }

        {
            EventHandler<ActionEvent> handler = e -> {
                var url = Feed.get().pmpDownloadUrl;
                if (url == null) {
                    url = "https://pmpcdn1.wmupd.com/pmp/client/PMP_1.0.7.0125.exe";
                }
                try {
                    Desktop.getDesktop().browse(new URL(url).toURI());
                } catch (Throwable t) {
                    Clipboard.getSystemClipboard().setContent(Map.of(DataFormat.PLAIN_TEXT, url));
                    new SimpleAlert(Alert.AlertType.ERROR, I18n.get().openBrowserForDownloadingFailed(url)).showAndWait();
                }
            };
            if (I18n.get().supportImageDownloadGameButton()) {
                var downloadBtn = new ImageButton("images/downloadgame-btn/downloadgame", "png");
                downloadBtn.setScale(0.6);
                downloadBtn.setOnAction(handler);
                vbox.getChildren().add(downloadBtn);
            } else {
                var downloadBtn = new Button(I18n.get().downloadHottaPC());
                downloadBtn.setOnAction(handler);
                FontManager.setFont(downloadBtn, 36);
                vbox.getChildren().add(downloadBtn);
            }
        }

        {
            var sep1 = new Separator();
            sep1.setPadding(new Insets(50, 0, 50, 0));
            vbox.getChildren().add(sep1);
        }

        {
            EventHandler<ActionEvent> handler = e -> {
                if (!Utils.checkGamePath()) {
                    return;
                }
                try {
                    Desktop.getDesktop().open(Path.of(GlobalValues.GamePath, "gameLauncher.exe").toFile());
                } catch (Throwable t) {
                    new SimpleAlert(Alert.AlertType.ERROR, I18n.get().launchGameFailed()).showAndWait();
                }
            };
            if (I18n.get().supportImageLaunchGameButton()) {
                var downloadBtn = new ImageButton("images/launchgame-btn/launchgame", "png");
                downloadBtn.setScale(0.6);
                downloadBtn.setOnAction(handler);
                vbox.getChildren().add(downloadBtn);
            } else {
                var downloadBtn = new Button(I18n.get().launchGame());
                downloadBtn.setOnAction(handler);
                FontManager.setFont(downloadBtn, 36);
                vbox.getChildren().add(downloadBtn);
            }
        }

        Platform.runLater(() -> {
            autoSearchGamePath(false);
            autoSearchSavedPath(false);
        });
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
            new SimpleAlert(Alert.AlertType.WARNING, I18n.get().autoSearchGameLocationFailed()).showAndWait();
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
            new SimpleAlert(Alert.AlertType.WARNING, I18n.get().autoSearchSavedLocationFailed()).showAndWait();
        }
    }
}
