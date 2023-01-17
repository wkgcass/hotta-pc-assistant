package net.cassite.hottapcassistant.data.skill;

import io.vproxy.vfx.manager.audio.AudioGroup;
import net.cassite.hottapcassistant.data.Skill;

public record NoHitSkill(AudioGroup audio) implements Skill {
    @Override
    public boolean hitTarget() {
        return false;
    }

    @Override
    public AudioGroup getAudio() {
        return audio;
    }
}
