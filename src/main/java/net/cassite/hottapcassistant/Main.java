package net.cassite.hottapcassistant;

import io.vproxy.base.util.LogType;
import io.vproxy.base.util.Logger;
import io.vproxy.base.util.Utils;
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
import io.vproxy.vfx.ui.alert.SimpleAlert;
import io.vproxy.vfx.ui.button.TransparentFusionButton;
import io.vproxy.vfx.ui.loading.LoadingFailure;
import io.vproxy.vfx.ui.loading.LoadingItem;
import io.vproxy.vfx.ui.loading.LoadingPane;
import io.vproxy.vfx.ui.scene.VSceneGroup;
import io.vproxy.vfx.ui.scene.VSceneShowMethod;
import io.vproxy.vfx.ui.stage.VStage;
import io.vproxy.vfx.util.FXUtils;
import io.vproxy.vfx.util.MiscUtils;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import net.cassite.hottapcassistant.feed.Feed;
import net.cassite.hottapcassistant.feed.FeedThread;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.multi.MultiHottaInstanceFlow;
import net.cassite.hottapcassistant.ui.Terminate;
import net.cassite.hottapcassistant.ui.UIEntry;
import net.cassite.hottapcassistant.util.Consts;
import net.cassite.hottapcassistant.util.Version;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

public class Main extends Application {
    private VSceneGroup mainSceneGroup;

    @Override
    public void start(Stage primaryStage) {
        cleanupLastRun();

        var itemsToLoad = new ArrayList<LoadingItem>();
        for (var path : Consts.ALL_IMAGE) {
            itemsToLoad.add(new LoadingItem(2, path, () -> {
                var img = ImageManager.get().load(path);
                if (img == null) {
                    return false;
                }
                if (!Consts.PRELOAD_IMAGE.contains(path)) {
                    ImageManager.get().remove(path);
                }
                return true;
            }));
        }
        itemsToLoad.add(new LoadingItem(2, "/images/icon/menu.png:white", () ->
            ImageManager.get().loadBlackAndChangeColor("/images/icon/menu.png", Map.of("white", 0xffffffff))));
        itemsToLoad.add(new LoadingItem(2, "/images/icon/question.png:white", () ->
            ImageManager.get().loadBlackAndChangeColor("/images/icon/question.png", Map.of("white", 0xffffffff))));
        itemsToLoad.add(new LoadingItem(2, "/images/icon/return.png:white", () ->
            ImageManager.get().loadBlackAndChangeColor("/images/icon/return.png", Map.of("white", 0xffffffff))));
        for (var path : Consts.ALL_CLIP) {
            itemsToLoad.add(new LoadingItem(1, path, () -> {
                var audio = AudioManager.get().loadAudio(path);
                if (audio == null) {
                    return false;
                }
                AudioManager.get().removeAudio(path);
                return true;
            }));
        }
        itemsToLoad.add(new LoadingItem(1, I18n.get().waitForStartupVideoToFinish(), () -> {
            long waitBegin = System.currentTimeMillis();
            while (isPlayingStartupVideo) {
                MiscUtils.threadSleep(1);
                long waitTs = System.currentTimeMillis();
                // at most wait for ... millis
                if (waitTs - waitBegin > 20_000) {
                    break;
                }
            }
        }));
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
        if (Version.version.contains("-")) {
            title = title + " " + I18n.get().titleMainScreenDevVersion();
        }
        stage.setTitle(title);
        stage.getStage().getIcons().add(ImageManager.get().load("images/icon/Avatar_150.png"));
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
                FXUtils.runOnFX(() -> {
                    SimpleAlert.showAndWait(Alert.AlertType.ERROR,
                        I18n.get().loadingFailedErrorMessage(failure.failedItem));
                    System.exit(1);
                });
            }
        });
        playStartupVideo(stage);
    }

    private volatile boolean isPlayingStartupVideo = false;

    private void playStartupVideo(VStage stage) {
        Runnable endCall = null;
        try {
            //noinspection DataFlowIssue
            var media = new Media(Main.class.getResource("/video/Luosilin.mp4").toExternalForm());
            var player = new MediaPlayer(media);
            var viewer = new MediaView(player);
            player.setMute(true);

            var rootNode = stage.getRoot().getContentPane();
            rootNode.getChildren().add(viewer);

            Runnable mediaWatcher = () -> {
                var mw = media.getWidth();
                var mh = media.getHeight();
                var nw = rootNode.getPrefWidth();
                var nh = rootNode.getPrefHeight();

                if (mw / (double) mh > nw / nh) {
                    viewer.setFitHeight(nh);
                    viewer.setLayoutX((nh / mh * mw - mw) / 2);
                } else {
                    viewer.setFitWidth(nw);
                    viewer.setFitHeight((nw / mw * mh - mh) / 2);
                }
            };
            media.widthProperty().addListener((ob) -> mediaWatcher.run());
            media.heightProperty().addListener((ob) -> mediaWatcher.run());

            var skipButton = new TransparentFusionButton(I18n.get().skipAnimation()) {{
                setPrefWidth(60);
                setPrefHeight(32);
                FontManager.get().setFont(getTextNode());
                setLayoutX(rootNode.getPrefWidth() - getPrefWidth() - 30);
                setLayoutY(30);
            }};
            rootNode.getChildren().add(skipButton);

            endCall = () -> {
                rootNode.getChildren().removeAll(viewer, skipButton);
                isPlayingStartupVideo = false;
            };
            final Runnable fendCall = endCall;
            skipButton.setOnAction(e -> {
                player.stop();
                fendCall.run();
            });
            player.setOnEndOfMedia(endCall);

            isPlayingStartupVideo = true;
            player.play();
        } catch (Exception e) {
            Logger.error(LogType.ALERT, "failed to play startup video", e);
            if (endCall != null) {
                endCall.run();
            }
        }
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
        TaskManager.get().terminate();
        GlobalScreenUtils.unregister();
    }

    public static void main(String[] args) {
        FXUtils.generalInitialization(args);

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

        Feed.get().lastCriticalVersion.addListener((ob, old, ver) -> {
            if (ver == null)
                return;
            try {
                Utils.validateVProxyVersion(ver);
            } catch (Exception e) {
                Logger.warn(LogType.ALERT, "invalid version from feed: " + ver, e);
                return;
            }
            if (Utils.compareVProxyVersions(Version.version, ver) < 0) {
                SimpleAlert.show(Alert.AlertType.INFORMATION, I18n.get().newCriticalVersionAvailable(ver));
            }
        });
        FeedThread.get().start();
        launch();
    }

    public static boolean cleanupLastRun() {
        var ok = MultiHottaInstanceFlow.unsetHostsFile();
        if (!ok) {
            Logger.error(LogType.FILE_ERROR, "failed clean up hosts related to multi-hotta-instances");
            SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().clearHostsFailed());
        }
        return ok;
    }
}
