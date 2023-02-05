package net.cassite.hottapcassistant.ui;

import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.scene.VScene;
import io.vproxy.vfx.ui.scene.VSceneRole;

public abstract class MainScene extends VScene {
    public final FusionButton menuButton = new FusionButton();

    public MainScene() {
        super(VSceneRole.MAIN);
    }

    public abstract String title();

    protected boolean hideMenuButton() {
        return false;
    }

    public void setVisible(boolean visible, VScene current) {
        if (this == current || visible || !hideMenuButton()) {
            menuButton.setManaged(true);
            menuButton.setVisible(true);
        } else {
            menuButton.setManaged(false);
            menuButton.setVisible(false);
        }
    }
}
