package net.cassite.hottapcassistant.entity;

import vjson.JSON;
import vjson.deserializer.rule.ArrayRule;
import vjson.deserializer.rule.ObjectRule;
import vjson.deserializer.rule.Rule;
import vjson.deserializer.rule.StringRule;
import vjson.util.ObjectBuilder;

import java.util.ArrayList;
import java.util.List;

public class AssistantCoolDownConfiguration {
    public String name;
    public List<AssistantCoolDownWeapon> weapons;
    public List<AssistantCoolDownRelics> relics;
    public AssistantCoolDownSimulacra simulacra;

    public static final Rule<AssistantCoolDownConfiguration> rule = new ObjectRule<>(AssistantCoolDownConfiguration::new)
        .put("name", (o, it) -> o.name = it, StringRule.get())
        .put("weapons", (o, it) -> o.weapons = it, new ArrayRule<List<AssistantCoolDownWeapon>, AssistantCoolDownWeapon>(
            ArrayList::new, List::add, AssistantCoolDownWeapon.rule
        ))
        .put("relics", (o, it) -> o.relics = it, new ArrayRule<List<AssistantCoolDownRelics>, AssistantCoolDownRelics>(
            ArrayList::new, List::add, AssistantCoolDownRelics.rule
        ))
        .put("simulacra", (o, it) -> o.simulacra = it, AssistantCoolDownSimulacra.rule);

    public JSON.Object toJson() {
        var ob = new ObjectBuilder();
        ob.put("name", name);
        if (weapons != null)
            ob.putArray("weapons", arr -> weapons.forEach(e -> arr.addInst(e.toJson())));
        if (relics != null)
            ob.putArray("relics", arr -> relics.forEach(e -> arr.addInst(e.toJson())));
        if (simulacra != null)
            ob.putInst("simulacra", simulacra.toJson());
        return ob.build();
    }
}
