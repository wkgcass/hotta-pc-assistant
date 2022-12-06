package net.cassite.hottapcassistant.entity;

import vjson.JSON;
import vjson.deserializer.rule.ObjectRule;
import vjson.deserializer.rule.Rule;
import vjson.util.ObjectBuilder;

public class Assistant {
    public AssistantLastValues lastValues;
    public AssistantMacro macro;
    public AssistantFishing fishing;
    public AssistantCoolDown cooldown;

    public static final Rule<Assistant> rule = new ObjectRule<>(Assistant::new)
        .put("lastValues", (o, it) -> o.lastValues = it, AssistantLastValues.rule)
        .put("macro", (o, it) -> o.macro = it, AssistantMacro.rule)
        .put("fishing", (o, it) -> o.fishing = it, AssistantFishing.rule)
        .put("cooldown", (o, it) -> o.cooldown = it, AssistantCoolDown.rule);

    public static Assistant empty() {
        var ret = new Assistant();
        ret.lastValues = null;
        ret.macro = AssistantMacro.empty();
        ret.fishing = AssistantFishing.empty();
        return ret;
    }

    public JSON.Object toJson() {
        var ob = new ObjectBuilder();
        if (lastValues != null)
            ob.putInst("lastValues", lastValues.toJson());
        if (macro != null)
            ob.putInst("macro", macro.toJson());
        if (fishing != null)
            ob.putInst("fishing", fishing.toJson());
        if (cooldown != null) {
            ob.putInst("cooldown", cooldown.toJson());
        }
        return ob.build();
    }
}
