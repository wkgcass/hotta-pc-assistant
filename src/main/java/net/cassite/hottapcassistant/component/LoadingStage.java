package net.cassite.hottapcassistant.component;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.FontManager;
import net.cassite.hottapcassistant.util.ImageManager;
import net.cassite.hottapcassistant.util.TaskManager;
import net.cassite.hottapcassistant.util.Utils;

import java.util.Arrays;
import java.util.Iterator;

public class LoadingStage extends Stage {
    private final Label label = new Label() {{
        FontManager.setFont(this);
    }};
    private final ProgressBar progressBar = new ProgressBar();
    private final Runnable cb;
    private boolean isDone = false;

    public static void load(Runnable cb) {
        var s = new LoadingStage(cb);
        s.show();
        s.load();
    }

    private LoadingStage(Runnable cb) {
        this.cb = cb;

        setTitle(I18n.get().loadingStageTitle());
        setWidth(670);
        setHeight(120);
        setResizable(false);

        var pane = new StackPane();
        var scene = new Scene(pane);
        setScene(scene);

        var vbox = new VBox();
        vbox.getChildren().addAll(new VPadding(15), label, new VPadding(15), progressBar);
        vbox.setPadding(new Insets(0, 0, 0, 10));
        pane.getChildren().add(vbox);

        label.setText(I18n.get().loadingStageTitle());
        progressBar.setProgress(0);
        progressBar.setPrefWidth(650);

        setOnCloseRequest(e -> {
            if (isDone) {
                return;
            }
            System.exit(1);
        });
    }

    private void load() {
        double total = ImageManager.ALL.length;
        loadImages(total, 0, () -> Platform.runLater(() -> {
            isDone = true;
            label.setText(I18n.get().hintPressAlt());
            TaskManager.execute(() -> {
                try {
                    Thread.sleep(120);
                } catch (InterruptedException ignore) {
                }
                Platform.runLater(() -> {
                    close();
                    Platform.runLater(cb);
                });
            });
        }));
    }

    @SuppressWarnings("SameParameterValue")
    private void loadImages(double total, double initial, Runnable cb) {
        var ite = Arrays.asList(ImageManager.ALL).iterator();
        loadImagesRecursive(total, initial, 1, ite, cb);
    }

    private void loadImagesRecursive(double total, double initial, int count, Iterator<String> ite, Runnable cb) {
        if (!ite.hasNext()) {
            cb.run();
            return;
        }
        var path = ite.next();
        Utils.runOnFX(() -> label.setText(path));
        TaskManager.execute(() -> {
            ImageManager.get().load(path);
            Platform.runLater(() -> {
                progressBar.setProgress((initial + count * 1.0) / total);
                loadImagesRecursive(total, initial, count + 1, ite, cb);
            });
        });
    }
}
