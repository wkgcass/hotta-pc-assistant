package net.cassite.hottapcassistant.feed;

import vjson.JSON;
import vjson.deserializer.rule.IntRule;
import vjson.deserializer.rule.ObjectRule;
import vjson.deserializer.rule.Rule;
import vjson.util.ObjectBuilder;

public class Metadata {
    public int version;

    public static final Rule<Metadata> rule = new ObjectRule<>(Metadata::new)
        .put("version", (o, it) -> o.version = it, IntRule.get());

    public JSON.Object toJson() {
        return new ObjectBuilder()
            .put("version", version)
            .build();
    }
}
