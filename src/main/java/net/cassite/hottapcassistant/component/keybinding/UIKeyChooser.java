package net.cassite.hottapcassistant.component.keybinding;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import javafx.application.Platform;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.stage.StageStyle;
import net.cassite.hottapcassistant.entity.Key;
import net.cassite.hottapcassistant.entity.KeyCode;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.FontManager;
import net.cassite.hottapcassistant.util.GlobalScreenUtils;

import java.util.Optional;

import static com.github.kwhat.jnativehook.keyboard.NativeKeyEvent.*;

public class UIKeyChooser extends Dialog<Key> {
    private final NativeKeyListener keyListener = new NativeKeyListener() {
        @Override
        public void nativeKeyPressed(NativeKeyEvent e) {
            Key key;
            if (e.getKeyCode() == VC_CONTROL || e.getKeyCode() == VC_ALT || e.getKeyCode() == VC_SHIFT) {
                boolean isLeft;
                if (e.getKeyLocation() == KEY_LOCATION_LEFT) {
                    isLeft = true;
                } else if (e.getKeyLocation() == KEY_LOCATION_RIGHT) {
                    isLeft = false;
                } else {
                    return; // should not happen, but if happens, we ignore this event
                }
                key = new Key(KeyCode.valueOf(e.getKeyCode()), isLeft);
            } else {
                if (e.getKeyLocation() == KEY_LOCATION_NUMPAD) {
                    return; // ignore numpad
                }
                key = new Key(KeyCode.valueOf(e.getKeyCode()));
            }
            Platform.runLater(() -> {
                UIKeyChooser.this.setResult(key);
                UIKeyChooser.this.close();
            });
        }
    };

    public UIKeyChooser() {
        this(true);
    }

    public UIKeyChooser(boolean withMouse) {
        initStyle(StageStyle.UTILITY);
        var useLeftMouseButtonType = new ButtonType(I18n.get().leftMouseButton(), ButtonBar.ButtonData.OK_DONE);
        var useRightMouseButtonType = new ButtonType(I18n.get().rightMouseButton(), ButtonBar.ButtonData.OK_DONE);
        if (withMouse) {
            getDialogPane().getButtonTypes().addAll(useLeftMouseButtonType, useRightMouseButtonType, ButtonType.CANCEL);
        } else {
            getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        }

        var desc = new Label(withMouse ? I18n.get().keyChooserDesc() : I18n.get().keyChooserDescWithoutMouse()) {{
            FontManager.setFont(this);
        }};

        getDialogPane().setContent(desc);

        setResultConverter(t -> {
            if (t == useLeftMouseButtonType) {
                return new Key(MouseButton.PRIMARY);
            } else if (t == useRightMouseButtonType) {
                return new Key(MouseButton.SECONDARY);
            }
            return null;
        });
    }

    public Optional<Key> choose() {
        GlobalScreenUtils.enable(this);
        GlobalScreen.addNativeKeyListener(keyListener);
        var ret = showAndWait();
        GlobalScreen.removeNativeKeyListener(keyListener);
        GlobalScreenUtils.disable(this);
        return ret;
    }
}
