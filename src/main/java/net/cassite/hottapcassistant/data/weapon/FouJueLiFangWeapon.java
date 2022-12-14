package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Matrix;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class FouJueLiFangWeapon extends AbstractWeapon implements Weapon {
    public FouJueLiFangWeapon() {
        super(60, 700);
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.FIRE;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.SUPPORT;
    }

    @Override
    public void init(int stars, Matrix[] matrix) {
        super.init(stars, matrix);
        if (stars >= 3) {
            cooldown = 30_700;
        }
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("fǒu jué lì fāng");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("fou-jue-li-fang");
    }
}
