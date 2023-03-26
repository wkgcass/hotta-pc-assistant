package net.cassite.hottapcassistant.entity;

import io.vproxy.vfx.entity.Point;
import io.vproxy.vfx.entity.Rect;
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
    public boolean scanDischargeNativeCapture;
    public boolean scanDischargeRoughCapture;
    public boolean hideWhenMouseEnter;
    public boolean lockCDWindowPosition;
    public boolean onlyShowFirstLineBuff;
    public boolean playAudio;
    public boolean skipAudioCollection001 = true;
    public boolean applyDischargeForYingZhi;
    public boolean autoFillPianGuangLingYuSubSkill = true;
    public boolean autoDischargeForYueXingChuan = true;
    public boolean autoDischargeForJueXiang = true;
    public boolean refreshBuffRegardlessOfCDForBuMieZhiYi;
    public double lastWindowScale;

    public static final Rule<AssistantCoolDownOptions> rule = new ObjectRule<>(AssistantCoolDownOptions::new)
        .put("scanDischarge", (o, it) -> o.scanDischarge = it, BoolRule.get())
        .put("scanDischargeDebug", (o, it) -> o.scanDischargeDebug = it, BoolRule.get())
        .put("scanDischargeRect", (o, it) -> o.scanDischargeRect = it, Rect.rule)
        .put("scanDischargeCapScale", (o, it) -> o.scanDischargeCapScale = it, DoubleRule.get())
        .put("scanDischargeCriticalPoints", (o, it) -> o.scanDischargeCriticalPoints = it,
            new ArrayRule<List<Point>, Point>(ArrayList::new, List::add, Point.rule))
        .put("scanDischargeNativeCapture", (o, it) -> o.scanDischargeNativeCapture = it, BoolRule.get())
        .put("scanDischargeRoughCapture", (o, it) -> o.scanDischargeRoughCapture = it, BoolRule.get())
        .put("hideWhenMouseEnter", (o, it) -> o.hideWhenMouseEnter = it, BoolRule.get())
        .put("lockCDWindowPosition", (o, it) -> o.lockCDWindowPosition = it, BoolRule.get())
        .put("onlyShowFirstLineBuff", (o, it) -> o.onlyShowFirstLineBuff = it, BoolRule.get())
        .put("playAudio", (o, it) -> o.playAudio = it, BoolRule.get())
        .put("skipAudioCollection001", (o, it) -> o.skipAudioCollection001 = it, BoolRule.get())
        .put("applyDischargeForYingZhi", (o, it) -> o.applyDischargeForYingZhi = it, BoolRule.get())
        .put("autoFillPianGuangLingYuSubSkill", (o, it) -> o.autoFillPianGuangLingYuSubSkill = it, BoolRule.get())
        .put("autoDischargeForYueXingChuan", (o, it) -> o.autoDischargeForYueXingChuan = it, BoolRule.get())
        .put("autoDischargeForJueXiang", (o, it) -> o.autoDischargeForJueXiang = it, BoolRule.get())
        .put("refreshBuffRegardlessOfCDForBuMieZhiYi", (o, it) -> o.refreshBuffRegardlessOfCDForBuMieZhiYi = it, BoolRule.get())
        .put("lastWindowScale", (o, it) -> o.lastWindowScale = it, DoubleRule.get());

    public AssistantCoolDownOptions() {
    }

    public AssistantCoolDownOptions(AssistantCoolDownOptions that) {
        scanDischarge = that.scanDischarge;
        scanDischargeDebug = that.scanDischargeDebug;
        scanDischargeRect = that.scanDischargeRect;
        scanDischargeCapScale = that.scanDischargeCapScale;
        scanDischargeCriticalPoints = that.scanDischargeCriticalPoints;
        scanDischargeNativeCapture = that.scanDischargeNativeCapture;
        scanDischargeRoughCapture = that.scanDischargeRoughCapture;
        hideWhenMouseEnter = that.hideWhenMouseEnter;
        lockCDWindowPosition = that.lockCDWindowPosition;
        onlyShowFirstLineBuff = that.onlyShowFirstLineBuff;
        playAudio = that.playAudio;
        skipAudioCollection001 = that.skipAudioCollection001;
        applyDischargeForYingZhi = that.applyDischargeForYingZhi;
        autoFillPianGuangLingYuSubSkill = that.autoFillPianGuangLingYuSubSkill;
        autoDischargeForYueXingChuan = that.autoDischargeForYueXingChuan;
        autoDischargeForJueXiang = that.autoDischargeForJueXiang;
        refreshBuffRegardlessOfCDForBuMieZhiYi = that.refreshBuffRegardlessOfCDForBuMieZhiYi;
        lastWindowScale = that.lastWindowScale;
    }

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
        ob.put("scanDischargeNativeCapture", scanDischargeNativeCapture);
        ob.put("scanDischargeRoughCapture", scanDischargeRoughCapture);
        ob.put("hideWhenMouseEnter", hideWhenMouseEnter);
        ob.put("lockCDWindowPosition", lockCDWindowPosition);
        ob.put("onlyShowFirstLineBuff", onlyShowFirstLineBuff);
        ob.put("playAudio", playAudio);
        ob.put("skipAudioCollection001", skipAudioCollection001);
        ob.put("applyDischargeForYingZhi", applyDischargeForYingZhi);
        ob.put("autoFillPianGuangLingYuSubSkill", autoFillPianGuangLingYuSubSkill);
        ob.put("autoDischargeForYueXingChuan", autoDischargeForYueXingChuan);
        ob.put("autoDischargeForJueXiang", autoDischargeForJueXiang);
        ob.put("refreshBuffRegardlessOfCDForBuMieZhiYi", refreshBuffRegardlessOfCDForBuMieZhiYi);
        ob.put("lastWindowScale", lastWindowScale);
        return ob.build();
    }
}
