package net.cassite.hottapcassistant.ui;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import io.vproxy.base.util.LogType;
import io.vproxy.vfx.control.globalscreen.GlobalScreenUtils;
import io.vproxy.vfx.entity.input.Key;
import io.vproxy.vfx.entity.input.KeyCode;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.manager.task.TaskManager;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.alert.SimpleAlert;
import io.vproxy.vfx.ui.alert.StackTraceAlert;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.layout.HPadding;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.pane.FusionPane;
import io.vproxy.vfx.ui.toggle.ToggleSwitch;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import io.vproxy.base.util.Logger;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.cassite.hottapcassistant.component.macro.UIMacroList;
import net.cassite.hottapcassistant.config.AssistantConfig;
import net.cassite.hottapcassistant.config.InputConfig;
import net.cassite.hottapcassistant.entity.*;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.status.Status;
import net.cassite.hottapcassistant.status.StatusComponent;
import net.cassite.hottapcassistant.status.StatusEnum;
import net.cassite.hottapcassistant.status.StatusManager;
import net.cassite.hottapcassistant.util.*;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.*;

public class MacroScene extends AbstractMainScene implements NativeKeyListener, NativeMouseListener, EnterCheck, Terminate {
    private final ToggleSwitch switchButton = new ToggleSwitch(10, 30);
    private final ToggleSwitch rememberMousePositionSwitchButton = new ToggleSwitch(10, 30);
    private final UIMacroList ls = new UIMacroList(this::flushConfig);

    private final Set<KeyCode> keys = new HashSet<>();
    private final Set<MouseButton> buttons = new HashSet<>();

    private AssistantMacroData releaseMouseKeyBinding;
    private AssistantMacro macro;

    public MacroScene() {
        enableAutoContentWidthHeight();
        var topVBox = new VBox();
        FXUtils.observeWidthHeight(getContentPane(), topVBox, -20, -5);
        topVBox.setLayoutX(10);

        var switchHBox = new HBox();
        var switchBtnLabel = new ThemeLabel(I18n.get().macroSwitchButtonLabel());
        switchHBox.getChildren().addAll(switchBtnLabel, new HPadding(15), switchButton.getNode());
        var macroAlertLabel = new ThemeLabel(I18n.get().macroAlertLabel()) {{
            FontManager.get().setFont(this);
        }};
        var knowConsequenceCheckBox = new CheckBox(I18n.get().knowConsequencePrompt()) {{
            FontManager.get().setFont(this);
            FXUtils.disableFocusColor(this);
            setTextFill(Theme.current().normalTextColor());
        }};
        var rememberHBox = new HBox();
        var rememberBtnLabel = new ThemeLabel(I18n.get().rememberMousePositionButtonLabel());
        rememberHBox.getChildren().addAll(rememberBtnLabel, new HPadding(15), rememberMousePositionSwitchButton.getNode());
        topVBox.getChildren().addAll(
            new VPadding(10),
            switchHBox,
            new VPadding(10),
            macroAlertLabel,
            new VPadding(10),
            knowConsequenceCheckBox,
            new VPadding(20),
            rememberHBox);

        boolean[] consequenceIsCheckedOnSelect = new boolean[]{false};
        switchButton.selectedProperty().addListener((ob, old, now) -> {
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
                StatusManager.get().updateStatus(new Status(
                    I18n.get().toolNameMacro(),
                    StatusComponent.MODULE,
                    StatusEnum.RUNNING
                ));
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
                for (var m : macro.macros) {
                    if (m.getStatus() == AssistantMacroStatus.RUNNING) {
                        m.setStatus(AssistantMacroStatus.STOPPING);
                    }
                }
                StatusManager.get().updateStatus(new Status(
                    I18n.get().toolNameMacro(),
                    StatusComponent.MODULE,
                    StatusEnum.STOPPED
                ));
            }
        });
        StatusManager.get().updateStatus(new Status(
            I18n.get().toolNameMacro(),
            StatusComponent.MODULE,
            StatusEnum.STOPPED
        ));
        rememberMousePositionSwitchButton.selectedProperty().addListener((ob, old, now) -> {
            if (now == null) return;
            if (Objects.equals(old, now)) return;
            if (!rememberMousePositionSwitchButton.isSelected()) {
                mouseIsReleased = false;
            }
            macro.rememberMousePosition = now;
            flushConfig();
        });

        FXUtils.observeHeight(getContentPane(), ls.getNode(), -220);
        topVBox.getChildren().addAll(
            new VPadding(10),
            ls.getNode());

        var bottomPane = new FusionPane();
        FXUtils.observeWidth(getContentPane(), bottomPane.getNode(), -20);
        bottomPane.getNode().setPrefHeight(60);

        var macroTipsBtn = new FusionButton(I18n.get().macroTipsButton());
        FXUtils.observeHeight(bottomPane.getContentPane(), macroTipsBtn);
        macroTipsBtn.setOnAction(e -> SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().macroTips(), Consts.JetbrainsMonoFont));
        macroTipsBtn.setPrefWidth(120);
        var reloadMacro = new FusionButton(I18n.get().reloadMacro());
        FXUtils.observeHeight(bottomPane.getContentPane(), reloadMacro);
        reloadMacro.setOnAction(e -> {
            switchButton.setSelected(false);
            reloadMacro();
        });
        reloadMacro.setPrefWidth(120);
        var editMacro = new FusionButton(I18n.get().editMacro());
        FXUtils.observeHeight(bottomPane.getContentPane(), editMacro);
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
                SimpleAlert.show(Alert.AlertType.ERROR, I18n.get().openFileFailed());
            }
        });
        editMacro.setPrefWidth(120);
        var bottomButtons = new HBox();
        FXUtils.observeWidth(bottomPane.getContentPane(), bottomButtons);
        bottomButtons.setAlignment(Pos.CENTER_RIGHT);
        bottomButtons.getChildren().addAll(macroTipsBtn, new HPadding(10), reloadMacro, new HPadding(10), editMacro);

        bottomPane.getContentPane().getChildren().add(bottomButtons);
        topVBox.getChildren().add(bottomPane.getNode());
        getContentPane().getChildren().addAll(
            new VPadding(10),
            topVBox);
    }

    @Override
    protected boolean hideMenuButton() {
        return true;
    }

    @Override
    public String title() {
        return I18n.get().toolNameMacro();
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
            runOrStopMacro(m);
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
            runOrStopMacro(m);
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

    private void runOrStopMacro(AssistantMacroData m) {
        if (m == null) {
            return;
        }
        if (m.getStatus() == AssistantMacroStatus.RUNNING) {
            FXUtils.runOnFX(() -> {
                if (m.getStatus() == AssistantMacroStatus.RUNNING) {
                    m.setStatus(AssistantMacroStatus.STOPPING);
                }
            });
            return;
        }
        if (m.getStatus() == AssistantMacroStatus.STOPPING) {
            return; // do nothing
        }
        m.setStatus(AssistantMacroStatus.RUNNING);
        TaskManager.get().execute(() -> {
            int loopCount = 0;
            loop:
            while (true) {
                if (m.getStatus() == AssistantMacroStatus.STOPPING) {
                    assert Logger.lowLevelDebug("macro execution stopping");
                    break;
                }
                if (m.type == AssistantMacroType.FINITE_LOOP) {
                    if (loopCount >= m.loopLimit) {
                        assert Logger.lowLevelDebug("finite loop reaches limit: " + loopCount);
                        break;
                    }
                }
                ++loopCount;
                assert Logger.lowLevelDebug("before macro execution: " + m.name);
                m.exec();
                assert Logger.lowLevelDebug("after macro execution: " + m.name);
                switch (m.type) {
                    case NORMAL -> {
                        break loop;
                    }
                    case INFINITE_LOOP -> {
                        //noinspection UnnecessaryContinue
                        continue;
                    }
                }
            }
            afterMacro();
            FXUtils.runOnFX(() -> m.setStatus(AssistantMacroStatus.STOPPED));
        });
    }

    private void afterMacro() {
        StressWorkers.get().end();
    }

    private volatile boolean mouseIsReleased = false;
    private Thread mouseHandlingThread = null;
    private Point2D lastMousePosition = null;

    private class MouseHandlingThread extends Thread {
        @Override
        public void run() {
            assert Logger.lowLevelDebug("recording mouse position thread is running");
            while (mouseIsReleased) {
                lastMousePosition = Utils.execRobotOnThread(RobotWrapper::getMousePosition);
                try {
                    //noinspection BusyWait
                    Thread.sleep(10);
                } catch (InterruptedException ignore) {
                }
            }
            assert Logger.lowLevelDebug("recording mouse position thread is exiting");
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
        Logger.alert("mouse is released");
        var pos = lastMousePosition;
        if (pos != null) {
            TaskManager.get().execute(() -> {
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
        Logger.alert("mouse is captured");
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
            StackTraceAlert.show(I18n.get().readAssistantConfigFailed(), t);
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
        ls.setItems(macro.macros);
        return true;
    }

    private void loadReleaseMouseKeyBinding() {
        var savedPath = GlobalValues.savedPath.get();
        if (savedPath == null) return;
        var config = InputConfig.ofSaved(Path.of(GlobalValues.savedPath.get(), "Config", "WindowsNoEditor", "Input.ini").toString());
        List<KeyBinding> ls;
        try {
            ls = config.read();
        } catch (Exception e) {
            Logger.error(LogType.FILE_ERROR, "failed reading input config", e);
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
            StackTraceAlert.show(I18n.get().writeAssistantConfigFailed(), t);
        }
    }
}
