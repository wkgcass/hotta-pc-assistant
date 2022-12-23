package net.cassite.hottapcassistant.data.skill;

import net.cassite.hottapcassistant.data.Skill;

public class NoHitSkill implements Skill {
    public static final NoHitSkill instance = new NoHitSkill();

    private NoHitSkill() {
    }

    @Override
    public boolean hitTarget() {
        return false;
    }
}
