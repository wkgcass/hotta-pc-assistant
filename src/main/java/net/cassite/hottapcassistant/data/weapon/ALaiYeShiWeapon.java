package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class ALaiYeShiWeapon extends AbstractWeapon implements Weapon {
    public ALaiYeShiWeapon() {
        super(25);
    }

    @Override
    public String getId() {
        return "a-lai-ye-shi";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.THUNDER;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.CARRY;
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("ā lài yē shí");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("a-lai-ye-shi");
    }
}
