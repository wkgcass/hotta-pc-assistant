package net.cassite.hottapcassistant;

import io.vproxy.base.util.LogType;
import io.vproxy.base.util.Logger;
import io.vproxy.base.util.callback.Callback;
import io.vproxy.commons.util.Singleton;
import io.vproxy.vfx.control.globalscreen.GlobalScreenUtils;
import io.vproxy.vfx.manager.audio.AudioManager;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.manager.font.FontProvider;
import io.vproxy.vfx.manager.font.FontSettings;
import io.vproxy.vfx.manager.font.FontUsage;
import io.vproxy.vfx.manager.image.ImageManager;
import io.vproxy.vfx.manager.internal_i18n.InternalI18n;
import io.vproxy.vfx.manager.task.TaskManager;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.theme.impl.DarkTheme;
import io.vproxy.vfx.theme.impl.DarkThemeFontProvider;
import io.vproxy.vfx.ui.loading.LoadingFailure;
import io.vproxy.vfx.ui.loading.LoadingItem;
import io.vproxy.vfx.ui.loading.LoadingPane;
import io.vproxy.vfx.ui.scene.VSceneGroup;
import io.vproxy.vfx.ui.scene.VSceneShowMethod;
import io.vproxy.vfx.ui.stage.VStage;
import io.vproxy.vfx.util.FXUtils;
import io.vproxy.vfx.util.MiscUtils;
import javafx.application.Application;
import javafx.stage.Stage;
import net.cassite.hottapcassistant.feed.FeedThread;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.multi.MultiHottaInstanceFlow;
import net.cassite.hottapcassistant.ui.Terminate;
import net.cassite.hottapcassistant.ui.UIEntry;
import net.cassite.hottapcassistant.util.Consts;
import net.cassite.hottapcassistant.util.GlobalValues;
import net.cassite.hottapcassistant.util.Version;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

public class Main extends Application {
    private VSceneGroup mainSceneGroup;

    @Override
    public void start(Stage primaryStage) {
        var itemsToLoad = new ArrayList<LoadingItem>();
        for (var path : Consts.ALL_IMAGE) {
            itemsToLoad.add(new LoadingItem(2, path, () -> ImageManager.get().load(path)));
        }
        itemsToLoad.add(new LoadingItem(2, "/images/icon/menu.png:white", () ->
            ImageManager.get().loadBlackAndChangeColor("/images/icon/menu.png", Map.of("white", 0xffffffff))));
        itemsToLoad.add(new LoadingItem(2, "/images/icon/question.png:white", () ->
            ImageManager.get().loadBlackAndChangeColor("/images/icon/question.png", Map.of("white", 0xffffffff))));
        itemsToLoad.add(new LoadingItem(2, "/images/icon/return.png:white", () ->
            ImageManager.get().loadBlackAndChangeColor("/images/icon/return.png", Map.of("white", 0xffffffff))));
        for (var path : Consts.ALL_CLIP) {
            itemsToLoad.add(new LoadingItem(1, path, () -> AudioManager.get().loadAudio(path)));
        }
        itemsToLoad.add(new LoadingItem(1, I18n.get().progressWelcomeText(), () -> MiscUtils.threadSleep(50)));

        var stage = new VStage(primaryStage) {
            @Override
            public void close() {
                terminate();
                super.close();
            }
        };
        var title = I18n.get().titleMainScreen();
        //noinspection ConstantConditions
        if (Version.version.endsWith("-dev")) {
            title = title + " " + I18n.get().titleMainScreenDevVersion();
        }
        stage.setTitle(title);
        stage.getStage().getIcons().add(ImageManager.get().load("images/icon/moliniya.png"));
        stage.getStage().setWidth(1100);
        stage.getStage().setHeight(800);
        stage.getStage().centerOnScreen();
        stage.show();

        var loadingPane = new LoadingPane(I18n.get().loadingStageTitle());
        loadingPane.getProgressBar().setItems(itemsToLoad);
        loadingPane.setLength(600);

        mainSceneGroup = stage.getSceneGroup();
        stage.getInitialScene().enableAutoContentWidthHeight();
        var rootPane = stage.getInitialScene().getContentPane();
        FXUtils.observeWidthHeightCenter(rootPane, loadingPane);
        rootPane.getChildren().add(loadingPane);
        loadingPane.getProgressBar().load(new Callback<>() {
            @Override
            protected void onSucceeded(Void unused) {
                var uiEntry = new UIEntry(stage);
                var firstScene = uiEntry.mainScenes.get(0);
                stage.getSceneGroup().show(firstScene.getScene(), VSceneShowMethod.FADE_IN);
                uiEntry.init();
            }

            @Override
            protected void onFailed(LoadingFailure failure) {
                System.exit(1);
            }
        });
    }

    private void terminate() {
        if (mainSceneGroup != null) {
            for (var scene : new HashSet<>(mainSceneGroup.getScenes())) {
                if (scene instanceof Terminate) {
                    ((Terminate) scene).terminate();
                }
            }
        }
        FeedThread.get().terminate();
        GlobalValues.vertx.close();
        TaskManager.get().terminate();
        GlobalScreenUtils.unregister();
    }

    public static void main(String[] args) {
        Theme.setTheme(new DarkTheme() {
            @Override
            public FontProvider fontProvider() {
                return new DarkThemeFontProvider() {
                    @Override
                    protected void defaultFont(FontSettings settings) {
                        super.defaultFont(settings);
                        settings.setFamily(FontManager.FONT_NAME_SmileySansOblique);
                    }

                    @Override
                    protected void windowTitle(FontSettings settings) {
                        super.windowTitle(settings);
                        settings.setFamily(FontManager.FONT_NAME_NotoSansSCRegular);
                    }

                    @Override
                    protected void tableCellText(FontSettings settings) {
                        super.tableCellText(settings);
                        settings.setFamily(FontManager.FONT_NAME_NotoSansSCRegular);
                        settings.setSize(14);
                    }

                    @Override
                    protected void _default(FontUsage usage, FontSettings settings) {
                        super._default(usage, settings);
                        if (usage == Consts.NotoFont) {
                            settings.setFamily(FontManager.FONT_NAME_NotoSansSCRegular);
                        } else if (usage == Consts.JetbrainsMonoFont) {
                            settings.setFamily(FontManager.FONT_NAME_JetBrainsMono);
                        }
                    }
                };
            }
        });
        Singleton.register(InternalI18n.class, I18n.get());
        Singleton.register(net.cassite.xboxrelay.ui.I18n.class, I18n.get());

        var dllPath = "/dll/JNativeHook_x64.dll";
        var dllStream = Main.class.getResourceAsStream(dllPath);
        if (dllStream == null) {
            Logger.error(LogType.SYS_ERROR, dllPath + " not found, program might not work");
        } else {
            GlobalScreenUtils.releaseJNativeHookNativeToLibraryPath(dllStream);
        }

        cleanupLastRun();

        FeedThread.get().start();
        launch();
    }

    public static void cleanupLastRun() {
        try {
            MultiHottaInstanceFlow.unsetHostsFile();
        } catch (Throwable t) {
            Logger.error(LogType.FILE_ERROR, "failed clean up hosts related to multi-hotta-instances");
        }
    }
}
