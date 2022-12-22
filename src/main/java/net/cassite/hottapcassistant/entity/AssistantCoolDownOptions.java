package net.cassite.hottapcassistant.entity;

import vjson.JSON;
import vjson.deserializer.rule.BoolRule;
import vjson.deserializer.rule.ObjectRule;
import vjson.deserializer.rule.Rule;
import vjson.util.ObjectBuilder;

public class AssistantCoolDownOptions {
    public boolean scanDischarge;
    public boolean scanDischargeDebug;
    public Rect scanDischargeRect;

    public static final Rule<AssistantCoolDownOptions> rule = new ObjectRule<>(AssistantCoolDownOptions::new)
        .put("scanDischarge", (o, it) -> o.scanDischarge = it, BoolRule.get())
        .put("scanDischargeDebug", (o, it) -> o.scanDischargeDebug = it, BoolRule.get())
        .put("scanDischargeRect", (o, it) -> o.scanDischargeRect = it, Rect.rule);

    public static AssistantCoolDownOptions empty() {
        return new AssistantCoolDownOptions();
    }

    public JSON.Object toJson() {
        var ob = new ObjectBuilder()
            .put("scanDischarge", scanDischarge)
            .put("scanDischargeDebug", scanDischargeDebug);
        if (scanDischargeRect != null) {
            ob.putInst("scanDischargeRect", scanDischargeRect.toJson());
        }
        return ob.build();
    }
}
