package net.cassite.hottapcassistant.data.skill;

import io.vproxy.vfx.manager.audio.AudioGroup;
import net.cassite.hottapcassistant.data.Skill;
import net.cassite.hottapcassistant.data.misc.TriggerAiLiSiSimulacra;
import net.cassite.hottapcassistant.data.misc.TriggerBuMieZhiYiStar1;
import net.cassite.hottapcassistant.data.misc.TriggerLiuQuanCheXinStar1;
import net.cassite.hottapcassistant.util.Utils;

public class ZhongJieZheSkill implements Skill, TriggerLiuQuanCheXinStar1, TriggerBuMieZhiYiStar1, TriggerAiLiSiSimulacra {
    public static final ZhongJieZheSkill instance = new ZhongJieZheSkill();

    private final AudioGroup audio = Utils.getSkillAudioGroup("xi-er-da", 3);

    private ZhongJieZheSkill() {
    }

    @Override
    public boolean hitTarget() {
        return false;
    }

    @Override
    public AudioGroup getAudio() {
        return audio;
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
