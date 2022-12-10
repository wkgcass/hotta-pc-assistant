package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Matrix;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class SiPaKeWeapon extends AbstractWeapon implements Weapon {
    public SiPaKeWeapon() {
        super(30, 200);
    }

    @Override
    public void init(int stars, Matrix[] matrix) {
        super.init(stars, matrix);
        if (stars >= 6) {
            cooldown = 16_000;
        }
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
        return I18n.get().weaponName("sī pà kè");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("si-pa-ke");
    }
}
