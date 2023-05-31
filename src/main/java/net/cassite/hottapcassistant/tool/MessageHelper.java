package net.cassite.hottapcassistant.tool;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import io.vproxy.vfx.control.drag.DragHandler;
import io.vproxy.vfx.control.globalscreen.GlobalScreenUtils;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.manager.image.ImageManager;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.scene.VScene;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.cassite.hottapcassistant.i18n.I18n;

import java.util.LinkedList;
import java.util.ListIterator;

public class MessageHelper extends AbstractTool implements Tool {
    private static final Background background = new Background(new BackgroundFill(new Color(21 / 255d, 138 / 255d, 247 / 255d, 1), CornerRadii.EMPTY, Insets.EMPTY));
    private static final Background blinkBackground = new Background(new BackgroundFill(new Color(234 / 255d, 117 / 255d, 8 / 255d, 1), CornerRadii.EMPTY, Insets.EMPTY));

    private S stage;

    @Override
    protected String buildName() {
        return I18n.get().toolName("message-helper");
    }

    @Override
    protected Image buildIcon() {
        return ImageManager.get().load("/images/icon/message-helper-icon.png");
    }

    @Override
    protected VScene buildScene() {
        stage = new S();
        stage.setTitle("message-helper");
        stage.show();

        return new SS();
    }

    @Override
    protected void terminate0() {
        var stage = this.stage;
        this.stage = null;
        if (stage != null) {
            GlobalScreen.removeNativeKeyListener(stage);
            GlobalScreenUtils.disable(stage);
            stage.close();
        }
    }

    private class SS extends ToolScene {
        public SS() {
            enableAutoContentWidthHeight();

            var descLabel = new ThemeLabel(I18n.get().messageHelperDesc());

            var redBtn = new FuncButton(I18n.get().messageHelperColorButton("red"), "<red>", new Color(0xe0 / 255d, 0x67 / 255d, 0x64 / 255d, 1));
            var blueBtn = new FuncButton(I18n.get().messageHelperColorButton("blue"), "<blue>", new Color(0x42 / 255d, 0x7c / 255d, 0xb0 / 255d, 1));
            var whiteBtn = new FuncButton(I18n.get().messageHelperColorButton("white"), "<hot>", null);
            var goldBtn = new FuncButton(I18n.get().messageHelperColorButton("gold"), "<ItemQualityLegendary>", new Color(0xbe / 255d, 0x93 / 255d, 0x52 / 255d, 1));
            var purpleBtn = new FuncButton(I18n.get().messageHelperColorButton("purple"), "<ItemQualityEpic>", new Color(0x87 / 255d, 0x5e / 255d, 0xab / 255d, 1));
            var greenBtn = new FuncButton(I18n.get().messageHelperColorButton("green"), "<ItemQualityCommon>", new Color(0x53 / 255d, 0xb6 / 255d, 0x67 / 255d, 1));
            var redBigBtn = new FuncButton(I18n.get().messageHelperColorButton("red_big"), "<red_lbl_16>", new Color(0xe0 / 255d, 0x67 / 255d, 0x64 / 255d, 1)) {{
                var tn = getTextNode();
                FontManager.get().setFont(tn, s -> s.setSize(16));
            }};
            var goldBigBtn = new FuncButton(I18n.get().messageHelperColorButton("gold_big"), "<yellow_lbl_16>", new Color(0xbe / 255d, 0x93 / 255d, 0x52 / 255d, 1)) {{
                var tn = getTextNode();
                FontManager.get().setFont(tn, s -> s.setSize(16));
            }};

            var pane = new VBox(descLabel,
                new HBox(redBtn, blueBtn, whiteBtn) {{
                    setSpacing(15);
                }},
                new HBox(goldBtn, purpleBtn, greenBtn) {{
                    setSpacing(15);
                }},
                new HBox(redBigBtn, goldBigBtn) {{
                    setSpacing(15);
                }});
            pane.setAlignment(Pos.CENTER);
            pane.setPrefWidth(650);
            pane.setSpacing(15);
            FXUtils.observeWidthHeightCenter(getContentPane(), pane);
            getContentPane().getChildren().add(pane);
        }
    }

    private class FuncButton extends FusionButton {
        private FuncButton(String name, String prefix, Color color) {
            this(name, prefix, "</</>>", color);
        }

        private FuncButton(String name, String prefix, String suffix, Color color) {
            super(name);
            if (color != null) {
                getTextNode().setTextFill(color);
            }

            setPrefWidth(150);
            setPrefHeight(50);

            setOnAction(e -> {
                var stage = MessageHelper.this.stage;
                if (stage != null) {
                    stage.insert(prefix, "*", suffix);
                }
            });
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
                FontManager.get().setFont(this, settings -> settings.setSize(24));
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

        public void insert(String prefix, String content, String suffix) {
            var pos = input.caretPositionProperty().get();
            var text = input.getText();
            if (pos < 0 || pos >= text.length()) {
                text += prefix + content + suffix;
                input.setText(text);
                input.positionCaret(text.length() - content.length() - suffix.length());
            } else {
                text = text.substring(0, pos) + prefix + content + suffix + text.substring(pos);
                input.setText(text);
                input.positionCaret(pos + prefix.length());
            }

            setAlwaysOnTop(true);
            FXUtils.runDelay(500, () -> setAlwaysOnTop(false));
            input.requestFocus();
        }
    }
}
