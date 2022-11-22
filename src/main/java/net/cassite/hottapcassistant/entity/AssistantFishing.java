package net.cassite.hottapcassistant.entity;

import vjson.JSON;
import vjson.deserializer.rule.ObjectRule;
import vjson.deserializer.rule.Rule;
import vjson.deserializer.rule.StringRule;
import vjson.util.ObjectBuilder;

public class AssistantFishing {
    public Key startKey;
    public Key stopKey;
    public Key leftKey;
    public Key rightKey;
    public Point fishingPoint;
    public Point castingPoint;
    public Rect posBarRect;
    public Rect fishStaminaRect;

    public static final Rule<AssistantFishing> rule = new ObjectRule<>(AssistantFishing::new)
        .put("startKey", (o, it) -> o.startKey = new Key(it), StringRule.get())
        .put("stopKey", (o, it) -> o.stopKey = new Key(it), StringRule.get())
        .put("leftKey", (o, it) -> o.leftKey = new Key(it), StringRule.get())
        .put("rightKey", (o, it) -> o.rightKey = new Key(it), StringRule.get())
        .put("fishingPoint", (o, it) -> o.fishingPoint = it, Point.rule)
        .put("castingPoint", (o, it) -> o.castingPoint = it, Point.rule)
        .put("posBarRect", (o, it) -> o.posBarRect = it, Rect.rule)
        .put("fishStaminaRect", (o, it) -> o.fishStaminaRect = it, Rect.rule);

    public static AssistantFishing empty() {
        var fish = new AssistantFishing();
        fish.startKey = new Key("F9");
        fish.stopKey = new Key("F9");
        fish.leftKey = new Key("A");
        fish.rightKey = new Key("D");
        return fish;
    }

    public JSON.Object toJson() {
        var ob = new ObjectBuilder()
            .put("startKey", startKey.toString())
            .put("stopKey", stopKey.toString())
            .put("leftKey", leftKey.toString())
            .put("rightKey", rightKey.toString());
        if (fishingPoint != null)
            ob.putInst("fishingPoint", fishingPoint.toJson());
        if (castingPoint != null)
            ob.putInst("castingPoint", castingPoint.toJson());
        if (posBarRect != null)
            ob.putInst("posBarRect", posBarRect.toJson());
        if (fishStaminaRect != null)
            ob.putInst("fishStaminaRect", fishStaminaRect.toJson());
        return ob.build();
    }

    public boolean isValid() {
        return fishingPoint != null && castingPoint != null && posBarRect != null && fishStaminaRect != null;
    }

    public void reset() {
        fishingPoint = null;
        castingPoint = null;
        posBarRect = null;
        fishStaminaRect = null;
    }
}
