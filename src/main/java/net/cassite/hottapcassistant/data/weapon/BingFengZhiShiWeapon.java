package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class BingFengZhiShiWeapon extends AbstractWeapon implements Weapon {
    private long buffTime = 0;

    public BingFengZhiShiWeapon() {
        super(12, 1000);
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.ICE;
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
    public void dodgeAttack(WeaponContext ctx) {
        if (stars >= 1) {
            buff(500);
        }
    }

    @Override
    public void aimAttack(WeaponContext ctx) {
        if (stars >= 6) {
            buff(0);
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
}
