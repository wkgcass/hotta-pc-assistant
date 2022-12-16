package net.cassite.hottapcassistant.test;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.cassite.hottapcassistant.discharge.DischargeCheckContext;
import net.cassite.hottapcassistant.util.Utils;

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
            "08-test-1.png",
            "09-test-2.png",
            "10-test-3.png",
        };

        var hbox = new HBox();
        var scene = new Scene(hbox);

        var totalWidth = 0.0;
        var maxHeight = 0.0;

        for (var imgName : images) {
            var img = new Image("/images/test/discharge/" + imgName, false);
            totalWidth += img.getWidth();
            if (maxHeight < img.getHeight()) {
                maxHeight = img.getHeight();
            }
            var imgView = new ImageView(img);
            var canvas = new Canvas();

            var label = new Label();
            label.setLayoutX(10);
            label.setLayoutY(10);
            label.setTextFill(Color.RED);

            var ctx = DischargeCheckContext.of(img, canvas);
            if (ctx != null) {
                canvas.setWidth(img.getWidth());
                canvas.setHeight(img.getHeight());
                var g = canvas.getGraphicsContext2D();
                g.setLineWidth(1.5);
                g.setStroke(Color.RED);
                g.beginPath();
                g.stroke();

                System.out.println("start from (" + ctx.getX() + ", " + ctx.getY() + ")");

                var point = runStep1(ctx, ctx.getX());
                var p = ctx.calculatePercentage(point[0], point[1]);
                System.out.println("current percentage of " + imgName + ": " + p);

                label.setText(Utils.roughFloatValueFormat.format(p * 100) + "%");
                hbox.getChildren().add(new Group(imgView, canvas, label));
            } else {
                label.setText("0.0%");
                hbox.getChildren().add(new Group(imgView, label));
            }
        }

        primaryStage.setScene(scene);
        primaryStage.setWidth(totalWidth);
        primaryStage.setHeight(maxHeight + 30);
        primaryStage.show();
    }

    private static final int falsePathLimit = 5;
    private static final int jumpMovingPathLimit = falsePathLimit + 1;
    private static final int extraEnsure = 2;

    private int[] runStep1(DischargeCheckContext ctx, int startX) {
        int lastX = ctx.getX();
        int lastY = ctx.getY();
        int state = 0; // 0 move right, 1 move down, 2 move left
        int downCount = 0;
        int leftCount = 0;
        while (true) {
            int moved;
            if ((moved = ctx.moveRightWithin(jumpMovingPathLimit, extraEnsure)) != -1) {
                lastX = ctx.getX();
                state = 0;
                downCount = 0;
                leftCount = 0;
                continue;
            }
            if ((moved = ctx.moveDownWithin(jumpMovingPathLimit - downCount, extraEnsure)) != -1) {
                downCount += moved;
                if (downCount > falsePathLimit) {
                    System.out.println("end step 1 enter step 2, too many down move: result = (" + lastX + ", " + lastY + ")");
                    return runStep2(ctx, startX);
                }

                lastY = ctx.getY();
                state = 1;
                leftCount = 0;
                continue;
            }
            if (state == 1 || state == 2) {
                if ((moved = ctx.moveLeftWithin(jumpMovingPathLimit - leftCount, extraEnsure)) != -1) {
                    leftCount += moved;
                    if (leftCount > falsePathLimit) {
                        System.out.println("finished at step 1, too many left move: result = (" + lastX + ", " + lastY + "), final point = (" + ctx.getX() + ", " + ctx.getY() + ")");
                        return new int[]{lastX, lastY};
                    }

                    state = 2;
                    continue;
                }
            }
            System.out.println("unable to go right, down or left, finished at step 1: result = (" + lastX + ", " + lastY + "), final point = (" + ctx.getX() + ", " + ctx.getY() + ")");
            return new int[]{lastX, lastY};
        }
    }

    private int[] runStep2(DischargeCheckContext ctx, int startX) {
        int lastX = ctx.getX();
        int lastY = ctx.getY();
        int state = 0; // 0 move down, 1 move left, 2 move up
        int leftCount = 0;
        int upCount = 0;
        while (true) {
            int moved;
            if ((moved = ctx.moveDownWithin(jumpMovingPathLimit, extraEnsure)) != -1) {
                lastY = ctx.getY();
                state = 0;
                leftCount = 0;
                upCount = 0;
                continue;
            }
            if ((moved = ctx.moveLeftWithin(jumpMovingPathLimit - leftCount, extraEnsure)) != -1) {
                leftCount += moved;
                if (leftCount > falsePathLimit) {
                    System.out.println("end step 2 enter step 3, too many left move: result = (" + lastX + ", " + lastY + "), final point = (" + ctx.getX() + ", " + ctx.getY() + ")");
                    return runStep3(ctx);
                }

                lastX = ctx.getX();
                state = 1;
                upCount = 0;
                continue;
            }
            if (state == 1 || state == 2) {
                if ((moved = ctx.moveUpWithin(jumpMovingPathLimit - upCount, extraEnsure)) != -1) {
                    upCount += moved;
                    if (upCount > falsePathLimit) {
                        System.out.println("end step 2 enter step 3, too many up move: result = (" + lastX + ", " + lastY + "), final point = (" + ctx.getX() + ", " + ctx.getY() + ")");
                        return runStep3(ctx);
                    }

                    state = 2;
                    continue;
                }
            }
            if (Math.abs(startX - ctx.getX()) < falsePathLimit) {
                System.out.println("end step 2 enter step 3, moved to bottom: result = (" + lastX + ", " + lastY + "), final point = (" + ctx.getX() + ", " + ctx.getY() + ")");
                return runStep3(ctx);
            }
            System.out.println("unable to go down, left or up, finished at step 2: result = (" + lastX + ", " + lastY + "), final point = (" + ctx.getX() + ", " + ctx.getY() + ")");
            return new int[]{lastX, lastY};
        }
    }

    private int[] runStep3(DischargeCheckContext ctx) {
        int lastX = ctx.getX();
        int lastY = ctx.getY();
        int state = 0; // 0 move left, 1 move up, 2 move right
        int upCount = 0;
        int rightCount = 0;
        while (true) {
            int moved;
            if ((moved = ctx.moveLeftWithin(jumpMovingPathLimit, extraEnsure)) != -1) {
                lastX = ctx.getX();
                state = 0;
                upCount = 0;
                rightCount = 0;
                continue;
            }
            if ((moved = ctx.moveUpWithin(jumpMovingPathLimit - upCount, extraEnsure)) != -1) {
                upCount += moved;
                if (upCount > falsePathLimit) {
                    System.out.println("end step 3 enter step 4, too many up move: result = (" + lastX + ", " + lastY + ")");
                    return runStep4(ctx);
                }

                lastY = ctx.getY();
                state = 1;
                rightCount = 0;
                continue;
            }
            if (state == 1 || state == 2) {
                if ((moved = ctx.moveRightWithin(jumpMovingPathLimit - rightCount, extraEnsure)) != -1) {
                    state = 2;
                    if (rightCount > falsePathLimit) {
                        System.out.println("finished at step 3, too many right move: result = (" + lastX + ", " + lastY + "), final point = (" + ctx.getX() + ", " + ctx.getY() + ")");
                        return new int[]{lastX, lastY};
                    }

                    rightCount += moved;
                    continue;
                }
            }
            System.out.println("unable to go left, up or right, finished at step 3: result = (" + lastX + ", " + lastY + "), final point = (" + ctx.getX() + ", " + ctx.getY() + ")");
            return new int[]{lastX, lastY};
        }
    }

    private int[] runStep4(DischargeCheckContext ctx) {
        int lastX = ctx.getX();
        int lastY = ctx.getY();
        int state = 0; // 0 move up, 1 move right, 2 move down
        int rightCount = 0;
        int downCount = 0;
        while (true) {
            int moved;
            if ((moved = ctx.moveUpWithin(jumpMovingPathLimit, extraEnsure)) != -1) {
                if (ctx.isCloseToInitial()) {
                    System.out.println("reaches initial point when moving up in step 4");
                    return ctx.getXY();
                }
                lastY = ctx.getY();
                state = 0;
                rightCount = 0;
                downCount = 0;
                continue;
            }
            if ((moved = ctx.moveRightWithin(jumpMovingPathLimit - rightCount, extraEnsure)) != -1) {
                if (ctx.isCloseToInitial()) {
                    System.out.println("reaches initial point when moving right in step 4");
                    return ctx.getXY();
                }
                rightCount += moved;
                if (rightCount > falsePathLimit) {
                    System.out.println("too many right move, finished at step 4: result = (" + lastX + ", " + lastY + "), final point = (" + ctx.getX() + ", " + ctx.getY() + ")");
                    return new int[]{lastX, lastY};
                }

                lastX = ctx.getX();
                state = 1;
                downCount = 0;
                continue;
            }
            if (state == 1 || state == 2) {
                if ((moved = ctx.moveDownWithin(jumpMovingPathLimit - downCount, extraEnsure)) != -1) {
                    downCount += moved;
                    if (downCount > falsePathLimit) {
                        System.out.println("too many up move, finished at step 4: result = (" + lastX + ", " + lastY + "), final point = (" + ctx.getX() + ", " + ctx.getY() + ")");
                        return new int[]{lastX, lastY};
                    }

                    state = 2;
                    continue;
                }
            }
            System.out.println("unable to go up, right or down, finished at step 4: result = (" + lastX + ", " + lastY + "), final point = (" + ctx.getX() + ", " + ctx.getY() + ")");
            return new int[]{lastX, lastY};
        }
    }
}
