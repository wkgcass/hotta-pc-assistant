package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.component.cooldown.WeaponCoolDown;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class YeQueZhiYuWeapon extends AbstractWeapon implements Weapon {
    private final WeaponCoolDown star5 = new WeaponCoolDown(Utils.getWeaponImageFromClasspath("ye-que-zhi-yu"), "yeQueZhiYuStar5", I18n.get().buffName("yeQueZhiYuStar5"));
    private long star5Time = 0;

    public YeQueZhiYuWeapon() {
        super(12, 1000);
    }

    @Override
    public String getId() {
        return "ye-que-zhi-yu";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.PHYSICS;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.CARRY;
    }

    @Override
    public void init(int stars, Matrix[] matrix) {
        super.init(stars, matrix);
        if (stars >= 5) {
            extraIndicatorList.add(star5);
        }
    }

    @Override
    protected void threadTick(long ts, long delta) {
        star5Time = Utils.subtractLongGE0(star5Time, delta);
    }

    @Override
    protected void attack0(WeaponContext ctx, AttackType type) {
        if (type == AttackType.DODGE) {
            if (stars >= 5) {
                star5Time = getTotalStar5Time() + 500;
            }
        }
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("yè què zhī yǔ");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("ye-que-zhi-yu");
    }

    public long getStar5Time() {
        return star5Time;
    }

    public long getTotalStar5Time() {
        return 7000;
    }

    @Override
    public void updateExtraData() {
        star5.setAllCoolDown(star5Time, getTotalStar5Time());
    }
}
