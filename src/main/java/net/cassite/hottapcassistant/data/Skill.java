package net.cassite.hottapcassistant.data;

import net.cassite.hottapcassistant.util.AudioGroup;

public interface Skill {
    boolean hitTarget();

    AudioGroup getAudio();
}
