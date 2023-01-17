package net.cassite.hottapcassistant.tool;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import io.vproxy.vfx.control.drag.DragHandler;
import io.vproxy.vfx.control.globalscreen.GlobalScreenUtils;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.manager.image.ImageManager;
import io.vproxy.vfx.util.FXUtils;
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

import java.util.LinkedList;
import java.util.ListIterator;

public class MessageHelper extends AbstractTool implements Tool {
    private static final Background background = new Background(new BackgroundFill(new Color(21 / 255d, 138 / 255d, 247 / 255d, 1), CornerRadii.EMPTY, Insets.EMPTY));
    private static final Background blinkBackground = new Background(new BackgroundFill(new Color(234 / 255d, 117 / 255d, 8 / 255d, 1), CornerRadii.EMPTY, Insets.EMPTY));

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
        private final Pane pane;
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
                FontManager.get().setFont(this, 24);
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

            pane = new Pane();
            pane.setBackground(background);
            var scene = new Scene(pane);
            setScene(scene);

            pane.getChildren().add(input);

            var dragHandler = new DragHandler() {
                @Override
                protected void set(double x, double y) {
                    setX(x);
                    setY(y);
                }

                @Override
                protected double[] get() {
                    return new double[]{getX(), getY()};
                }
            };
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

        private boolean isBlinking = false;

        @Override
        public void nativeKeyReleased(NativeKeyEvent e) {
            if (e.getKeyCode() == NativeKeyEvent.VC_ENTER) {
                if (input.isFocused()) {
                    return;
                }
                FXUtils.runOnFX(() -> {
                    blink();
                    setAlwaysOnTop(true);
                    FXUtils.runDelay(500, () -> setAlwaysOnTop(false));
                });
            }
        }

        private void blink() {
            if (isBlinking) {
                return;
            }
            isBlinking = true;
            FXUtils.runDelay(100, () -> {
                pane.setBackground(blinkBackground);
                FXUtils.runDelay(100, () -> {
                    pane.setBackground(background);
                    FXUtils.runDelay(100, () -> {
                        pane.setBackground(blinkBackground);
                        FXUtils.runDelay(100, () -> {
                            pane.setBackground(background);
                            FXUtils.runDelay(100, () -> {
                                pane.setBackground(blinkBackground);
                                FXUtils.runDelay(100, () -> {
                                    pane.setBackground(background);
                                    isBlinking = false;
                                });
                            });
                        });
                    });
                });
            });
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
