package net.cassite.hottapcassistant.entity;

import vjson.JSON;
import vjson.deserializer.rule.*;
import vjson.util.ObjectBuilder;

import java.util.ArrayList;
import java.util.List;

public class AssistantCoolDownOptions implements WeaponArgs {
    public boolean scanDischarge;
    public boolean scanDischargeDebug;
    public Rect scanDischargeRect;
    public double scanDischargeCapScale;
    public List<Point> scanDischargeCriticalPoints;
    public boolean applyDischargeForYingZhi;
    public boolean playAudio;
    public boolean autoFillPianGuangLingYuSubSkill;
    public double lastWindowScale;

    public static final Rule<AssistantCoolDownOptions> rule = new ObjectRule<>(AssistantCoolDownOptions::new)
        .put("scanDischarge", (o, it) -> o.scanDischarge = it, BoolRule.get())
        .put("scanDischargeDebug", (o, it) -> o.scanDischargeDebug = it, BoolRule.get())
        .put("scanDischargeRect", (o, it) -> o.scanDischargeRect = it, Rect.rule)
        .put("scanDischargeCapScale", (o, it) -> o.scanDischargeCapScale = it, DoubleRule.get())
        .put("scanDischargeCriticalPoints", (o, it) -> o.scanDischargeCriticalPoints = it,
            new ArrayRule<List<Point>, Point>(ArrayList::new, List::add, Point.rule))
        .put("applyDischargeForYingZhi", (o, it) -> o.applyDischargeForYingZhi = it, BoolRule.get())
        .put("playAudio", (o, it) -> o.playAudio = it, BoolRule.get())
        .put("autoFillPianGuangLingYuSubSkill", (o, it) -> o.autoFillPianGuangLingYuSubSkill = it, BoolRule.get())
        .put("lastWindowScale", (o, it) -> o.lastWindowScale = it, DoubleRule.get());

    public static AssistantCoolDownOptions empty() {
        return new AssistantCoolDownOptions();
    }

    public boolean scanDischargeEnabled() {
        return scanDischarge && canEnableDischarge();
    }

    public boolean canEnableDischarge() {
        return scanDischargeRect != null && scanDischargeCapScale != 0 && scanDischargeCriticalPoints != null;
    }

    public JSON.Object toJson() {
        var ob = new ObjectBuilder()
            .put("scanDischarge", scanDischarge)
            .put("scanDischargeDebug", scanDischargeDebug);
        if (scanDischargeRect != null) {
            ob.putInst("scanDischargeRect", scanDischargeRect.toJson());
        }
        ob.put("scanDischargeCapScale", scanDischargeCapScale);
        if (scanDischargeCriticalPoints != null) {
            ob.putArray("scanDischargeCriticalPoints", a -> scanDischargeCriticalPoints.forEach(e -> a.addInst(e.toJson())));
        }
        ob.put("applyDischargeForYingZhi", applyDischargeForYingZhi);
        ob.put("playAudio", playAudio);
        ob.put("autoFillPianGuangLingYuSubSkill", autoFillPianGuangLingYuSubSkill);
        ob.put("lastWindowScale", lastWindowScale);
        return ob.build();
    }
}
