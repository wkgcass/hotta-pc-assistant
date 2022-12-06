package net.cassite.hottapcassistant.entity;

import vjson.JSON;
import vjson.deserializer.rule.ArrayRule;
import vjson.deserializer.rule.IntRule;
import vjson.deserializer.rule.ObjectRule;
import vjson.deserializer.rule.Rule;
import vjson.util.ObjectBuilder;

import java.util.ArrayList;
import java.util.List;

public class AssistantCoolDownWeapon {
    public int weaponId;
    public int stars;
    public List<AssistantCoolDownWeaponMatrix> matrix;

    public static final Rule<AssistantCoolDownWeapon> rule = new ObjectRule<>(AssistantCoolDownWeapon::new)
        .put("weaponId", (o, it) -> o.weaponId = it, IntRule.get())
        .put("stars", (o, it) -> o.stars = it, IntRule.get())
        .put("matrix", (o, it) -> o.matrix = it, new ArrayRule<List<AssistantCoolDownWeaponMatrix>, AssistantCoolDownWeaponMatrix>(
            ArrayList::new, List::add, AssistantCoolDownWeaponMatrix.rule
        ));

    public JSON.Object toJson() {
        var ob = new ObjectBuilder()
            .put("weaponId", weaponId)
            .put("stars", stars);
        if (matrix != null)
            ob.putArray("matrix", arr -> matrix.forEach(e -> arr.addInst(e.toJson())));
        return ob.build();
    }
}
