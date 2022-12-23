package net.cassite.hottapcassistant.data.skill;

import net.cassite.hottapcassistant.data.Skill;

public class ChuDongZhongJiAttackSkill implements Skill {
    public static final ChuDongZhongJiAttackSkill instance = new ChuDongZhongJiAttackSkill();

    private ChuDongZhongJiAttackSkill() {
    }

    @Override
    public boolean hitTarget() {
        return true;
    }
}
