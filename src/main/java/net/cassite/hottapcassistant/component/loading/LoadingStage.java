package net.cassite.hottapcassistant.component.loading;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.cassite.hottapcassistant.component.VPadding;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.FontManager;
import net.cassite.hottapcassistant.util.TaskManager;
import net.cassite.hottapcassistant.util.Utils;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class LoadingStage extends Stage {
    private final Label label = new Label() {{
        FontManager.setFont(this);
    }};
    private final ProgressBar progressBar = new ProgressBar();
    private final List<LoadingItem> items;
    private final long delay;
    private final Runnable cb;
    private final Consumer<LoadingItem> failedCB;
    private boolean isDone = false;

    public static void load(List<LoadingItem> items, Runnable cb, Consumer<LoadingItem> failedCB) {
        load(items, 0, cb, failedCB);
    }

    public static void load(List<LoadingItem> items, long delay, Runnable cb, Consumer<LoadingItem> failedCB) {
        var s = new LoadingStage(items, delay, cb, failedCB);
        s.show();
        s.load();
    }

    private LoadingStage(List<LoadingItem> items, long delay, Runnable cb, Consumer<LoadingItem> failedCB) {
        this.items = items;
        this.delay = delay;
        this.cb = cb;
        this.failedCB = failedCB;

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
        progressBar.setPrefWidth(630);

        setOnCloseRequest(e -> {
            if (isDone) {
                return;
            }
            failedCB.accept(null);
        });
    }

    private void load() {
        long total = 0;
        for (var item : items) {
            total += item.weight();
        }
        loadItem(total, 0, items.iterator(), () -> Platform.runLater(() -> {
            isDone = true;
            Platform.runLater(() -> {
                close();
                Platform.runLater(cb);
            });
        }));
    }

    private void loadItem(long total, long current, Iterator<LoadingItem> ite, Runnable cb) {
        if (!ite.hasNext()) {
            cb.run();
            return;
        }
        var item = ite.next();
        var name = item.name();
        Utils.runOnFX(() -> label.setText(name));
        TaskManager.execute(() -> {
            var ok = item.loadFunc().getAsBoolean();
            if (ok) {
                if (delay > 0) {
                    Utils.delay(delay);
                }
            }
            Platform.runLater(() -> {
                if (!ok) {
                    close();
                    failedCB.accept(item);
                    return;
                }
                long newCurr = current + item.weight();
                progressBar.setProgress(newCurr / (double) total);
                loadItem(total, newCurr, ite, cb);
            });
        });
    }
}
