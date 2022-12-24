package net.cassite.hottapcassistant.util;

import javafx.scene.control.Labeled;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

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

    public static void setFont(Text text, int size) {
        text.setFont(Font.font("Smiley Sans Oblique", size));
    }

    public static void setNoto(Labeled labeled) {
        labeled.setFont(Font.font("Noto Sans Regular", 16));
    }

    public static void setNoto(TextInputControl input) {
        input.setFont(Font.font("Noto Sans Regular", 16));
    }
}
