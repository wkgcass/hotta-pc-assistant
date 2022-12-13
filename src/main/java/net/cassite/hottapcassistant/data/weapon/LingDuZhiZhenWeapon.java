package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class LingDuZhiZhenWeapon extends AbstractWeapon implements Weapon {
    private long beeTime = 0;

    public LingDuZhiZhenWeapon() {
        super(60);
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.ICE;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.SUPPORT;
    }

    @Override
    protected void threadTick(long ts, long delta) {
        beeTime = Utils.subtractLongGE0(beeTime, delta);
    }

    @Override
    protected void dodge0(WeaponContext ctx) {
        if (stars >= 1) {
            if (beeTime == 0) {
                beeTime = getTotalBeeTime();
            }
        }
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("líng dù zhǐ zhēn");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("ling-du-zhi-zhen");
    }

    public long getBeeTime() {
        return beeTime;
    }

    public long getTotalBeeTime() {
        return 25_000;
    }
}
