package net.cassite.hottapcassistant.ui.cooldown;

import io.vproxy.vfx.manager.image.ImageManager;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.pane.FusionPane;
import io.vproxy.vfx.ui.scene.VSceneGroup;
import io.vproxy.vfx.ui.scene.VSceneShowMethod;
import io.vproxy.vfx.ui.shapes.VLine;
import io.vproxy.vfx.ui.wrapper.FusionW;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import net.cassite.hottapcassistant.entity.WeaponRef;

import java.util.Arrays;
import java.util.List;

public class CooldownWeaponChooser {
    private static final Image unselectedImage = ImageManager.get().load("/images/icon/question.png:white");
    public static final int WIDTH_HEIGHT = 140;
    private final FusionPane pane = new FusionPane(false);
    private final VLine line = new VLine(4) {{
        setStartX(2);
        setEndX(WIDTH_HEIGHT - 2);
    }};

    public CooldownWeaponChooser(VSceneGroup matrixSceneGroup,
                                 List<CooldownMatrixScene> scenes,
                                 List<CooldownWeaponChooser> choosers,
                                 int thisIndex,
                                 SimpleObjectProperty<WeaponRef> weaponProperty) {
        var weaponVBox = new VBox();
        weaponVBox.setSpacing(4);

        pane.getContentPane().getChildren().add(weaponVBox);

        var image = new ImageView(unselectedImage);
        var weaponSelect = new ComboBox<WeaponRef>();
        var starsSelect = new ComboBox<Integer>();
        weaponVBox.getChildren().addAll(
            image,
            new FusionW(weaponSelect),
            new FusionW(starsSelect),
            line,
            new VPadding(0)
        );

        weaponProperty.addListener((ob, old, now) -> {
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
            for (var w : weaponSelect.getItems()) {
                if (w.id == now.id) {
                    if (w != now) {
                        weaponSelect.setValue(w);
                        starsSelect.setValue(now.getStars());
                        weaponProperty.set(w);
                    }
                    break;
                }
            }
        });
        weaponSelect.setConverter(new StringConverter<>() {
            @Override
            public String toString(WeaponRef object) {
                if (object == null) return "";
                return object.name;
            }

            @Override
            public WeaponRef fromString(String string) {
                throw new UnsupportedOperationException();
            }
        });
        weaponSelect.setEditable(false);
        weaponSelect.setItems(FXCollections.observableList(WeaponRef.all()));
        weaponSelect.getItems().forEach(e -> e.starsSupplier = starsSelect::getValue);
        weaponSelect.setOnAction(e -> {
            var selected = weaponSelect.getValue();
            if (selected == null) return;
            weaponProperty.set(selected);
        });
        weaponSelect.setPrefWidth(WIDTH_HEIGHT);
        image.setFitWidth(WIDTH_HEIGHT);
        image.setFitHeight(WIDTH_HEIGHT);
        starsSelect.setEditable(false);
        starsSelect.setItems(FXCollections.observableList(Arrays.asList(0, 1, 2, 3, 4, 5, 6)));
        starsSelect.getSelectionModel().select(6);
        starsSelect.setPrefWidth(WIDTH_HEIGHT);

        line.setStroke(Theme.current().progressBarProgressColor());
        if (thisIndex != 0) {
            line.setVisible(false);
        }

        weaponVBox.setOnMouseClicked(e -> {
            var currentScene = matrixSceneGroup.getCurrentMainScene();
            //noinspection SuspiciousMethodCalls
            var currentIndex = scenes.indexOf(currentScene);
            if (currentIndex == thisIndex)
                return;
            if (currentIndex < thisIndex) {
                matrixSceneGroup.show(scenes.get(thisIndex), VSceneShowMethod.FROM_RIGHT);
            } else {
                matrixSceneGroup.show(scenes.get(thisIndex), VSceneShowMethod.FROM_LEFT);
            }
            this.setChosen(true);
            for (var w : choosers) {
                if (w != this) {
                    w.setChosen(false);
                }
            }
        });
    }

    public void setChosen(boolean chosen) {
        line.setVisible(chosen);
    }

    public Node getNode() {
        return pane.getNode();
    }
}
