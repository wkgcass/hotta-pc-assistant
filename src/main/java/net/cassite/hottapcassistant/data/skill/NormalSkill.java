package net.cassite.hottapcassistant.data.skill;

import net.cassite.hottapcassistant.data.Skill;
import net.cassite.hottapcassistant.util.AudioGroup;

public record NormalSkill(AudioGroup audio) implements Skill {
    @Override
    public boolean hitTarget() {
        return true;
    }

    @Override
    public AudioGroup getAudio() {
        return audio;
    }
}
