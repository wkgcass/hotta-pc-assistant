package net.cassite.hottapcassistant.entity;

import vjson.JSON;
import vjson.deserializer.rule.ArrayRule;
import vjson.deserializer.rule.ObjectRule;
import vjson.deserializer.rule.Rule;
import vjson.deserializer.rule.StringRule;
import vjson.util.ObjectBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AssistantCoolDown {
    public List<AssistantCoolDownWeapon> weapons;
    public List<AssistantCoolDownRelics> relics;
    public AssistantCoolDownSimulacra simulacra;
    public Set<String> row2Ids;

    public static final Rule<AssistantCoolDown> rule = new ObjectRule<>(AssistantCoolDown::new)
        .put("weapons", (o, it) -> o.weapons = it, new ArrayRule<List<AssistantCoolDownWeapon>, AssistantCoolDownWeapon>(
            ArrayList::new, List::add, AssistantCoolDownWeapon.rule
        ))
        .put("relics", (o, it) -> o.relics = it, new ArrayRule<List<AssistantCoolDownRelics>, AssistantCoolDownRelics>(
            ArrayList::new, List::add, AssistantCoolDownRelics.rule
        ))
        .put("simulacra", (o, it) -> o.simulacra = it, AssistantCoolDownSimulacra.rule)
        .put("row2Ids", (o, it) -> o.row2Ids = it, new ArrayRule<Set<String>, String>(HashSet::new, Set::add, StringRule.get()));

    public JSON.Object toJson() {
        var ob = new ObjectBuilder();
        if (weapons != null)
            ob.putArray("weapons", arr -> weapons.forEach(e -> arr.addInst(e.toJson())));
        if (relics != null)
            ob.putArray("relics", arr -> relics.forEach(e -> arr.addInst(e.toJson())));
        if (simulacra != null)
            ob.putInst("simulacra", simulacra.toJson());
        if (row2Ids != null) {
            ob.putArray("row2Ids", arr -> row2Ids.forEach(arr::add));
        }
        return ob.build();
    }
}
