package net.cassite.hottapcassistant.entity;

import vjson.JSON;
import vjson.deserializer.rule.IntRule;
import vjson.deserializer.rule.ObjectRule;
import vjson.deserializer.rule.Rule;
import vjson.util.ObjectBuilder;

public class AssistantCoolDownSimulacra {
    public int simulacraId;

    public static final Rule<AssistantCoolDownSimulacra> rule = new ObjectRule<>(AssistantCoolDownSimulacra::new)
        .put("simulacraId", (o, it) -> o.simulacraId = it, IntRule.get());

    public JSON.Object toJson() {
        return new ObjectBuilder()
            .put("simulacraId", simulacraId)
            .build();
    }
}
