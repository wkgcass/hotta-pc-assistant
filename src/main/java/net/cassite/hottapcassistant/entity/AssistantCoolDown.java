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

public class AssistantCoolDown extends AssistantCoolDownBase {
    public Set<String> row2Ids;
    public List<AssistantCoolDownConfiguration> configurations;
    public AssistantCoolDownOptions options;

    public static final Rule<AssistantCoolDown> rule = new ObjectRule<>(AssistantCoolDown::new, (ObjectRule<AssistantCoolDownBase>) AssistantCoolDownBase.rule)
        .put("row2Ids", (o, it) -> o.row2Ids = it, new ArrayRule<Set<String>, String>(HashSet::new, Set::add, StringRule.get()))
        .put("configurations", (o, it) -> o.configurations = it, new ArrayRule<List<AssistantCoolDownConfiguration>, AssistantCoolDownConfiguration>(
            ArrayList::new, List::add, AssistantCoolDownConfiguration.rule
        ))
        .put("options", (o, it) -> o.options = it, AssistantCoolDownOptions.rule);

    public JSON.Object toJson() {
        var ob = new ObjectBuilder(super.toJson());
        if (row2Ids != null) {
            ob.putArray("row2Ids", arr -> row2Ids.forEach(arr::add));
        }
        if (configurations != null) {
            ob.putArray("configurations", arr -> configurations.forEach(e -> arr.addInst(e.toJson())));
        }
        if (options != null) {
            ob.putInst("options", options.toJson());
        }
        return ob.build();
    }
}
