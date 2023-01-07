package net.cassite.hottapcassistant.tool;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.*;

import java.util.LinkedList;
import java.util.ListIterator;

public class MessageHelper extends AbstractTool implements Tool {
    @Override
    protected String buildName() {
        return I18n.get().toolName("message-helper");
    }

    @Override
    protected Image buildIcon() {
        return ImageManager.get().load("/images/icon/message-helper-icon.png");
    }

    @Override
    protected Stage buildStage() {
        return new S();
    }

    @Override
    public void alert() {
        terminate();
    }

    @Override
    protected void terminate0() {
        var stage = (S) this.stage;
        if (stage != null) {
            GlobalScreen.removeNativeKeyListener(stage);
            GlobalScreenUtils.disable(stage);
        }
    }

    private static class S extends Stage implements NativeKeyListener {
        private static double lastX;
        private static double lastY;
        private static final LinkedList<String> history = new LinkedList<>();
        private final TextField input;
        private ListIterator<String> ite = null;

        S() {
            initStyle(StageStyle.TRANSPARENT);
            setWidth(1024);
            setHeight(64);
            if (lastX != 0 && lastY != 0) {
                setX(lastX);
                setY(lastY);
            } else {
                centerOnScreen();
            }
            input = new TextField() {{
                FontManager.setFont(this, 24);
                setPrefWidth(1000);
                setPrefHeight(56);
            }};
            input.setLayoutX(20);
            input.setLayoutY(4);
            input.setOnAction(e -> {
                var s = input.getText().trim();
                if (!s.isEmpty()) {
                    addHistory(s);
                    var content = new ClipboardContent();
                    content.putString(s);
                    Clipboard.getSystemClipboard().setContent(content);
                }
                input.setText("");
                ite = null;
            });
            input.setOnKeyReleased(e -> {
                if (e.getCode() == KeyCode.ESCAPE) {
                    input.setText("");
                    ite = null;
                } else if (e.getCode() == KeyCode.UP) {
                    if (ite == null) {
                        ite = history.listIterator();
                    }
                    if (ite.hasNext()) {
                        var s = ite.next();
                        input.setText(s);
                        input.positionCaret(s.length());
                    }
                } else if (e.getCode() == KeyCode.DOWN) {
                    if (ite == null) {
                        return;
                    }
                    if (ite.hasPrevious()) {
                        var s = ite.previous();
                        input.setText(s);
                        input.positionCaret(s.length());
                    }
                }
            });

            var pane = new Pane();
            pane.setBackground(new Background(new BackgroundFill(new Color(21 / 255d, 138 / 255d, 247 / 255d, 1), CornerRadii.EMPTY, Insets.EMPTY)));
            var scene = new Scene(pane);
            setScene(scene);

            pane.getChildren().add(input);

            var dragHandler = new DragHandler(xy -> {
                setX(xy[0]);
                setY(xy[1]);
            }, () -> new double[]{getX(), getY()});
            pane.setOnMousePressed(dragHandler);
            pane.setOnMouseDragged(dragHandler);
            xProperty().addListener((ob, old, now) -> {
                if (now == null) return;
                lastX = now.doubleValue();
            });
            yProperty().addListener((ob, old, now) -> {
                if (now == null) return;
                lastY = now.doubleValue();
            });

            GlobalScreenUtils.enable(this);
            GlobalScreen.addNativeKeyListener(this);
        }

        @Override
        public void nativeKeyReleased(NativeKeyEvent e) {
            if (e.getKeyCode() == NativeKeyEvent.VC_ENTER) {
                Utils.runOnFX(() -> {
                    setAlwaysOnTop(true);
                    Utils.runDelay(500, () -> setAlwaysOnTop(false));
                });
            }
        }

        private void addHistory(String s) {
            history.remove(s);
            if (history.size() >= 20) {
                history.removeLast();
            }
            history.addFirst(s);
        }
    }
}
