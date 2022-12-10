package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class FuHeGongWeapon extends AbstractWeapon implements Weapon {
    public FuHeGongWeapon() {
        super(12, 1000);
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.FIRE;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.ATK;
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("fù hé gōng");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("fu-he-gong");
    }
}
