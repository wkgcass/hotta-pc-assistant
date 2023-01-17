package net.cassite.hottapcassistant.multi;

import io.vproxy.vfx.util.MiscUtils;
import net.cassite.hottapcassistant.ui.JSONJavaObject;
import vjson.JSON;
import vjson.deserializer.rule.ObjectRule;
import vjson.deserializer.rule.Rule;
import vjson.deserializer.rule.StringRule;
import vjson.util.ObjectBuilder;

public class MultiHottaInstanceConfig implements JSONJavaObject {
    String betaPath;
    String onlinePath;
    String advBranch;

    public static final Rule<MultiHottaInstanceConfig> rule = new ObjectRule<>(MultiHottaInstanceConfig::new)
        .put("betaPath", (o, it) -> o.betaPath = it, StringRule.get())
        .put("onlinePath", (o, it) -> o.onlinePath = it, StringRule.get())
        .put("advBranch", (o, it) -> o.advBranch = it, StringRule.get());

    public void clearEmptyFields() {
        betaPath = MiscUtils.returnNullIfBlank(betaPath);
        onlinePath = MiscUtils.returnNullIfBlank(onlinePath);
        advBranch = MiscUtils.returnNullIfBlank(advBranch);
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
        return ob.build();
    }

    public boolean hasEmptyField() {
        clearEmptyFields();
        return betaPath == null || onlinePath == null || advBranch == null;
    }
}
