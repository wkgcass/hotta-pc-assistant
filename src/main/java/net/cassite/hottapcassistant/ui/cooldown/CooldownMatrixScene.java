package net.cassite.hottapcassistant.ui.cooldown;

import io.vproxy.vfx.ui.scene.VScene;
import io.vproxy.vfx.ui.scene.VSceneRole;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.HBox;
import net.cassite.hottapcassistant.entity.MatrixRef;

public class CooldownMatrixScene extends VScene {
    public static final int SPACING = 20;

    public CooldownMatrixScene(SimpleObjectProperty<MatrixRef>[] matrixProperties) {
        super(VSceneRole.MAIN);

        var hbox = new HBox();
        hbox.setSpacing(SPACING);
        for (var matrixProperty : matrixProperties) {
            hbox.getChildren().add(new CooldownMatrixChooser(matrixProperty).getNode());
        }

        getContentPane().getChildren().add(hbox);
    }
}
