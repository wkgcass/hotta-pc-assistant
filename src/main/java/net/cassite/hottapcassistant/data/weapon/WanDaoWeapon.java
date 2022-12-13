package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class WanDaoWeapon extends AbstractWeapon implements Weapon {
    private int count = 3;

    public WanDaoWeapon() {
        super(20);
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.PHYSICS;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.SUPPORT;
    }

    @Override
    protected void threadTick(long ts, long delta) {
        if (!ctx.resonanceInfo.sup()) return;
        if (cd == 0) {
            if (count < 3) {
                ++count;
            }
            if (count < 3) {
                cd = cooldown;
            }
        }
    }

    @Override
    protected boolean useSkill0(WeaponContext ctx) {
        if (ctx.resonanceInfo.sup()) {
            if (count > 0) {
                --count;
                if (cd == 0) {
                    return super.useSkill0(ctx);
                } else {
                    return true;
                }
            }
        }
        return super.useSkill0(ctx);
    }

    @Override
    public long getCoolDown() {
        if (ctx.resonanceInfo.sup()) {
            if (count > 0) return 0;
        }
        return super.getCoolDown();
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("wǎn dǎo");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("wan-dao");
    }

    public int getCount() {
        return count;
    }

    public void resetCount() {
        count = 3;
        cd = 0;
    }
}
