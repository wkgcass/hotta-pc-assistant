package net.cassite.hottapcassistant.ui;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import net.cassite.hottapcassistant.component.HPadding;
import net.cassite.hottapcassistant.component.VPadding;
import net.cassite.hottapcassistant.tool.LansBrainWash;
import net.cassite.hottapcassistant.tool.MessageHelper;
import net.cassite.hottapcassistant.tool.Tool;
import net.cassite.hottapcassistant.tool.WorldBossTimer;
import net.cassite.hottapcassistant.util.FontManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ToolBoxPane extends Pane implements Terminate {
    private static final List<Supplier<Tool>> tools = new ArrayList<>() {{
        add(WorldBossTimer::new);
        add(MessageHelper::new);
        add(LansBrainWash::new);
    }};
    private final List<Tool> toolInstances = new ArrayList<>();

    public ToolBoxPane() {
        var gridPane = new GridPane();
        getChildren().add(new VBox(
            new VPadding(10),
            new HBox(new HPadding(10), gridPane)
        ));

        initTools(gridPane);
    }

    private void initTools(GridPane grid) {
        grid.setHgap(10);
        grid.setVgap(10);
        var i = 0;
        for (var tool : tools) {
            var t = tool.get();
            toolInstances.add(t);

            var icon = t.getIcon();
            var name = t.getName();

            var imageView = new ImageView(icon);
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);

            var label = new Label(name) {{
                FontManager.setFont(this);
            }};

            var vbox = new VBox();
            vbox.setAlignment(Pos.CENTER);
            vbox.getChildren().addAll(imageView, new VPadding(2), label);

            vbox.setOnMouseClicked(e -> {
                if (t.isRunning())
                    t.alert();
                else {
                    if (t instanceof EnterCheck) {
                        var checkPass = ((EnterCheck) t).enterCheck(false);
                        if (!checkPass) return;
                    }
                    t.launch();
                }
            });
            vbox.setCursor(Cursor.HAND);

            final var colsPerLine = 5;
            grid.add(vbox, i % colsPerLine, i / colsPerLine);
            ++i;
        }
    }

    @Override
    public void terminate() {
        for (var t : toolInstances) {
            t.terminate();
        }
    }
}
