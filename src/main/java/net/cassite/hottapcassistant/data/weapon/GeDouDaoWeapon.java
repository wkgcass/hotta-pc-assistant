package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class GeDouDaoWeapon extends AbstractWeapon implements Weapon {
    public GeDouDaoWeapon() {
        super(45);
    }

    @Override
    public String getId() {
        return "ge-dou-dao";
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
    protected String buildName() {
        return I18n.get().weaponName("gě dòu dāo");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("ge-dou-dao");
    }
}
