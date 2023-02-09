package net.cassite.hottapcassistant.entity;

import io.vproxy.vfx.entity.input.Key;
import io.vproxy.vfx.entity.input.KeyCode;
import javafx.scene.input.MouseButton;
import vjson.JSON;
import vjson.deserializer.rule.ArrayRule;
import vjson.deserializer.rule.BoolRule;
import vjson.deserializer.rule.ObjectRule;
import vjson.deserializer.rule.Rule;
import vjson.util.ObjectBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AssistantMacro {
    public boolean rememberMousePosition = false;
    public List<AssistantMacroData> macros;

    public static final Rule<AssistantMacro> rule = new ObjectRule<>(AssistantMacro::new)
        .put("rememberMousePosition", (o, it) -> o.rememberMousePosition = it, BoolRule.get())
        .put("macros", (o, it) -> o.macros = it,
            new ArrayRule<ArrayList<AssistantMacroData>, AssistantMacroData>(ArrayList::new, ArrayList::add, AssistantMacroData.rule));

    public static AssistantMacro empty() {
        var macro = new AssistantMacro();
        macro.rememberMousePosition = false;
        macro.macros = new ArrayList<>();
        {
            var m = new AssistantMacroData();
            m.name = "红莲飞";
            m.key = new Key(KeyCode.F10);
            m.steps = Arrays.asList(
                new AssistantMacroStep.KeyPress(new Key(MouseButton.PRIMARY)),
                new AssistantMacroStep.KeyRelease(new Key(MouseButton.PRIMARY)),
                new AssistantMacroStep.Delay(150),
                new AssistantMacroStep.KeyPress(new Key(MouseButton.PRIMARY)),
                new AssistantMacroStep.KeyRelease(new Key(MouseButton.PRIMARY)),
                new AssistantMacroStep.Delay(150),
                new AssistantMacroStep.KeyPress(new Key(MouseButton.PRIMARY)),
                new AssistantMacroStep.KeyRelease(new Key(MouseButton.PRIMARY)),
                new AssistantMacroStep.Delay(260),
                new AssistantMacroStep.KeyPress(new Key(KeyCode.KEY_2)),
                new AssistantMacroStep.KeyRelease(new Key(KeyCode.KEY_2)),
                new AssistantMacroStep.Delay(10),
                new AssistantMacroStep.KeyPress(new Key(KeyCode.KEY_2)),
                new AssistantMacroStep.KeyRelease(new Key(KeyCode.KEY_2))
            );
            m.isSystemPreBuilt = true;
            macro.macros.add(m);
        }
        {
            var m = new AssistantMacroData();
            m.name = "大锤宏";
            m.type = AssistantMacroType.INFINITE_LOOP;
            m.ctrl = true;
            m.alt = true;
            m.key = new Key(KeyCode.F9);
            m.steps = Arrays.asList(
                new AssistantMacroStep.KeyPress(new Key(MouseButton.PRIMARY)),
                new AssistantMacroStep.Delay(3000),
                new AssistantMacroStep.KeyRelease(new Key(MouseButton.PRIMARY)),
                new AssistantMacroStep.Delay(500)
            );
            m.isSystemPreBuilt = true;
        }
        return macro;
    }

    public JSON.Object toJson() {
        var ob = new ObjectBuilder();
        ob.put("rememberMousePosition", rememberMousePosition);
        if (macros == null) {
            ob.putArray("macros", a -> {
            });
        } else {
            ob.putArray("macros", a -> macros.forEach(e -> a.addInst(e.toJson())));
        }
        return ob.build();
    }
}
