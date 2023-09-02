package net.cassite.hottapcassistant.entity;

import net.cassite.hottapcassistant.i18n.I18nType;
import vjson.JSON;
import vjson.deserializer.rule.BoolRule;
import vjson.deserializer.rule.ObjectRule;
import vjson.deserializer.rule.Rule;
import vjson.deserializer.rule.StringRule;
import vjson.util.ObjectBuilder;

public class Assistant {
    public AssistantLastValues lastValues;
    public AssistantMacro macro;
    public AssistantFishing fishing;
    public AssistantCoolDown cooldown;
    public boolean disableAlertingGPL;
    public I18nType i18n;

    public static final Rule<Assistant> rule = new ObjectRule<>(Assistant::new)
        .put("lastValues", (o, it) -> o.lastValues = it, AssistantLastValues.rule)
        .put("macro", (o, it) -> o.macro = it, AssistantMacro.rule)
        .put("fishing", (o, it) -> o.fishing = it, AssistantFishing.rule)
        .put("cooldown", (o, it) -> o.cooldown = it, AssistantCoolDown.rule)
        .put("disableAlertingGPL", (o, it) -> o.disableAlertingGPL = it, BoolRule.get())
        .put("i18n", (o, it) -> o.i18n = I18nType.valueOf(it), StringRule.get());

    public static Assistant empty() {
        var ret = new Assistant();
        ret.lastValues = null;
        ret.macro = AssistantMacro.empty();
        ret.fishing = AssistantFishing.empty();
        ret.i18n = I18nType.ZhCn;
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
        ob.put("disableAlertingGPL", disableAlertingGPL);
        if (i18n != null) {
            ob.put("i18n", i18n.name());
        }
        return ob.build();
    }
}
