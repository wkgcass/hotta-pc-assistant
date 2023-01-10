package net.cassite.hottapcassistant.test;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.cassite.hottapcassistant.util.JNAScreenShot;

import java.awt.*;

public class NativeCaptureTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        var awtImg = JNAScreenShot.getScreenshot(new Rectangle(100, 200, 300, 400));
        if (awtImg == null) {
            throw new Exception("capturing failed");
        }
        var fxImg = SwingFXUtils.toFXImage(awtImg, null);

        var pane = new Pane();
        var scene = new Scene(pane);
        primaryStage.setScene(scene);
        pane.getChildren().add(new ImageView(fxImg));

        primaryStage.show();
    }
}
