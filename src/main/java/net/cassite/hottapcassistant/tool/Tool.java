package net.cassite.hottapcassistant.tool;

import io.vproxy.vfx.ui.scene.VScene;
import javafx.scene.image.Image;

public interface Tool {
    String getName();

    Image getIcon();

    void launch();

    boolean isRunning();

    VScene getScene();

    void setOnTerminated(Runnable f);

    void terminate();
}
