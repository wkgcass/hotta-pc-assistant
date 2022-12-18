package net.cassite.hottapcassistant.component.cooldown;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;

public class CoolDownArc extends Group {
    private final Circle circle;
    private final Arc arc;
    private final Circle clip;

    public CoolDownArc(Paint arcFill) {
        this.circle = new Circle();
        this.arc = new Arc();
        this.clip = new Circle();

        arc.setType(ArcType.ROUND);

        arc.setStrokeWidth(0);
        arc.setFill(arcFill);

        circle.setStrokeWidth(0);
        circle.setFill(Color.color(0xff / 255d, 0xff / 255d, 0xff / 255d, 0.3));

        getChildren().addAll(circle, arc);

        clip.setFill(Color.TRANSPARENT);
        clip.setStroke(Color.WHITE);
        this.setClip(clip);
    }

    public void setRadius(double r, double w) {
        circle.setRadius(r);
        arc.setRadiusX(r);
        arc.setRadiusY(r);
        clip.setRadius(r - w / 2);
        clip.setStrokeWidth(w);
    }

    public void setPercentage(double p) {
        arc.setStartAngle(-90 + 360 * (1 - p));
        arc.setLength(360 * p);
    }
}
