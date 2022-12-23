package net.cassite.hottapcassistant.data.skill;

import net.cassite.hottapcassistant.data.Skill;
import net.cassite.hottapcassistant.data.misc.TriggerBuMieZhiYiStar1;
import net.cassite.hottapcassistant.data.misc.TriggerLiuQuanCheXinStar1;

public class ZhongJieZheSkill implements Skill, TriggerLiuQuanCheXinStar1, TriggerBuMieZhiYiStar1 {
    public static final ZhongJieZheSkill instance = new ZhongJieZheSkill();

    private ZhongJieZheSkill() {
    }

    @Override
    public boolean hitTarget() {
        return false;
    }

    @Override
    public boolean triggerLiuQuanCheXinStar1() {
        return false;
    }

    @Override
    public boolean triggerBuMieZhiYiStar1() {
        return false;
    }
}
