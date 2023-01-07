package net.cassite.hottapcassistant.test;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.cassite.hottapcassistant.discharge.DischargeCheckContext;
import net.cassite.hottapcassistant.discharge.SimpleDischargeCheckAlgorithm;
import net.cassite.hottapcassistant.util.Utils;

import java.awt.*;

@SuppressWarnings("DuplicatedCode")
public class DischargeTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        var images = new String[]{
            "00-none.jpg",
            "01-little.jpg",
            "02-quarter.jpg",
            "03-half.jpg",
            "04-more-than-half.jpg",
            "05-3-quarter.jpg",
            "06-much.jpg",
            "07-almost-full.jpg",
        };

        var gridPane = new GridPane();
        var scene = new Scene(gridPane);

        for (int i = 0; i < images.length; i++) {
            String imgName = images[i];
            var img = new Image("/images/test/discharge/" + imgName, false);

            var label = new Label();
            label.setLayoutX(10);
            label.setLayoutY(10);
            label.setTextFill(Color.RED);

            var bImg = SwingFXUtils.fromFXImage(img, null);
            var g = bImg.createGraphics();
            g.setStroke(new BasicStroke());
            g.setPaint(new java.awt.Color(255, 0, 0));
            var ctx = DischargeCheckContext.of(bImg, g);
            if (ctx != null) {
                System.out.println("start from (" + ctx.getX() + ", " + ctx.getY() + ")");

                var algo = new SimpleDischargeCheckAlgorithm(
                    new SimpleDischargeCheckAlgorithm.Args(),
                    System.out::println);
                algo.init(ctx);
                var result = algo.check();
                System.out.println("current percentage of " + imgName + ": " + result.p() + "~" + result.pMax());

                label.setText(Utils.roughFloatValueFormat.format(result.p() * 100) + "%");
                bImg.flush();
                gridPane.add(new Group(new ImageView(SwingFXUtils.toFXImage(bImg, null)), label), i % 5, i / 5);
            } else {
                label.setText("0.0%");
                gridPane.add(new Group(new ImageView(img), label), i % 5, i / 5);
            }
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
