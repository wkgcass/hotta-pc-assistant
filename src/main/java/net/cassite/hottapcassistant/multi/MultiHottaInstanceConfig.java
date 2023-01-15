package net.cassite.hottapcassistant.multi;

import net.cassite.hottapcassistant.ui.JSONJavaObject;
import net.cassite.hottapcassistant.util.Utils;
import vjson.JSON;
import vjson.deserializer.rule.ObjectRule;
import vjson.deserializer.rule.Rule;
import vjson.deserializer.rule.StringRule;
import vjson.util.ObjectBuilder;

public class MultiHottaInstanceConfig implements JSONJavaObject {
    String betaPath;
    String onlinePath;
    String advBranch;
    String resVersion;
    String resSubVersion;
    String clientVersion;

    public static final Rule<MultiHottaInstanceConfig> rule = new ObjectRule<>(MultiHottaInstanceConfig::new)
        .put("betaPath", (o, it) -> o.betaPath = it, StringRule.get())
        .put("onlinePath", (o, it) -> o.onlinePath = it, StringRule.get())
        .put("advBranch", (o, it) -> o.advBranch = it, StringRule.get())
        .put("resVersion", (o, it) -> o.resVersion = it, StringRule.get())
        .put("resSubVersion", (o, it) -> o.resSubVersion = it, StringRule.get())
        .put("clientVersion", (o, it) -> o.clientVersion = it, StringRule.get());

    public void clearEmptyFields() {
        betaPath = Utils.returnNullIfBlank(betaPath);
        onlinePath = Utils.returnNullIfBlank(onlinePath);
        advBranch = Utils.returnNullIfBlank(advBranch);
        resVersion = Utils.returnNullIfBlank(resVersion);
        resSubVersion = Utils.returnNullIfBlank(resSubVersion);
        clientVersion = Utils.returnNullIfBlank(clientVersion);
    }

    @Override
    public JSON.Object toJson() {
        clearEmptyFields();
        var ob = new ObjectBuilder();
        if (betaPath != null) {
            ob.put("betaPath", betaPath);
        }
        if (onlinePath != null) {
            ob.put("onlinePath", onlinePath);
        }
        if (advBranch != null) {
            ob.put("advBranch", advBranch);
        }
        if (resVersion != null) {
            ob.put("resVersion", resVersion);
        }
        if (resSubVersion != null) {
            ob.put("resSubVersion", resSubVersion);
        }
        if (clientVersion != null) {
            ob.put("clientVersion", clientVersion);
        }
        return ob.build();
    }

    public boolean hasEmptyField() {
        clearEmptyFields();
        return betaPath == null || onlinePath == null || advBranch == null || resVersion == null || resSubVersion == null || clientVersion == null;
    }
}