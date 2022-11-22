package net.cassite.hottapcassistant.component.shapes;

import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import net.cassite.hottapcassistant.entity.Point;
import net.cassite.hottapcassistant.util.DragHandler;
import net.cassite.hottapcassistant.util.FontManager;
import net.cassite.hottapcassistant.util.Utils;

public class MovablePoint extends Group {
    public MovablePoint(String labelText) {
        var point = new Circle(5);
        point.setFill(Color.RED);
        point.setStrokeWidth(0);
        point.setStroke(Color.TRANSPARENT);
        var label = new Label(labelText) {{
            FontManager.setFont(this);
        }};
        point.setCursor(Cursor.MOVE);
        label.setTextFill(Color.RED);
        var wh = Utils.calculateTextBounds(label);
        label.setLayoutX(-wh.getWidth() / 2);
        label.setLayoutY(10);

        var handler = new DragHandler((xy) -> {
            setLayoutX(xy[0]);
            setLayoutY(xy[1]);
        }, () -> new double[]{getLayoutX(), getLayoutY()});

        point.setOnMousePressed(handler);
        point.setOnMouseDragged(handler);

        getChildren().addAll(label, point);
    }

    public Point makePoint() {
        var point = new Point();
        point.x = getLayoutX();
        point.y = getLayoutY();
        return point;
    }

    public void from(Point point) {
        setLayoutX(point.x);
        setLayoutY(point.y);
    }
}
