package net.cassite.hottapcassistant.multi;

import io.vproxy.vfx.util.MiscUtils;
import vjson.JSON;
import vjson.JSONObject;
import vjson.deserializer.rule.IntRule;
import vjson.deserializer.rule.ObjectRule;
import vjson.deserializer.rule.Rule;
import vjson.deserializer.rule.StringRule;
import vjson.util.ObjectBuilder;

import java.util.Objects;

import static net.cassite.hottapcassistant.multi.MultiHottaInstanceFlow.DEFAULT_RES_SUB_VERSION;
import static net.cassite.hottapcassistant.multi.MultiHottaInstanceFlow.DEFAULT_RES_VERSION;

public class MultiHottaInstanceConfig implements JSONObject {
    String betaPath; // nullable, requires advBranch
    String onlinePath; // non-null
    String onlineModPath; // nullable, requires onlineModBranch, onlineVersion
    String advBranch; // nullable, required by betaPath
    String onlineBranch; // non-null
    String onlineModBranch; // nullable, required by onlineModPath
    String onlineVersion; // nullable, required by onlineModPath
    int disableTips;
    int onlineBranchVersion;

    public MultiHottaInstanceConfig() {
    }

    public static final int CURRENT_ONLINE_BRANCH_VERSION = 1;

    public static final Rule<MultiHottaInstanceConfig> rule = new ObjectRule<>(MultiHottaInstanceConfig::new)
        .put("betaPath", (o, it) -> o.betaPath = it, StringRule.get())
        .put("onlinePath", (o, it) -> o.onlinePath = it, StringRule.get())
        .put("onlineModPath", (o, it) -> o.onlineModPath = it, StringRule.get())
        .put("advBranch", (o, it) -> o.advBranch = it, StringRule.get())
        .put("onlineBranch", (o, it) -> o.onlineBranch = it, StringRule.get())
        .put("onlineModBranch", (o, it) -> o.onlineModBranch = it, StringRule.get())
        .put("onlineVersion", (o, it) -> o.onlineVersion = it, StringRule.get())
        .put("disableTips", (o, it) -> o.disableTips = it, IntRule.get())
        .put("onlineBranchVersion", (o, it) -> o.onlineBranchVersion = it, IntRule.get());

    public void clearInvalidFields() {
        betaPath = MiscUtils.returnNullIfBlank(betaPath);
        onlinePath = MiscUtils.returnNullIfBlank(onlinePath);
        onlineModPath = MiscUtils.returnNullIfBlank(onlineModPath);
        advBranch = MiscUtils.returnNullIfBlank(advBranch);
        onlineBranch = MiscUtils.returnNullIfBlank(onlineBranch);
        onlineModBranch = MiscUtils.returnNullIfBlank(onlineModBranch);
        onlineVersion = MiscUtils.returnNullIfBlank(onlineVersion);
        if (onlineVersion != null) {
            var split = onlineVersion.split("\\.");
            if (split.length != 3) {
                onlineVersion = null;
            }
        }
    }

    @Override
    public JSON.Object toJson() {
        clearInvalidFields();
        var ob = new ObjectBuilder();
        if (betaPath != null) {
            ob.put("betaPath", betaPath);
        }
        if (onlinePath != null) {
            ob.put("onlinePath", onlinePath);
        }
        if (onlineModPath != null) {
            ob.put("onlineModPath", onlineModPath);
        }
        if (advBranch != null) {
            ob.put("advBranch", advBranch);
        }
        if (onlineBranch != null) {
            ob.put("onlineBranch", onlineBranch);
        }
        if (onlineModBranch != null) {
            ob.put("onlineModBranch", onlineModBranch);
        }
        if (onlineVersion != null) {
            ob.put("onlineVersion", onlineVersion);
        }
        ob.put("disableTips", disableTips);
        ob.put("onlineBranchVersion", onlineBranchVersion);
        return ob.build();
    }

    public String configValidation() {
        clearInvalidFields();
        // betaPath is nullable, not required by others
        if (onlinePath == null)
            return "onlinePath";
        // onlineModPath is nullable, not required by others
        if (advBranch == null) {
            if (betaPath != null)
                return "advBranch";
        }
        if (onlineBranch == null)
            return "onlineBranch";
        if (onlineModBranch == null) {
            if (onlineModPath != null)
                return "onlineModBranch";
        }
        if (onlineVersion == null) {
            if (onlineModPath != null)
                return "onlineVersion";
        }

        if (betaPath == null && onlineModPath == null) {
            return "betaPath&onlineModPath";
        }
        return null;
    }

    // must be called after validation
    public String resVersion() {
        if (onlineVersion == null) {
            return DEFAULT_RES_VERSION;
        }
        var split = onlineVersion.split("\\.");
        assert split.length == 3;
        return STR."\{split[0]}.\{split[1]}";
    }

    public String resSubVersion() {
        return Objects.requireNonNullElse(onlineVersion, DEFAULT_RES_SUB_VERSION);
    }
}
