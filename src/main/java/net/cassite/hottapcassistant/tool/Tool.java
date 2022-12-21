package net.cassite.hottapcassistant.tool;

import javafx.scene.image.Image;

public interface Tool {
    String getName();

    Image getIcon();

    void launch();

    boolean isRunning();

    void alert();

    void terminate();
}
