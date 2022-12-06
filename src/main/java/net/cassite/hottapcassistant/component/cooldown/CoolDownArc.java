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

    public CoolDownArc(Paint arcFill) {
        this.circle = new Circle();
        this.arc = new Arc();

        arc.setType(ArcType.ROUND);

        arc.setStrokeWidth(0);
        arc.setFill(arcFill);

        circle.setStrokeWidth(1);
        circle.setStroke(Color.WHITE);
        circle.setFill(Color.color(0xcc / 255d, 0xcc / 255d, 0xcc / 255d));

        getChildren().addAll(circle, arc);
    }

    public void setRadius(double r) {
        circle.setRadius(r);
        arc.setRadiusX(r);
        arc.setRadiusY(r);
    }

    public void setPercentage(double p) {
        arc.setStartAngle(-90 + 360 * (1 - p));
        arc.setLength(360 * p);
    }
}
