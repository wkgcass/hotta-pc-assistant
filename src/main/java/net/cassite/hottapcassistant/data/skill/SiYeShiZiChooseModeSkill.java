package net.cassite.hottapcassistant.data.skill;

import net.cassite.hottapcassistant.data.Skill;

public class SiYeShiZiChooseModeSkill implements Skill {
    public static final SiYeShiZiChooseModeSkill instance = new SiYeShiZiChooseModeSkill();

    private SiYeShiZiChooseModeSkill() {
    }

    @Override
    public boolean hitTarget() {
        return false;
    }
}
