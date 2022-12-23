package net.cassite.hottapcassistant.data.skill;

import net.cassite.hottapcassistant.data.Skill;

public class NormalSkill implements Skill {
    public static final NormalSkill instance = new NormalSkill();

    private NormalSkill() {
    }

    @Override
    public boolean hitTarget() {
        return true;
    }
}
