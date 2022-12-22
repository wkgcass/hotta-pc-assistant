package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class QiangWeiZhiFengWeapon extends AbstractWeapon implements Weapon {
    public QiangWeiZhiFengWeapon() {
        super(30, 800);
    }

    @Override
    public String getId() {
        return "qiang-wei-zhi-feng";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.ICE;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.TANK;
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("qiáng wēi zhī fēng");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("qiang-wei-zhi-feng");
    }
}
