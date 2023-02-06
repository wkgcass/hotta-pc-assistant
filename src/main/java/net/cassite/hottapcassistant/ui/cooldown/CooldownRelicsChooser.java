package net.cassite.hottapcassistant.ui.cooldown;

import io.vproxy.vfx.manager.image.ImageManager;
import io.vproxy.vfx.ui.pane.FusionPane;
import io.vproxy.vfx.ui.wrapper.FusionW;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import net.cassite.hottapcassistant.entity.RelicsRef;
import net.cassite.hottapcassistant.i18n.I18n;

import java.util.Arrays;

public class CooldownRelicsChooser {
    private static final Image unselectedImage = ImageManager.get().load("/images/icon/question.png:white");
    private static final int WIDTH_HEIGHT = 90;
    private final FusionPane pane = new FusionPane(false);

    public CooldownRelicsChooser(SimpleObjectProperty<RelicsRef> relicsProperty, int index) {
        var relicsVBox = new VBox();
        relicsVBox.setSpacing(4);

        pane.getContentPane().getChildren().add(relicsVBox);

        var image = new ImageView(unselectedImage);
        image.setFitWidth(WIDTH_HEIGHT);
        image.setFitHeight(WIDTH_HEIGHT);
        image.setPreserveRatio(true);
        var relics0 = new ComboBox<RelicsRef>();
        var relicsStars0 = new ComboBox<Integer>();

        relicsVBox.getChildren().addAll(image, new FusionW(relics0), new FusionW(relicsStars0));

        relics0.setConverter(new StringConverter<>() {
            @Override
            public String toString(RelicsRef object) {
                if (object == null)
                    return I18n.get().relicsChooserPlaceHolder(index);
                return object.name;
            }

            @Override
            public RelicsRef fromString(String string) {
                throw new UnsupportedOperationException();
            }
        });
        relics0.setItems(FXCollections.observableList(RelicsRef.all()));
        relics0.getItems().forEach(e -> e.starsSupplier = relicsStars0::getValue);
        relics0.setOnAction(e -> relicsProperty.set(relics0.getValue()));
        relicsProperty.addListener((ob, old, now) -> {
            if (now == null) {
                image.setImage(unselectedImage);
                return;
            }
            var img = now.image;
            if (img == null) {
                image.setImage(unselectedImage);
            } else {
                image.setImage(img);
            }
            for (var r : relics0.getItems()) {
                if (r.id == now.id && r != now) {
                    relics0.setValue(r);
                    relicsStars0.setValue(now.getStars());
                    relicsProperty.setValue(r);
                }
            }
        });
        relics0.setPrefWidth(WIDTH_HEIGHT);
        relicsStars0.setEditable(false);
        relicsStars0.setItems(FXCollections.observableList(Arrays.asList(0, 1, 2, 3, 4, 5)));
        relicsStars0.getSelectionModel().select(3);
        relicsStars0.setPrefWidth(WIDTH_HEIGHT);
    }

    public Node getNode() {
        return pane.getNode();
    }
}
