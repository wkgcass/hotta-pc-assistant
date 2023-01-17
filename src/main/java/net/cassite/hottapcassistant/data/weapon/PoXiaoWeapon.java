package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.i18n.I18n;
import io.vproxy.vfx.manager.audio.AudioGroup;
import net.cassite.hottapcassistant.util.Utils;

public class PoXiaoWeapon extends AbstractWeapon implements Weapon {
    private int state = 0;
    // 0 -> human
    // 1 -> mecha
    private long humanCD = 0;
    private long mechaCD = 0;

    public PoXiaoWeapon() {
        super(20);
    }

    @Override
    public String getId() {
        return "po-xiao";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.PHYSICS;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.TANK;
    }

    @Override
    protected void threadTick(long ts, long delta) {
        humanCD = Utils.subtractLongGE0(humanCD, delta);
        mechaCD = Utils.subtractLongGE0(mechaCD, delta);
    }

    @Override
    protected Skill useSkill0(WeaponContext ctx) {
        var skill = super.useSkill0(ctx);
        if (skill == null) {
            return null;
        }
        if (state == 0) {
            state = 1;
            humanCD = cd;
            cd = mechaCD;
        } else {
            mechaCD = cd;
        }
        return skill;
    }

    @Override
    public double[] getAllCoolDown() {
        if (humanCD == 0 && mechaCD == 0)
            return null;
        if (humanCD == 0) {
            return new double[]{mechaCD / 20_000d};
        } else if (mechaCD == 0) {
            return new double[]{humanCD / 20_000d};
        }
        if (state == 0) {
            return new double[]{humanCD / 20_000d, mechaCD / 20_000d};
        } else {
            return new double[]{mechaCD / 20_000d, humanCD / 20_000d};
        }
    }

    @Override
    protected void alertWeaponSwitched0(WeaponContext ctx, Weapon w, boolean discharge) {
        if (w == this && discharge) {
            state = 1;
            cd = mechaCD;
        } else if (w != null) {
            state = 0;
            cd = humanCD;
        }
    }

    @Override
    public void resetCoolDown() {
        super.resetCoolDown();
        humanCD = 0;
        mechaCD = 0;
    }

    @Override
    public void decreaseCoolDown(long time) {
        super.decreaseCoolDown(time);
        humanCD = Utils.subtractLongGE0(humanCD, time);
        mechaCD = Utils.subtractLongGE0(mechaCD, time);
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("pò xiǎo");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("po-xiao");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("ma-ke", 3);
    }
}
