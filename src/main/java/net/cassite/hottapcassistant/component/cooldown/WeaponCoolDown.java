package net.cassite.hottapcassistant.component.cooldown;

import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.transform.Translate;
import net.cassite.hottapcassistant.util.Utils;

public class WeaponCoolDown extends Group implements WithId, WithDesc {
    static final int INNER_RADIUS = 28;
    static final int FONT_SIZE = 24;
    private static final int STROKE_WIDTH = 4;
    private static final int L1_RADIUS = INNER_RADIUS + STROKE_WIDTH + 1;
    private static final int L2_RADIUS = L1_RADIUS + STROKE_WIDTH;
    public static final double MAX_RADIUS = L2_RADIUS;
    public static final double MIN_RADIUS = INNER_RADIUS;
    static final Color INACTIVE_COLOR = Color.color(0x88 / 255d, 0x88 / 255d, 0x88 / 255d, 0.5);
    private static final Color ACTIVE_COLOR = Color.color(0xff / 255d, 0x99 / 255d, 0x66 / 255d, 0.9);

    private final CoolDownArc[] cds;
    private final Group cdPane;
    private final Label cdNumber;
    private final ImageView imageView;
    private final SimpleObjectProperty<Color> backgroundColor = new SimpleObjectProperty<>(INACTIVE_COLOR);
    private final Translate translate = new Translate();
    private CoolDownOKAnimationTimer cooldownOKTimer = new CoolDownOKAnimationTimer();

    private final String id;
    private final String desc;

    public WeaponCoolDown(Image image, String id, String desc) {
        this(image, 1, id, desc);
    }

    public WeaponCoolDown(Image image, double imageScale, String id, String desc) {
        this.id = id;
        this.desc = desc;

        var innerCircle = new Circle();
        innerCircle.setFill(Color.TRANSPARENT);
        innerCircle.setStrokeWidth(1);
        innerCircle.setStroke(Color.WHITE);
        innerCircle.setRadius(INNER_RADIUS);

        this.cdPane = new Group();
        this.cdNumber = new Label();
        cdNumber.setFont(new Font(FONT_SIZE));
        cdNumber.setTextFill(Color.WHITE);
        var cdPaneBackground = new Circle();
        cdPaneBackground.setRadius(INNER_RADIUS);
        cdPaneBackground.setFill(Color.color(0.5019608f, 0.5019608f, 0.5019608f, 0.7));
        cdPane.getChildren().addAll(cdPaneBackground, cdNumber);
        cdPane.setVisible(false);

        var maskCircle = new Circle();
        maskCircle.setFill(Color.WHITE);
        maskCircle.setStrokeWidth(0);
        maskCircle.setRadius(INNER_RADIUS);
        maskCircle.setCenterX(INNER_RADIUS * imageScale);
        maskCircle.setCenterY(INNER_RADIUS * imageScale);
        imageView = new ImageView(image);
        imageView.setFitWidth(INNER_RADIUS * 2 * imageScale);
        imageView.setFitHeight(INNER_RADIUS * 2 * imageScale);
        imageView.setLayoutX(-INNER_RADIUS * imageScale);
        imageView.setLayoutY(-INNER_RADIUS * imageScale);
        imageView.setClip(maskCircle);

        var innerBackground = new Circle();
        innerBackground.setFill(INACTIVE_COLOR);
        innerBackground.setStrokeWidth(0);
        innerBackground.setRadius(INNER_RADIUS);
        backgroundColor.addListener((ob, old, now) -> {
            if (now == null) return;
            innerBackground.setFill(now);
        });

        var cd0 = new CoolDownArc(Color.color(0xff / 255d, 0x66 / 255d, 0x66 / 255d));
        cd0.setRadius(L1_RADIUS, STROKE_WIDTH);
        cd0.setVisible(false);
        var cd1 = new CoolDownArc(Color.color(0x00 / 255d, 0x99 / 255d, 0xcc / 255d));
        cd1.setRadius(L2_RADIUS, STROKE_WIDTH);
        cd1.setVisible(false);
        this.cds = new CoolDownArc[]{cd0, cd1};

        getChildren().addAll(cd1, cd0, innerBackground, imageView, cdPane, innerCircle);

        isCoolingDown.addListener((ob, old, now) -> {
            if (now == null) return;
            if (old == now) return;
            if (now) {
                translate.setY(0);
                var timer = this.cooldownOKTimer;
                this.cooldownOKTimer = null;
                if (timer != null) {
                    timer.stop();
                }
            } else {
                var timer = new CoolDownOKAnimationTimer();
                timer.start();
                this.cooldownOKTimer = timer;
            }
        });
        getTransforms().add(translate);
    }

    private final SimpleBooleanProperty isCoolingDown = new SimpleBooleanProperty(false);

    private class CoolDownOKAnimationTimer extends AnimationTimer {
        long beginTime;

        @Override
        public void handle(long now) {
            if (beginTime == 0) {
                beginTime = now;
                return;
            }
            var delta = (double) (now - beginTime);
            if (delta > 500_000_000) {
                translate.setY(0);
                stop();
                cooldownOKTimer = null;
            } else if (delta > 250_000_000) {
                var d = delta - 250_000_000;
                d /= 250_000_000;
                var v = (d * d - d) * 15;
                translate.setY(v);
            } else {
                var d = delta / 250_000_000;
                var v = (d * d - d) * 40;
                translate.setY(v);
            }
        }
    }

    public void setCoolDown(long time) {
        if (time == 0) {
            cdPane.setVisible(false);
            isCoolingDown.set(false);
            return;
        }
        cdPane.setVisible(true);
        var str = Utils.roughFloatValueFormat.format(time / 1000d);
        var needCalculate = cdNumber.getText() == null || cdNumber.getText().length() != str.length();
        cdNumber.setText(str);
        if (needCalculate) {
            var rect = Utils.calculateTextBounds(cdNumber);
            double x = -rect.getWidth() / 2;
            double y = -rect.getHeight() / 2;
            cdNumber.setLayoutX(x);
            cdNumber.setLayoutY(y);
        }
        isCoolingDown.set(true);
    }

    public void setAllCoolDown(long time, long total) {
        setCoolDown(time);
        if (time == 0) setAllCoolDown(null);
        else setAllCoolDown(new double[]{time / (double) total});
    }

    public void setAllCoolDown(double[] cd) {
        if (cd == null || cd.length == 0) {
            for (var c : cds) {
                c.setVisible(false);
            }
        } else {
            for (var i = 0; i < cds.length && i < cd.length; ++i) {
                cds[i].setVisible(true);
                cds[i].setPercentage(cd[i]);
            }
            for (var i = cd.length; i < cds.length; ++i) {
                cds[i].setVisible(false);
            }
        }
    }

    public void setActive(boolean active) {
        if (active) {
            backgroundColor.set(ACTIVE_COLOR);
        } else {
            backgroundColor.set(INACTIVE_COLOR);
        }
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String desc() {
        return desc;
    }
}
