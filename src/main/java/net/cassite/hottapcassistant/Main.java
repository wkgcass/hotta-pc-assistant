package net.cassite.hottapcassistant;

import io.vproxy.vfx.control.globalscreen.GlobalScreenUtils;
import io.vproxy.vfx.manager.audio.AudioManager;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.manager.font.FontProvider;
import io.vproxy.vfx.manager.font.FontUsage;
import io.vproxy.vfx.manager.image.ImageManager;
import io.vproxy.vfx.manager.internal_i18n.InternalI18n;
import io.vproxy.vfx.manager.task.TaskManager;
import io.vproxy.vfx.ui.alert.SimpleAlert;
import io.vproxy.vfx.ui.loading.LoadingItem;
import io.vproxy.vfx.ui.loading.LoadingStage;
import io.vproxy.vfx.util.Callback;
import io.vproxy.vfx.util.Logger;
import io.vproxy.vfx.util.MiscUtils;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import net.cassite.hottapcassistant.feed.FeedThread;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.ui.MainScreen;
import net.cassite.hottapcassistant.util.Consts;
import net.cassite.hottapcassistant.util.GlobalValues;

import java.util.ArrayList;

public class Main extends Application {
    private MainScreen mainScreen = null;

    @Override
    public void start(Stage stage) {
        var font = Font.loadFont(getClass().getResourceAsStream("/font/SmileySans-Oblique.otf"), 1);
        if (font == null) {
            SimpleAlert.show(Alert.AlertType.WARNING, I18n.get().loadingFontFailed("smiley"));
        }
        font = Font.loadFont(getClass().getResourceAsStream("/font/NotoSansSC-Regular.otf"), 1);
        if (font == null) {
            SimpleAlert.show(Alert.AlertType.WARNING, I18n.get().loadingFontFailed("noto"));
        }

        var itemsToLoad = new ArrayList<LoadingItem>();
        for (var path : Consts.ALL_IMAGE) {
            itemsToLoad.add(new LoadingItem(2, path, () -> ImageManager.get().load(path)));
        }
        for (var path : Consts.ALL_CLIP) {
            itemsToLoad.add(new LoadingItem(1, path, () -> AudioManager.get().loadAudio(path)));
        }
        itemsToLoad.add(new LoadingItem(1, I18n.get().hintPressAlt(), () -> MiscUtils.threadSleep(120)));

        var loadingStage = new LoadingStage(I18n.get().loadingStageTitle());
        loadingStage.setItems(itemsToLoad);
        loadingStage.load(new Callback<>() {
            @Override
            protected void succeeded0(Void unused) {
                stage.getIcons().add(ImageManager.get().load("images/icon/icon.jpg"));
                stage.setOnCloseRequest(e -> terminate());

                mainScreen = new MainScreen();
                var root = mainScreen;
                Scene scene = new Scene(root);
                stage.setScene(scene);

                MainScreen.initStage(stage);
                stage.show();
            }

            @Override
            protected void failed0(LoadingItem loadingItem) {
                System.exit(1);
            }
        });
    }

    private void terminate() {
        GlobalScreenUtils.unregister();
        TaskManager.get().terminate();
        FeedThread.get().terminate();
        var mainScreen = this.mainScreen;
        if (mainScreen != null) mainScreen.terminate();
        GlobalValues.vertx.close();
    }

    public static void main(String[] args) {
        InternalI18n.setInstance(I18n.get());
        //noinspection Convert2Lambda
        FontManager.get().setFontProvider(new FontProvider() {
            @Override
            public String name(FontUsage fontUsage) {
                if (fontUsage == Consts.NotoFont) {
                    return "Noto Sans Regular";
                } else {
                    return "Smiley Sans Oblique";
                }
            }
        });

        var dllPath = "/dll/JNativeHook_x64.dll";
        var dllStream = Main.class.getResourceAsStream(dllPath);
        if (dllStream == null) {
            Logger.error(dllPath + " not found, program might not work");
        } else {
            GlobalScreenUtils.releaseJNativeHookNativeToTmpDir("dll", dllStream);
        }
        FeedThread.get().start();
        launch();
    }
}
