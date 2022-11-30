package net.cassite.hottapcassistant.entity;

import vjson.JSON;
import vjson.deserializer.rule.ObjectRule;
import vjson.deserializer.rule.Rule;
import vjson.deserializer.rule.StringRule;
import vjson.util.ObjectBuilder;

public class GameAssistant {
    public GameVersion version;

    public static final Rule<GameAssistant> rule = new ObjectRule<>(GameAssistant::new)
        .put("version", (o, it) -> o.version = GameVersion.valueOf(it), StringRule.get());

    public static GameAssistant empty() {
        //noinspection UnnecessaryLocalVariable
        var ret = new GameAssistant();
        return ret;
    }

    public JSON.Object toJson() {
        var ob = new ObjectBuilder();
        if (version != null) {
            ob.put("version", version.name());
        }
        return ob.build();
    }
}
