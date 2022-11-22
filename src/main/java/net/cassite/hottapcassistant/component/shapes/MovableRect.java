package net.cassite.hottapcassistant.component.shapes;

import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import net.cassite.hottapcassistant.entity.Rect;
import net.cassite.hottapcassistant.util.DragHandler;
import net.cassite.hottapcassistant.util.FontManager;

public class MovableRect extends Group {
    private final Rectangle rect;

    public MovableRect(String labelText) {
        rect = new Rectangle();
        rect.setFill(Color.TRANSPARENT);
        rect.setStroke(Color.RED);
        rect.setStrokeWidth(5);
        rect.setCursor(Cursor.MOVE);

        var pointRightBottom = new Rectangle();
        pointRightBottom.setWidth(10);
        pointRightBottom.setHeight(10);
        pointRightBottom.setFill(Color.RED);
        pointRightBottom.setStrokeWidth(0);
        pointRightBottom.setStroke(Color.TRANSPARENT);
        pointRightBottom.setCursor(Cursor.SE_RESIZE);

        rect.widthProperty().addListener((ob, old, now) -> {
            if (now == null) return;
            pointRightBottom.setLayoutX(now.doubleValue() - 10);
        });
        rect.heightProperty().addListener((ob, old, now) -> {
            if (now == null) return;
            pointRightBottom.setLayoutY(now.doubleValue() - 10);
        });

        var label = new Label(labelText) {{
            FontManager.setFont(this);
        }};
        label.setTextFill(Color.RED);
        label.setLayoutX(0);
        rect.heightProperty().addListener((ob, old, now) -> {
            if (now == null) return;
            label.setLayoutY(now.doubleValue() + 5);
        });

        var dragHandler = new DragHandler((xy) -> {
            setLayoutX(xy[0]);
            setLayoutY(xy[1]);
        }, () -> new double[]{getLayoutX(), getLayoutY()});

        rect.setOnMousePressed(dragHandler);
        rect.setOnMouseDragged(dragHandler);

        var resizeHandler = new DragHandler((xy) -> {
            if (xy[0] > 5) rect.setWidth(xy[0]);
            if (xy[1] > 5) rect.setHeight(xy[1]);
        }, () -> new double[]{rect.getWidth(), rect.getHeight()});

        pointRightBottom.setOnMousePressed(resizeHandler);
        pointRightBottom.setOnMouseDragged(resizeHandler);

        getChildren().addAll(label, rect, pointRightBottom);
    }

    public void from(Rect rect) {
        setLayoutX(rect.x);
        setLayoutY(rect.y);
        setWidth(rect.w);
        setHeight(rect.h);
    }

    public Rect makeRect() {
        var rect = new Rect();
        rect.x = getLayoutX();
        rect.y = getLayoutY();
        rect.w = getWidth();
        rect.h = getHeight();
        return rect;
    }

    public double getWidth() {
        return rect.getWidth();
    }

    public double getHeight() {
        return rect.getHeight();
    }

    public void setWidth(double width) {
        rect.setWidth(width);
    }

    public void setHeight(double height) {
        rect.setHeight(height);
    }
}
