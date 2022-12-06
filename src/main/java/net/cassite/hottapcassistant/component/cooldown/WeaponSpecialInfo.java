package net.cassite.hottapcassistant.component.cooldown;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import net.cassite.hottapcassistant.util.Utils;

public class WeaponSpecialInfo extends Group {
    private static final int INNER_RADIUS = WeaponCoolDown.INNER_RADIUS;
    private static final int FONT_SIZE = WeaponCoolDown.FONT_SIZE;
    private final Group textPane;
    private final Label text;

    public WeaponSpecialInfo(Image image) {
        var innerCircle = new Circle();
        innerCircle.setFill(Color.TRANSPARENT);
        innerCircle.setStrokeWidth(1);
        innerCircle.setStroke(Color.WHITE);
        innerCircle.setRadius(INNER_RADIUS);

        this.textPane = new Group();
        this.text = new Label();
        text.setFont(new Font(FONT_SIZE));
        text.setTextFill(Color.WHITE);
        var textPaneBackground = new Circle();
        textPaneBackground.setRadius(INNER_RADIUS);
        textPaneBackground.setFill(Color.color(0.5019608f, 0.5019608f, 0.5019608f, 0.7));
        textPane.getChildren().addAll(textPaneBackground, text);
        textPane.setVisible(false);

        var maskCircle = new Circle();
        maskCircle.setFill(Color.WHITE);
        maskCircle.setStrokeWidth(0);
        maskCircle.setRadius(INNER_RADIUS);
        maskCircle.setCenterX(INNER_RADIUS);
        maskCircle.setCenterY(INNER_RADIUS);
        var imageView = new ImageView(image);
        imageView.setFitWidth(INNER_RADIUS * 2);
        imageView.setFitHeight(INNER_RADIUS * 2);
        imageView.setLayoutX(-INNER_RADIUS);
        imageView.setLayoutY(-INNER_RADIUS);
        imageView.setClip(maskCircle);

        var innerBackground = new Circle();
        innerBackground.setFill(WeaponCoolDown.INACTIVE_COLOR);
        innerBackground.setStrokeWidth(0);
        innerBackground.setRadius(INNER_RADIUS);

        getChildren().addAll(innerBackground, imageView, textPane, innerCircle);
    }

    public void setText(String str) {
        if (str == null || str.isBlank()) {
            textPane.setVisible(false);
            return;
        }
        if (str.equals(text.getText())) {
            return;
        }
        textPane.setVisible(true);
        text.setText(str);
        var rect = Utils.calculateTextBounds(text);
        text.setLayoutX(-rect.getWidth() / 2);
        text.setLayoutY(-rect.getHeight() / 2);
    }
}
