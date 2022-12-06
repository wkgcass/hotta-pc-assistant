package net.cassite.hottapcassistant.entity;

import vjson.JSON;
import vjson.deserializer.rule.IntRule;
import vjson.deserializer.rule.ObjectRule;
import vjson.deserializer.rule.Rule;
import vjson.util.ObjectBuilder;

public class AssistantCoolDownWeaponMatrix {
    public int matrixId;
    public int stars;

    public static final Rule<AssistantCoolDownWeaponMatrix> rule = new ObjectRule<>(AssistantCoolDownWeaponMatrix::new)
        .put("matrixId", (o, it) -> o.matrixId = it, IntRule.get())
        .put("stars", (o, it) -> o.stars = it, IntRule.get());

    public JSON.Object toJson() {
        return new ObjectBuilder()
            .put("matrixId", matrixId)
            .put("stars", stars)
            .build();
    }
}
