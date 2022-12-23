package net.cassite.hottapcassistant.data;

import net.cassite.hottapcassistant.data.skill.NoHitSkill;
import net.cassite.hottapcassistant.data.skill.NormalSkill;

public interface Skill {
    static Skill normal() {
        return NormalSkill.instance;
    }

    static Skill noHit() {
        return NoHitSkill.instance;
    }

    boolean hitTarget();
}
