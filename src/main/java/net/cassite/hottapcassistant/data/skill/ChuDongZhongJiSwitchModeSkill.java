package net.cassite.hottapcassistant.data.skill;

import io.vproxy.vfx.manager.audio.AudioGroup;
import net.cassite.hottapcassistant.data.Skill;
import net.cassite.hottapcassistant.data.misc.TriggerAiLiSiSimulacra;
import net.cassite.hottapcassistant.data.misc.TriggerBuMieZhiYiStar1;
import net.cassite.hottapcassistant.data.misc.TriggerLiuQuanCheXinStar1;

public class ChuDongZhongJiSwitchModeSkill implements Skill, TriggerLiuQuanCheXinStar1, TriggerBuMieZhiYiStar1, TriggerAiLiSiSimulacra {
    public static final ChuDongZhongJiSwitchModeSkill instance = new ChuDongZhongJiSwitchModeSkill();

    private ChuDongZhongJiSwitchModeSkill() {
    }

    @Override
    public boolean hitTarget() {
        return false;
    }

    @Override
    public AudioGroup getAudio() {
        return null;
    }

    @Override
    public boolean triggerLiuQuanCheXinStar1() {
        return false;
    }

    @Override
    public boolean triggerBuMieZhiYiStar1() {
        return false;
    }

    @Override
    public boolean triggerAiLiSiSimulacra() {
        return false;
    }
}
