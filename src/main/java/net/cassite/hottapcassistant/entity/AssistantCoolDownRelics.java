package net.cassite.hottapcassistant.entity;

import vjson.JSON;
import vjson.deserializer.rule.IntRule;
import vjson.deserializer.rule.ObjectRule;
import vjson.deserializer.rule.Rule;
import vjson.util.ObjectBuilder;

public class AssistantCoolDownRelics {
    public int relicsId;
    public int stars;

    public static final Rule<AssistantCoolDownRelics> rule = new ObjectRule<>(AssistantCoolDownRelics::new)
        .put("relicsId", (o, it) -> o.relicsId = it, IntRule.get())
        .put("stars", (o, it) -> o.stars = it, IntRule.get());

    public JSON.Object toJson() {
        return new ObjectBuilder()
            .put("relicsId", relicsId)
            .put("stars", stars)
            .build();
    }
}
