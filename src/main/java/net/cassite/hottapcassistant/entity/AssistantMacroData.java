package net.cassite.hottapcassistant.entity;

import javafx.scene.input.MouseButton;
import vjson.JSON;
import vjson.deserializer.rule.*;
import vjson.util.ObjectBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AssistantMacroData {
    public boolean enabled;
    public String name;
    public boolean ctrl;
    public boolean alt;
    public boolean shift;
    public Key key;
    public List<AssistantMacroStep> steps;

    public static final Rule<AssistantMacroData> rule = new ObjectRule<>(AssistantMacroData::new)
        .put("enabled", (o, it) -> o.enabled = it, BoolRule.get())
        .put("name", (o, it) -> o.name = it, StringRule.get())
        .put("ctrl", (o, it) -> o.ctrl = it, BoolRule.get())
        .put("alt", (o, it) -> o.alt = it, BoolRule.get())
        .put("shift", (o, it) -> o.shift = it, BoolRule.get())
        .put("key", (o, it) -> o.key = new Key(it), StringRule.get())
        .put("steps", (o, it) -> o.steps = it,
            new ArrayRule<ArrayList<AssistantMacroStep>, AssistantMacroStep>(ArrayList::new, ArrayList::add, AssistantMacroStep.rule));

    public JSON.Object toJson() {
        return new ObjectBuilder()
            .put("enabled", enabled)
            .put("name", name)
            .put("ctrl", ctrl)
            .put("alt", alt)
            .put("shift", shift)
            .put("key", key.toString())
            .putArray("steps", a -> steps.forEach(e -> a.addInst(e.toJson())))
            .build();
    }

    public boolean matches(Set<KeyCode> keys, Set<MouseButton> buttons, KeyCode currentKey, MouseButton currentMouse) {
        if (ctrl) {
            if (!keys.contains(KeyCode.CONTROL)) return false;
        }
        if (alt) {
            if (!keys.contains(KeyCode.ALT)) return false;
        }
        if (shift) {
            if (!keys.contains(KeyCode.SHIFT)) return false;
        }
        if (key.key != null) return key.key == currentKey;
        if (key.button != null) return key.button == currentMouse;
        return false;
    }

    public void exec() {
        for (var s : steps) {
            s.exec();
        }
    }
}
