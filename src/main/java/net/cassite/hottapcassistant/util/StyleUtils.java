package net.cassite.hottapcassistant.util;

import javafx.scene.Node;

public class StyleUtils {
    private StyleUtils() {
    }

    public static void setNoFocusBlur(Node node) {
        node.setStyle("-fx-focus-color: transparent;");
    }
}
