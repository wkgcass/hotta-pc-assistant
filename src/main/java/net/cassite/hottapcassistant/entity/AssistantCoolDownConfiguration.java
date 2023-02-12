package net.cassite.hottapcassistant.entity;

import vjson.JSON;
import vjson.deserializer.rule.ObjectRule;
import vjson.deserializer.rule.Rule;
import vjson.deserializer.rule.StringRule;
import vjson.util.ObjectBuilder;

public class AssistantCoolDownConfiguration extends AssistantCoolDownBase {
    public String name;

    public static final Rule<AssistantCoolDownConfiguration> rule = new ObjectRule<>(AssistantCoolDownConfiguration::new, (ObjectRule<AssistantCoolDownBase>) AssistantCoolDownBase.rule)
        .put("name", (o, it) -> o.name = it, StringRule.get());

    public JSON.Object toJson() {
        var ob = new ObjectBuilder(super.toJson());
        ob.put("name", name);
        return ob.build();
    }
}
