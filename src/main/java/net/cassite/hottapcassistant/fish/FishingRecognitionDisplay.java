package net.cassite.hottapcassistant.fish;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.cassite.hottapcassistant.entity.AssistantFishing;

public class FishingRecognitionDisplay extends Stage {
    private final Rectangle bar = new Rectangle();
    private final Rectangle pos = new Rectangle();

    public FishingRecognitionDisplay() {
        super(StageStyle.TRANSPARENT);
        setAlwaysOnTop(true);

        bar.setFill(Color.color(255f / 255, 176f / 255, 64f / 255));
        bar.setStrokeWidth(0);
        bar.setHeight(20);

        pos.setFill(Color.WHITE);
        pos.setStrokeWidth(2);
        pos.setStroke(Color.color(153f / 255, 128f / 255, 124f / 255));
        pos.setWidth(3);
        pos.setHeight(20);

        var group = new Group();
        var scene = new Scene(group);
        scene.setFill(Color.TRANSPARENT);
        setScene(scene);

        group.getChildren().addAll(bar, pos);
    }

    public void doShow(AssistantFishing config) {
        double x = config.posBarRect.x;
        double y = config.posBarRect.y;
        double w = config.posBarRect.w;
        double h = config.posBarRect.h;

        setX(x);
        setY(y + h + 20);
        setWidth(w);
        setHeight(20);

        hidePosBar();

        show();
    }

    public void hidePosBar() {
        pos.setVisible(false);
        bar.setVisible(false);
    }

    public void updatePosBar(int pos, int barLeft, int barRight) {
        this.pos.setVisible(pos >= 0);
        this.bar.setVisible(barLeft >= 0 && barRight >= 0);
        this.pos.setX(pos + 1.5);
        this.bar.setX(barLeft);
        this.bar.setWidth(barRight - barLeft);
    }
}
