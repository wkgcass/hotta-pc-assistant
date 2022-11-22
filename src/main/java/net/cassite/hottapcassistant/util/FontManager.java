package net.cassite.hottapcassistant.util;

import javafx.scene.control.Labeled;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Font;

public class FontManager {
    private FontManager() {
    }

    public static void setFont(Labeled labeled, int size) {
        labeled.setFont(Font.font("Smiley Sans Oblique", size));
    }

    public static void setFont(Labeled labeled) {
        labeled.setFont(Font.font("Smiley Sans Oblique", 16));
    }

    public static void setFont(TextInputControl input, int size) {
        input.setFont(Font.font("Smiley Sans Oblique", size));
    }

    public static void setFont(TextInputControl input) {
        input.setFont(Font.font("Smiley Sans Oblique", 16));
    }
}
