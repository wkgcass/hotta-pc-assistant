package net.cassite.hottapcassistant.entity;

import javafx.scene.input.MouseButton;
import vjson.JSON;
import vjson.deserializer.rule.ObjectRule;
import vjson.deserializer.rule.Rule;
import vjson.deserializer.rule.StringRule;
import vjson.util.ObjectBuilder;

import java.util.ArrayList;
import java.util.Arrays;

public class Assistant {
    public String version;
    public AssistantMacro macro;
    public AssistantFishing fishing;

    public static final Rule<Assistant> rule = new ObjectRule<>(Assistant::new)
        .put("version", (o, it) -> o.version = it, StringRule.get())
        .put("macro", (o, it) -> o.macro = it, AssistantMacro.rule)
        .put("fishing", (o, it) -> o.fishing = it, AssistantFishing.rule);

    public static Assistant empty() {
        var ret = new Assistant();
        ret.macro = AssistantMacro.empty();
        ret.fishing = AssistantFishing.empty();
        return ret;
    }

    public JSON.Object toJson() {
        var ob = new ObjectBuilder();
        if (version != null) {
            ob.put("version", version);
        }
        ob.putInst("macro", macro.toJson());
        ob.putInst("fishing", fishing.toJson());
        return ob.build();
    }
}
