package net.cassite.hottapcassistant;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import net.cassite.hottapcassistant.component.LoadingStage;
import net.cassite.hottapcassistant.feed.FeedThread;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.ui.MainScreen;
import net.cassite.hottapcassistant.util.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main extends Application {
    private MainScreen mainScreen = null;

    @Override
    public void start(Stage stage) {
        var font = Font.loadFont(getClass().getResourceAsStream("/font/SmileySans-Oblique.otf"), 1);
        if (font == null) {
            new SimpleAlert(Alert.AlertType.WARNING, I18n.get().loadingFontFailed("smiley")).show();
        }
        font = Font.loadFont(getClass().getResourceAsStream("/font/NotoSansSC-Regular.otf"), 1);
        if (font == null) {
            new SimpleAlert(Alert.AlertType.WARNING, I18n.get().loadingFontFailed("noto")).show();
        }

        LoadingStage.load(() -> {
            stage.getIcons().add(ImageManager.get().load("images/icon/icon.jpg"));
            stage.setOnCloseRequest(e -> terminate());

            mainScreen = new MainScreen();
            var root = mainScreen;
            Scene scene = new Scene(root);
            stage.setScene(scene);

            MainScreen.initStage(stage);
            stage.show();
        });
    }

    private void terminate() {
        GlobalScreenUtils.unregister();
        TaskManager.terminate();
        FeedThread.get().terminate();
        var mainScreen = this.mainScreen;
        if (mainScreen != null) mainScreen.terminate();
    }

    public static void main(String[] args) {
        try {
            File f = File.createTempFile("JNativeHook", ".dll");
            f.deleteOnExit();
            String dllname = f.getName();
            dllname = dllname.substring(0, dllname.length() - ".dll".length());

            var libpaths = System.getProperty("java.library.path", "");
            if (libpaths.contains(f.getParentFile().getAbsolutePath())) {
                System.setProperty("jnativehook.lib.name", dllname);
            }

            Logger.info("dllname: " + dllname);
            Logger.info("java.library.path: " + libpaths);

            var dllStream = Main.class.getResourceAsStream("/dll/JNativeHook_x64.dll");
            if (dllStream == null) {
                Logger.error("JNativeHook_x64.dll not found, program might not work");
            } else {
                try (dllStream) {
                    try (var fos = new FileOutputStream(f)) {
                        var buf = new byte[1048576];
                        while (true) {
                            int n = dllStream.read(buf);
                            if (n == -1) {
                                break;
                            }
                            fos.write(buf, 0, n);
                        }
                    }
                } catch (IOException e) {
                    Logger.error("extracting JNativeHook_x64.dll failed", e);
                }
            }
        } catch (IOException e) {
            Logger.error("creating tmp file for jnative hook libs failed", e);
        }

        FeedThread.get().start();

        launch();
    }
}
