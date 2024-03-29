package net.cassite.hottapcassistant.entity;

import io.vproxy.vfx.entity.input.InputData;
import net.cassite.hottapcassistant.util.Utils;
import vjson.simple.SimpleString;

public class KeyBinding extends InputData {
    public String action;
    public double scale;

    public boolean isAxis;
    public int lineIndex = -1;

    @Override
    public String toString() {
        if (isAxis) {
            return "AxisMappings=(AxisName=" + new SimpleString(action).stringify() + ",Key=" + key + ",Scale=" + Utils.floatValueFormat.format(scale) + ")";
        } else {
            return "ActionMappings=(ActionName=" + new SimpleString(action).stringify() +
                ",bShift=" + Utils.boolToString(shift) + ",bCtrl=" + Utils.boolToString(ctrl) + ",bAlt=" + Utils.boolToString(alt) + ",bCmd=False" +
                ",Key=" + key + ")";
        }
    }
}
