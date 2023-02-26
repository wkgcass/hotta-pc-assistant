package net.cassite.hottapcassistant.entity;

import io.vproxy.vfx.entity.Point;
import io.vproxy.vfx.entity.Rect;
import io.vproxy.vfx.entity.input.Key;
import vjson.JSON;
import vjson.deserializer.rule.BoolRule;
import vjson.deserializer.rule.ObjectRule;
import vjson.deserializer.rule.Rule;
import vjson.deserializer.rule.StringRule;
import vjson.util.ObjectBuilder;

public class AssistantFishing {
    public Key startKey;
    public Key stopKey;
    public Key leftKey;
    public Key rightKey;
    public Key castKey;
    public boolean skipFishingPoint;
    public boolean useCastKey;
    public Point fishingPoint;
    public Point castingPoint;
    public Rect posBarRect;
    public Rect fishStaminaRect;

    public static final Rule<AssistantFishing> rule = new ObjectRule<>(AssistantFishing::new)
        .put("startKey", (o, it) -> o.startKey = new Key(it), StringRule.get())
        .put("stopKey", (o, it) -> o.stopKey = new Key(it), StringRule.get())
        .put("leftKey", (o, it) -> o.leftKey = new Key(it), StringRule.get())
        .put("rightKey", (o, it) -> o.rightKey = new Key(it), StringRule.get())
        .put("skipFishingPoint", (o, it) -> o.skipFishingPoint = it, BoolRule.get())
        .put("castKey", (o, it) -> o.castKey = new Key(it), StringRule.get())
        .put("useCastKey", (o, it) -> o.useCastKey = it, BoolRule.get())
        .put("fishingPoint", (o, it) -> o.fishingPoint = it, Point.rule)
        .put("castingPoint", (o, it) -> o.castingPoint = it, Point.rule)
        .put("posBarRect", (o, it) -> o.posBarRect = it, Rect.rule)
        .put("fishStaminaRect", (o, it) -> o.fishStaminaRect = it, Rect.rule);

    public AssistantFishing() {
    }

    public AssistantFishing(AssistantFishing f) {
        this.startKey = f.startKey;
        this.stopKey = f.stopKey;
        this.leftKey = f.leftKey;
        this.rightKey = f.rightKey;
        this.skipFishingPoint = f.skipFishingPoint;
        this.castKey = f.castKey;
        this.useCastKey = f.useCastKey;
        this.fishingPoint = f.fishingPoint;
        this.castingPoint = f.castingPoint;
        this.posBarRect = f.posBarRect;
        this.fishStaminaRect = f.fishStaminaRect;
    }

    public static AssistantFishing empty() {
        var fish = new AssistantFishing();
        fish.startKey = new Key("F8");
        fish.stopKey = new Key("F8");
        fish.leftKey = new Key("A");
        fish.rightKey = new Key("D");
        fish.castKey = new Key("One");
        return fish;
    }

    public JSON.Object toJson() {
        var ob = new ObjectBuilder()
            .put("startKey", startKey.toString())
            .put("stopKey", stopKey.toString())
            .put("leftKey", leftKey.toString())
            .put("rightKey", rightKey.toString());
        if (castKey != null) {
            ob.put("castKey", castKey.toString());
        }
        ob.put("skipFishingPoint", skipFishingPoint)
            .put("useCastKey", useCastKey);
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
