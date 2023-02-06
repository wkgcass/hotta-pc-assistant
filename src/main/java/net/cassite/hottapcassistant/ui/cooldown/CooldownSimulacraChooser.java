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
import net.cassite.hottapcassistant.entity.SimulacraRef;

public class CooldownSimulacraChooser {
    private static final Image unselectedImage = ImageManager.get().load("/images/icon/question.png:white");
    private static final int WIDTH_HEIGHT = 90;
    private final FusionPane pane = new FusionPane(false);

    public CooldownSimulacraChooser(SimpleObjectProperty<SimulacraRef> simulacraProperty) {
        var simulacraVBox = new VBox();
        simulacraVBox.setSpacing(4);

        pane.getContentPane().getChildren().add(simulacraVBox);

        var image = new ImageView(unselectedImage);
        image.setFitWidth(WIDTH_HEIGHT);
        image.setFitHeight(WIDTH_HEIGHT);

        var simulacraRefComboBox = new ComboBox<SimulacraRef>();
        simulacraRefComboBox.setPrefWidth(WIDTH_HEIGHT);
        simulacraRefComboBox.setEditable(false);
        simulacraRefComboBox.setItems(FXCollections.observableList(SimulacraRef.all()));
        simulacraRefComboBox.getSelectionModel().select(0);
        simulacraRefComboBox.setOnAction(e -> simulacraProperty.set(simulacraRefComboBox.getValue()));
        simulacraRefComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(SimulacraRef object) {
                if (object == null) return "";
                return object.name;
            }

            @Override
            public SimulacraRef fromString(String string) {
                throw new UnsupportedOperationException();
            }
        });
        simulacraProperty.addListener((ob, old, now) -> {
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
            for (var s : simulacraRefComboBox.getItems()) {
                if (s.id == now.id) {
                    if (s != now) {
                        simulacraRefComboBox.setValue(s);
                        simulacraProperty.set(s);
                    }
                    break;
                }
            }
        });

        simulacraVBox.getChildren().addAll(image, new FusionW(simulacraRefComboBox));
    }

    public Node getNode() {
        return pane.getNode();
    }
}
