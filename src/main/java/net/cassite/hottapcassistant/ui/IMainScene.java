package net.cassite.hottapcassistant.ui;

import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.scene.VScene;

public interface IMainScene {
    String title();

    void setVisible(boolean visible, VScene current);

    VScene getScene();

    FusionButton getMenuButton();
}
