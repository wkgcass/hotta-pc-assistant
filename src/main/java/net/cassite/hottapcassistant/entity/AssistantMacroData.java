package net.cassite.hottapcassistant.entity;

import vjson.JSON;
import vjson.deserializer.rule.*;
import vjson.util.ObjectBuilder;

import java.util.ArrayList;
import java.util.List;

public class AssistantMacroData extends InputData {
    public boolean enabled;
    public String name;
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

    public void exec() {
        for (var s : steps) {
            s.exec();
        }
    }
}
