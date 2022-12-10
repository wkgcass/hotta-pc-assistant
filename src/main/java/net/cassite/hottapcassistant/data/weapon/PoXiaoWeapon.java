package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
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
    public WeaponElement element() {
        return WeaponElement.PHYSICS;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.DEF;
    }

    @Override
    protected void threadTick(long ts, long delta) {
        humanCD = Utils.subtractLongGE0(humanCD, delta);
        mechaCD = Utils.subtractLongGE0(mechaCD, delta);
    }

    @Override
    public boolean useSkill(WeaponContext ctx) {
        if (!super.useSkill(ctx)) {
            return false;
        }
        if (state == 0) {
            state = 1;
            humanCD = cd;
            cd = mechaCD;
        } else {
            mechaCD = cd;
        }
        return true;
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
    public void alertWeaponSwitched(WeaponContext ctx, Weapon w) {
        state = 0;
        cd = humanCD;
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
}
