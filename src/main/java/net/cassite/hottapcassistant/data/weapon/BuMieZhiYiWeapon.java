package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class BuMieZhiYiWeapon extends AbstractWeapon implements Weapon {
    public BuMieZhiYiWeapon() {
        super(25, 300);
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
    protected String buildName() {
        return I18n.get().weaponName("bú miè zhī yì");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("bu-mie-zhi-yi");
    }
}
