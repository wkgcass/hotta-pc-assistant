package net.cassite.hottapcassistant.component;

import javafx.geometry.Insets;
import javafx.scene.layout.Pane;

public class HPadding extends Pane {
    public HPadding(int padding) {
        setVisible(false);
        setWidth(0);
        setPrefWidth(0);
        setMaxWidth(0);
        setPadding(new Insets(0, 0, 0, padding));
    }
}
