package net.cassite.hottapcassistant.tool;

import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.scene.VScene;
import io.vproxy.vfx.ui.scene.VSceneRole;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;

public class ToolScene extends VScene {
    public ToolScene() {
        super(VSceneRole.TEMPORARY);
        getNode().setBackground(new Background(new BackgroundFill(
            Theme.current().sceneBackgroundColor(),
            CornerRadii.EMPTY,
            Insets.EMPTY
        )));
    }
}
