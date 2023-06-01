package net.cassite.hottapcassistant.tool;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import io.vproxy.vfx.control.drag.DragHandler;
import io.vproxy.vfx.control.globalscreen.GlobalScreenUtils;
import io.vproxy.vfx.control.scroll.ScrollDirection;
import io.vproxy.vfx.control.scroll.VScrollPane;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.manager.font.FontSettings;
import io.vproxy.vfx.manager.image.ImageManager;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.scene.VScene;
import io.vproxy.vfx.ui.stage.VStage;
import io.vproxy.vfx.ui.stage.VStageInitParams;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import net.cassite.hottapcassistant.i18n.I18n;

import java.util.LinkedList;
import java.util.ListIterator;

public class MessageHelper extends AbstractTool implements Tool {
    private static final Background background = new Background(new BackgroundFill(Theme.current().sceneBackgroundColor(), CornerRadii.EMPTY, Insets.EMPTY));
    private static final Background blinkBackground = new Background(new BackgroundFill(Theme.current().subSceneBackgroundColor(), CornerRadii.EMPTY, Insets.EMPTY));

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

    private static class SS extends ToolScene {
        public SS() {
            enableAutoContentWidthHeight();

            var descLabel = new ThemeLabel(I18n.get().messageHelperDesc());
            FXUtils.observeWidthHeightCenter(getContentPane(), descLabel);
            getContentPane().getChildren().add(descLabel);
        }
    }

    private static class S extends VStage implements NativeKeyListener {
        private static double lastX;
        private static double lastY;
        private static final LinkedList<String> history = new LinkedList<>();
        private final Pane pane;
        private final TextField input;
        private String lastInputForCtrlZ;
        private ListIterator<String> ite = null;

        S() {
            super(new VStageInitParams()
                .setResizable(false)
                .setCloseButton(false)
                .setMaximizeAndResetButton(false)
                .setIconifyButton(false));
            getStage().setWidth(844);
            getStage().setHeight(112);
            if (lastX != 0 && lastY != 0) {
                getStage().setX(lastX);
                getStage().setY(lastY);
            } else {
                getStage().centerOnScreen();
            }

            var textNumCount = new Label("0") {{
                setPrefWidth(35);
                setPrefHeight(30);
                setLayoutX(0);
                setLayoutY(30);
                FontManager.get().setFont(this, s -> s
                    .setSize(12)
                    .setFamily(FontManager.FONT_NAME_JetBrainsMono));
                setTextFill(Theme.current().normalTextColor());
                setAlignment(Pos.BOTTOM_RIGHT);
            }};

            input = new TextField() {{
                FontManager.get().setFont(this, settings -> settings.setSize(24).setFamily(FontManager.FONT_NAME_JetBrainsMono));
                setPrefWidth(798);
                setPrefHeight(56);
                FXUtils.disableFocusColor(this);
            }};
            input.setLayoutX(40);
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
                lastInputForCtrlZ = null;
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
                } else if (e.getCode() == KeyCode.Z && (e.isControlDown() || e.isMetaDown())) {
                    if (lastInputForCtrlZ != null) {
                        input.setText(lastInputForCtrlZ);
                    }
                    e.consume(); // disable built-in Ctrl-Z
                } else if (e.getCode() == KeyCode.Y && (e.isControlDown() || e.isMetaDown())) {
                    e.consume(); // disable Ctrl-Y
                }
            });
            input.textProperty().addListener((ob, old, now) -> {
                if (now == null) return;
                //noinspection ConcatenationWithEmptyString
                textNumCount.setText("" + now.length());
                if (old == null) return;
                lastInputForCtrlZ = old;
            });

            var buttons = new VScrollPane(ScrollDirection.HORIZONTAL);
            buttons.getNode().setLayoutX(20);
            buttons.getNode().setLayoutY(64);
            buttons.getNode().setPrefHeight(40);
            buttons.getNode().setPrefWidth(824);

            {
                var redBtn = new FuncButton(I18n.get().messageHelperColorButton("red"), "<red>", new Color(0xe0 / 255d, 0x67 / 255d, 0x64 / 255d, 1));
                var blueBtn = new FuncButton(I18n.get().messageHelperColorButton("blue"), "<blue>", new Color(0x42 / 255d, 0x7c / 255d, 0xb0 / 255d, 1));
                var whiteBtn = new FuncButton(I18n.get().messageHelperColorButton("white"), "<hot>", null);
                var goldBtn = new FuncButton(I18n.get().messageHelperColorButton("gold"), "<NameInMain>", new Color(0xbe / 255d, 0x93 / 255d, 0x52 / 255d, 1));
                var purpleBtn = new FuncButton(I18n.get().messageHelperColorButton("purple"), "<GuildNotice>", new Color(0x87 / 255d, 0x5e / 255d, 0xab / 255d, 1));
                var greenBtn = new FuncButton(I18n.get().messageHelperColorButton("green"), "<Item>", new Color(0x53 / 255d, 0xb6 / 255d, 0x67 / 255d, 1));
                var greenBoldBtn = new FuncButton(I18n.get().messageHelperColorButton("green_bold"), "<at>", new Color(0x53 / 255d, 0xb6 / 255d, 0x67 / 255d, 1));
                var itemBtn = new FuncButton(I18n.get().messageHelperItemButton(), "<hot param=\"2&", "&&&\">?</</>>", null);

                buttons.setContent(new HBox(
                    redBtn,
                    blueBtn,
                    greenBoldBtn,
                    greenBtn,
                    whiteBtn,
                    goldBtn,
                    purpleBtn,
                    itemBtn
                ) {{
                    setSpacing(10);
                }});
            }

            pane = getRoot().getContentPane();
            pane.setBackground(background);

            pane.getChildren().addAll(textNumCount, input, buttons.getNode());

            var dragHandler = new DragHandler() {
                @Override
                protected void set(double x, double y) {
                    getStage().setX(x);
                    getStage().setY(y);
                }

                @Override
                protected double[] get() {
                    return new double[]{getStage().getX(), getStage().getY()};
                }
            };
            pane.setOnMousePressed(dragHandler);
            pane.setOnMouseDragged(dragHandler);
            getStage().xProperty().addListener((ob, old, now) -> {
                if (now == null) return;
                lastX = now.doubleValue();
            });
            getStage().yProperty().addListener((ob, old, now) -> {
                if (now == null) return;
                lastY = now.doubleValue();
            });

            GlobalScreenUtils.enable(this);
            GlobalScreen.addNativeKeyListener(this);
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

                setDisableAnimation(true);
                setPrefWidth(100);
                setPrefHeight(35);
                FontManager.get().setFont(getTextNode(), this::setFontSettings);

                setOnAction(e -> {
                    var stage = S.this;
                    stage.insert(prefix, suffix);
                });
            }

            protected FontSettings setFontSettings(FontSettings s) {
                return s.setSize(16);
            }
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
                    getStage().setAlwaysOnTop(true);
                    FXUtils.runDelay(500, () -> getStage().setAlwaysOnTop(false));
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

        public void insert(String prefix, String suffix) {
            var selection = input.getSelection();
            if (selection == null || selection.getLength() == 0) {
                var pos = input.caretPositionProperty().get();
                var text = input.getText();
                if (pos < 0 || pos >= text.length()) {
                    text += prefix + suffix;
                    input.setText(text);
                    input.positionCaret(text.length() - suffix.length());
                } else {
                    text = text.substring(0, pos) + prefix + suffix + text.substring(pos);
                    input.setText(text);
                    input.positionCaret(pos + prefix.length());
                }
            } else {
                var start = selection.getStart();
                var end = selection.getEnd();
                var text = input.getText();
                text = text.substring(0, start) + prefix + text.substring(start, end) + suffix + text.substring(end);
                input.setText(text);
                input.selectRange(start + prefix.length(), end + prefix.length());
            }
            input.requestFocus();
        }
    }
}
