package net.cassite.hottapcassistant.ui.cooldown;

import io.vproxy.vfx.manager.image.ImageManager;
import io.vproxy.vfx.ui.layout.VPadding;
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
import net.cassite.hottapcassistant.entity.MatrixRef;

import java.util.Arrays;

public class CooldownMatrixChooser {
    private static final Image unselectedImage = ImageManager.get().load("/images/icon/question.png:white");
    public static final int WIDTH_HEIGHT = 110;
    private final FusionPane pane = new FusionPane(false);

    public CooldownMatrixChooser(SimpleObjectProperty<MatrixRef> matrixProperty) {
        var matrixVBox = new VBox();
        matrixVBox.setSpacing(4);

        pane.getContentPane().getChildren().add(matrixVBox);

        var image = new ImageView(unselectedImage);
        var matrixSelect = new ComboBox<MatrixRef>();
        var starsSelect = new ComboBox<Integer>();
        matrixVBox.getChildren().addAll(new VPadding(30), image, new FusionW(matrixSelect), new FusionW(starsSelect));

        matrixProperty.addListener((ob, old, now) -> {
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
            for (var m : matrixSelect.getItems()) {
                if (m.id == now.id) {
                    if (m != now) {
                        matrixSelect.setValue(m);
                        starsSelect.setValue(now.getStars());
                        matrixProperty.set(m);
                    }
                    break;
                }
            }
        });
        matrixSelect.setConverter(new StringConverter<>() {
            @Override
            public String toString(MatrixRef object) {
                if (object == null) return "";
                return object.name;
            }

            @Override
            public MatrixRef fromString(String string) {
                throw new UnsupportedOperationException();
            }
        });
        matrixSelect.setEditable(false);
        matrixSelect.setItems(FXCollections.observableList(MatrixRef.all()));
        matrixSelect.getItems().forEach(e -> e.starsSupplier = starsSelect::getValue);
        matrixSelect.setOnAction(e -> {
            var selected = matrixSelect.getValue();
            if (selected == null) return;
            matrixProperty.set(selected);
        });
        matrixSelect.setPrefWidth(WIDTH_HEIGHT);
        image.setFitWidth(WIDTH_HEIGHT);
        image.setFitHeight(WIDTH_HEIGHT);
        starsSelect.setEditable(false);
        starsSelect.setItems(FXCollections.observableList(Arrays.asList(0, 1, 2, 3)));
        starsSelect.getSelectionModel().select(3);
        starsSelect.setPrefWidth(WIDTH_HEIGHT);
    }

    public Node getNode() {
        return pane.getNode();
    }
}
