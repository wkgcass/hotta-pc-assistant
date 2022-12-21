package net.cassite.hottapcassistant.entity;

import vjson.JSON;
import vjson.deserializer.rule.IntRule;
import vjson.deserializer.rule.ObjectRule;
import vjson.deserializer.rule.Rule;
import vjson.deserializer.rule.StringRule;
import vjson.util.ObjectBuilder;

public class GameAssistant {
    public GameVersion version;
    public int fullscreenMode;
    public int resolutionSizeX;
    public int resolutionSizeY;

    public static final Rule<GameAssistant> rule = new ObjectRule<>(GameAssistant::new)
        .put("version", (o, it) -> o.version = GameVersion.valueOf(it), StringRule.get())
        .put("fullscreenMode", (o, it) -> o.fullscreenMode = it, IntRule.get())
        .put("resolutionSizeX", (o, it) -> o.resolutionSizeX = it, IntRule.get())
        .put("resolutionSizeY", (o, it) -> o.resolutionSizeY = it, IntRule.get());

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
        ob.put("fullscreenMode", fullscreenMode);
        ob.put("resolutionSizeX", resolutionSizeX);
        ob.put("resolutionSizeY", resolutionSizeY);
        return ob.build();
    }
}
