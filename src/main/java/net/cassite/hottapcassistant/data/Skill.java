package net.cassite.hottapcassistant.data;

import io.vproxy.vfx.manager.audio.AudioGroup;

public interface Skill {
    boolean hitTarget();

    AudioGroup getAudio();
}
