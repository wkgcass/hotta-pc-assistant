package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.component.cooldown.WeaponCoolDown;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class BingFengZhiShiWeapon extends AbstractWeapon implements Weapon {
    private long buffTime = 0;
    private final WeaponCoolDown bingFengZhiShiBuffTimer;

    public BingFengZhiShiWeapon() {
        super(12, 1000);
        bingFengZhiShiBuffTimer = new WeaponCoolDown(getImage(), I18n.get().buffName("bingFengZhiShiBuffTimer"));
        extraIndicatorList.add(bingFengZhiShiBuffTimer);
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.ICE;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.CARRY;
    }

    @Override
    protected void threadTick(long ts, long delta) {
        var buffTime = this.buffTime;
        if (buffTime > 0) {
            if (buffTime < delta) {
                this.buffTime = 0;
            } else {
                buffTime -= delta;
                this.buffTime = buffTime;
            }
        }
    }

    @Override
    protected boolean useSkill0(WeaponContext ctx) {
        boolean ok = super.useSkill0(ctx);
        if (!ok) return false;
        if (stars >= 1) {
            buff(1000);
        }
        return true;
    }

    @Override
    protected void attack0(WeaponContext ctx, AttackType type) {
        if (type == AttackType.DODGE) {
            if (stars >= 1) {
                buff(500);
            }
        } else if (type == AttackType.AIM) {
            if (stars >= 6) {
                buff(0);
            }
        }
    }

    private void buff(long extra) {
        buffTime = getTotalBuffTime() + extra;
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("bīng fēng zhī shǐ");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("bing-feng-zhi-shi");
    }

    public long getBuffTime() {
        return buffTime;
    }

    public long getTotalBuffTime() {
        return 15_000;
    }

    @Override
    public void updateExtraData() {
        bingFengZhiShiBuffTimer.setAllCoolDown(getBuffTime(), getTotalBuffTime());
    }
}
