package net.cassite.hottapcassistant.data.skill;

import net.cassite.hottapcassistant.data.Skill;
import net.cassite.hottapcassistant.util.AudioGroup;
import net.cassite.hottapcassistant.util.Utils;

public class SiYeShiZiChooseModeSkill implements Skill {
    public static final SiYeShiZiChooseModeSkill instance = new SiYeShiZiChooseModeSkill();

    private final AudioGroup audio = Utils.getSkillAudioGroup("an-na-bei-la", 5);

    private SiYeShiZiChooseModeSkill() {
    }

    @Override
    public boolean hitTarget() {
        return false;
    }

    @Override
    public AudioGroup getAudio() {
        return audio;
    }
}
