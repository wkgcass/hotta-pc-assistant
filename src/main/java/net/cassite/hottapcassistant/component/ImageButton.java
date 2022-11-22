package net.cassite.hottapcassistant.component;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import net.cassite.hottapcassistant.i18n.I18n;

public class ImageButton extends ImageView {
    private final double w;
    private final double h;
    private EventHandler<Event> handler;

    public ImageButton(String prefix, String suffix) {
        setCursor(Cursor.HAND);

        var normalImage = new Image(prefix + "-normal-" + I18n.get().id() + "." + suffix, false);
        w = normalImage.getWidth();
        h = normalImage.getHeight();
        var hoverImage = new Image(prefix + "-hover-" + I18n.get().id() + "." + suffix, true);
        var downImage = new Image(prefix + "-down-" + I18n.get().id() + "." + suffix, true);
        setImage(normalImage);
        setOnMouseEntered(e -> setImage(hoverImage));
        setOnMouseExited(e -> setImage(normalImage));
        setOnMousePressed(e -> setImage(downImage));
        setOnMouseReleased(e -> setImage(normalImage));
        setOnMouseClicked(e -> {
            var handler = this.handler;
            if (handler == null) {
                return;
            }
            handler.handle(null);
        });
    }

    public void setOnAction(EventHandler<? extends Event> handler) {
        //noinspection unchecked
        this.handler = (EventHandler<Event>) handler;
    }

    public void setScale(double v) {
        setFitWidth(w * v);
        setFitHeight(h * v);
    }
}
