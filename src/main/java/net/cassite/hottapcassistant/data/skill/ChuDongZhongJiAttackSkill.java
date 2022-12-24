package net.cassite.hottapcassistant.data.skill;

import net.cassite.hottapcassistant.data.Skill;
import net.cassite.hottapcassistant.util.AudioGroup;
import net.cassite.hottapcassistant.util.Utils;

public class ChuDongZhongJiAttackSkill implements Skill {
    public static final ChuDongZhongJiAttackSkill instance = new ChuDongZhongJiAttackSkill();

    private final AudioGroup audio = Utils.getSkillAudioGroup("xi", 3);

    private ChuDongZhongJiAttackSkill() {
    }

    @Override
    public boolean hitTarget() {
        return true;
    }

    @Override
    public AudioGroup getAudio() {
        return audio;
    }
}
