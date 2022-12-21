package net.cassite.hottapcassistant.ui;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.cassite.hottapcassistant.component.HPadding;
import net.cassite.hottapcassistant.component.VPadding;
import net.cassite.hottapcassistant.component.macro.UIMacroList;
import net.cassite.hottapcassistant.config.AssistantConfig;
import net.cassite.hottapcassistant.config.InputConfig;
import net.cassite.hottapcassistant.entity.*;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.*;
import org.controlsfx.control.ToggleSwitch;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.*;

public class MacroPane extends BorderPane implements NativeKeyListener, NativeMouseListener, EnterCheck, Terminate {
    private final ToggleSwitch switchButton = new ToggleSwitch();
    private final ToggleSwitch rememberMousePositionSwitchButton = new ToggleSwitch();
    private final UIMacroList ls = new UIMacroList(this::flushConfig);

    private final Set<KeyCode> keys = new HashSet<>();
    private final Set<MouseButton> buttons = new HashSet<>();

    private AssistantMacroData releaseMouseKeyBinding;
    private AssistantMacro macro;

    public MacroPane() {
        var topVBox = new VBox();
        var switchHBox = new HBox();
        var switchBtnLabel = new Label(I18n.get().macroSwitchButtonLabel()) {{
            FontManager.setFont(this);
        }};
        switchHBox.getChildren().addAll(switchBtnLabel, new HPadding(5), switchButton);
        switchHBox.setPadding(new Insets(10, 0, 10, 0));
        var macroAlertLabel = new Label(I18n.get().macroAlertLabel()) {{
            FontManager.setFont(this);
        }};
        var knowConsequenceCheckBox = new CheckBox(I18n.get().knowConsequencePrompt()) {{
            FontManager.setFont(this);
        }};
        var rememberHBox = new HBox();
        var rememberBtnLabel = new Label(I18n.get().rememberMousePositionButtonLabel()) {{
            FontManager.setFont(this);
        }};
        rememberHBox.getChildren().addAll(rememberBtnLabel, new HPadding(5), rememberMousePositionSwitchButton);
        topVBox.getChildren().addAll(
            switchHBox,
            new VPadding(2),
            macroAlertLabel,
            new VPadding(5),
            knowConsequenceCheckBox,
            new Separator() {{
                setPadding(new Insets(10, 0, 10, 0));
            }},
            rememberHBox,
            new VPadding(5));
        setTop(topVBox);

        boolean[] consequenceIsCheckedOnSelect = new boolean[]{false};
        switchButton.selectedProperty().addListener((ob, old, now) -> {
            if (Objects.equals(old, now)) return;
            if (switchButton.isSelected()) {
                if (!knowConsequenceCheckBox.isSelected()) {
                    consequenceIsCheckedOnSelect[0] = false;
                    switchButton.setSelected(false);
                    return;
                }
                consequenceIsCheckedOnSelect[0] = true;
                GlobalScreenUtils.enable(this);
                GlobalScreen.addNativeKeyListener(this);
                GlobalScreen.addNativeMouseListener(this);
            } else {
                if (!consequenceIsCheckedOnSelect[0]) {
                    return;
                }
                GlobalScreen.removeNativeKeyListener(this);
                GlobalScreen.removeNativeMouseListener(this);
                keys.clear();
                buttons.clear();
                GlobalScreenUtils.disable(this);
                mouseIsReleased = false;
            }
        });
        rememberMousePositionSwitchButton.selectedProperty().addListener((ob, old, now) -> {
            if (now == null) return;
            if (Objects.equals(old, now)) return;
            if (!rememberMousePositionSwitchButton.isSelected()) {
                mouseIsReleased = false;
            }
            macro.rememberMousePosition = now;
            flushConfig();
        });

        StyleUtils.setNoFocusBlur(ls);
        setCenter(ls);

        var reloadMacro = new Button(I18n.get().reloadMacro()) {{
            FontManager.setFont(this);
        }};
        reloadMacro.setOnAction(e -> {
            switchButton.setSelected(false);
            reloadMacro();
        });
        reloadMacro.setPrefWidth(120);
        var editMacro = new Button(I18n.get().editMacro()) {{
            FontManager.setFont(this);
        }};
        editMacro.setOnAction(e -> {
            var file = AssistantConfig.assistantFilePath.toFile();
            if (!file.exists()) {
                try {
                    //noinspection ResultOfMethodCallIgnored
                    file.createNewFile();
                } catch (IOException ignore) {
                }
            }
            try {
                Desktop.getDesktop().open(file);
            } catch (Throwable ignore) {
                new SimpleAlert(Alert.AlertType.ERROR, I18n.get().openFileFailed()).show();
            }
        });
        editMacro.setPrefWidth(120);
        var bottomButtons = new HBox();
        bottomButtons.setAlignment(Pos.CENTER_RIGHT);
        bottomButtons.getChildren().addAll(reloadMacro, new HPadding(4), editMacro);
        bottomButtons.setPadding(new Insets(10, 5, 2, 0));
        setBottom(bottomButtons);

        setRight(new HPadding(4));
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (!switchButton.isSelected()) {
            return;
        }
        var code = KeyCode.valueOf(e.getKeyCode());
        if (code == null) return;
        keys.add(code);
        rememberMousePositionBegin(code, null);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        if (!switchButton.isSelected()) {
            return;
        }
        var code = KeyCode.valueOf(e.getKeyCode());
        if (code == null) return;
        if (keys.contains(code)) {
            var m = filterOneMacroToRun(code, null);
            keys.remove(code);
            runMacro(m);
        }
        rememberMousePositionEnd(code, null);
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent e) {
        if (!switchButton.isSelected()) {
            return;
        }
        MouseButton btn = null;
        if (e.getButton() == NativeMouseEvent.BUTTON1) {
            btn = MouseButton.PRIMARY;
        } else if (e.getButton() == NativeMouseEvent.BUTTON2) {
            btn = MouseButton.SECONDARY;
        }
        if (btn == null) {
            return;
        }
        buttons.add(btn);
        rememberMousePositionBegin(null, btn);
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {
        if (!switchButton.isSelected()) {
            return;
        }
        MouseButton btn = null;
        if (e.getButton() == NativeMouseEvent.BUTTON1) {
            btn = MouseButton.PRIMARY;
        } else if (e.getButton() == NativeMouseEvent.BUTTON2) {
            btn = MouseButton.SECONDARY;
        }

        if (btn == null) return;
        if (buttons.contains(btn)) {
            var m = filterOneMacroToRun(null, btn);
            buttons.remove(btn);
            runMacro(m);
        }
        rememberMousePositionEnd(null, btn);
    }

    private AssistantMacroData filterOneMacroToRun(KeyCode currentKey, MouseButton currentMouse) {
        for (var m : macro.macros) {
            if (!m.enabled) {
                continue;
            }
            if (m.matches(keys, buttons, currentKey, currentMouse)) {
                return m;
            }
        }
        return null;
    }

    private void runMacro(AssistantMacroData m) {
        if (m != null) {
            TaskManager.execute(() -> {
                Logger.debug("before macro execution: " + m.name);
                m.exec();
                Logger.debug("after macro executioin: " + m.name);
            });
        }
    }

    private volatile boolean mouseIsReleased = false;
    private Thread mouseHandlingThread = null;
    private Point2D lastMousePosition = null;

    private class MouseHandlingThread extends Thread {
        @Override
        public void run() {
            Logger.debug("recording mouse position thread is running");
            while (mouseIsReleased) {
                lastMousePosition = Utils.execRobotOnThread(RobotWrapper::getMousePosition);
                try {
                    //noinspection BusyWait
                    Thread.sleep(10);
                } catch (InterruptedException ignore) {
                }
            }
            Logger.debug("recording mouse position thread is exiting");
        }
    }

    private void rememberMousePositionBegin(KeyCode currentKey, MouseButton currentMouse) {
        if (!rememberMousePositionSwitchButton.isSelected()) {
            return;
        }
        if (mouseIsReleased) {
            return;
        }
        if (mouseReleasedKeyNotPressed(currentKey, currentMouse)) {
            return;
        }
        synchronized (this) {
            if (mouseIsReleased) {
                return;
            }
            mouseIsReleased = true;
        }
        Logger.info("mouse is released");
        var pos = lastMousePosition;
        if (pos != null) {
            TaskManager.execute(() -> {
                try {
                    Thread.sleep(50); // the game has a short delay before releasing the mouse
                } catch (InterruptedException ignore) {
                }
                Utils.execRobot(r -> r.mouseMove(pos.getX(), pos.getY()));
            });
        }
        mouseHandlingThread = new MouseHandlingThread();
        mouseHandlingThread.start();
    }

    private void rememberMousePositionEnd(KeyCode currentKey, MouseButton currentMouse) {
        if (!rememberMousePositionSwitchButton.isSelected()) {
            return;
        }
        if (!mouseIsReleased) {
            return;
        }
        if (mouseReleasedKeyNotPressed(currentKey, currentMouse)) {
            return;
        }
        synchronized (this) {
            if (!mouseIsReleased) {
                return;
            }
            mouseIsReleased = false;
        }
        Logger.info("mouse is captured");
        mouseHandlingThread.interrupt();
        mouseHandlingThread = null;
    }

    private boolean mouseReleasedKeyNotPressed(KeyCode currentKey, MouseButton currentMouse) {
        var kb = this.releaseMouseKeyBinding;
        if (kb == null) return true;
        return !kb.matches(keys, buttons, currentKey, currentMouse);
    }

    @Override
    public void terminate() {
        var thread = mouseHandlingThread;
        if (thread != null) {
            mouseIsReleased = false;
            thread.interrupt();
        }
    }

    @SuppressWarnings("rawtypes")
    private final ChangeListener inputConfigChangeListener = (ob, old, now) -> loadReleaseMouseKeyBinding();

    @SuppressWarnings("unchecked")
    @Override
    public boolean enterCheck(boolean skipGamePathCheck) {
        if (!Utils.checkLock("macro")) {
            return false;
        }

        GlobalValues.useVersion.removeListener(inputConfigChangeListener);
        GlobalValues.savedPath.removeListener(inputConfigChangeListener);
        GlobalValues.useVersion.addListener(inputConfigChangeListener);
        GlobalValues.savedPath.addListener(inputConfigChangeListener);
        loadReleaseMouseKeyBinding();
        return reloadMacro();
    }

    private boolean reloadMacro() {
        Assistant a;
        try {
            a = AssistantConfig.readAssistant(true);
        } catch (Throwable t) {
            new StackTraceAlert(t).show();
            return false;
        }
        macro = a.macro;
        if (macro == null) {
            macro = new AssistantMacro();
        }
        if (macro.macros == null) {
            macro.macros = new ArrayList<>();
        }
        rememberMousePositionSwitchButton.setSelected(macro.rememberMousePosition);
        ls.setItems(FXCollections.observableList(macro.macros));
        return true;
    }

    private void loadReleaseMouseKeyBinding() {
        var savedPath = GlobalValues.savedPath.get();
        if (savedPath == null) return;
        var config = InputConfig.ofSaved(Path.of(GlobalValues.savedPath.get(), "Config", "WindowsNoEditor", "Input.ini").toString());
        List<KeyBinding> ls;
        try {
            ls = config.read();
        } catch (IOException e) {
            Logger.error("failed reading input config", e);
            var kb = new KeyBinding();
            kb.action = "SwitchMouse";
            kb.key = new Key(KeyCode.ALT);
            ls = Collections.singletonList(kb);
        }
        for (var kb : ls) {
            if (kb.action.equals("SwitchMouse")) {
                var data = new AssistantMacroData();
                data.ctrl = kb.ctrl;
                data.alt = kb.alt;
                data.shift = kb.shift;
                data.key = kb.key;
                releaseMouseKeyBinding = data;
                break;
            }
        }
    }

    private void flushConfig() {
        try {
            AssistantConfig.updateAssistant(config -> config.macro = macro);
        } catch (Throwable t) {
            new StackTraceAlert(t).show();
        }
    }
}
