package net.cassite.hottapcassistant.entity;

import vjson.JSON;
import vjson.deserializer.rule.ArrayRule;
import vjson.deserializer.rule.ObjectRule;
import vjson.deserializer.rule.Rule;
import vjson.deserializer.rule.StringRule;
import vjson.util.ObjectBuilder;

import java.util.ArrayList;
import java.util.List;

public class AssistantLastValues {
    public String savedPath;
    public String gamePath;
    public String globalServerGamePath;
    public List<String> requireWritingHostsFileServerNames;

    public static final Rule<AssistantLastValues> rule = new ObjectRule<>(AssistantLastValues::new)
        .put("savedPath", (o, it) -> o.savedPath = it, StringRule.get())
        .put("gamePath", (o, it) -> o.gamePath = it, StringRule.get())
        .put("globalServerGamePath", (o, it) -> o.globalServerGamePath = it, StringRule.get())
        .put("requireWritingHostsFileServerNames", (o, it) -> o.requireWritingHostsFileServerNames = it,
            new ArrayRule<List<String>, String>(ArrayList::new, List::add, StringRule.get()));

    public static AssistantLastValues empty() {
        var ret = new AssistantLastValues();
        ret.savedPath = null;
        ret.gamePath = null;
        ret.globalServerGamePath = null;
        ret.requireWritingHostsFileServerNames = null;
        return ret;
    }

    public JSON.Object toJson() {
        var ob = new ObjectBuilder();
        if (savedPath != null) {
            ob.put("savedPath", savedPath);
        }
        if (gamePath != null) {
            ob.put("gamePath", gamePath);
        }
        if (globalServerGamePath != null) {
            ob.put("globalServerGamePath", globalServerGamePath);
        }
        if (requireWritingHostsFileServerNames != null) {
            ob.putArray("requireWritingHostsFileServerNames", arr -> requireWritingHostsFileServerNames.forEach(arr::add));
        }
        return ob.build();
    }
}
