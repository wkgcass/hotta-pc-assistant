package net.cassite.hottapcassistant.data.skill;

import io.vproxy.vfx.manager.audio.AudioGroup;
import net.cassite.hottapcassistant.data.Skill;

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
