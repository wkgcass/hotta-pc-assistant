package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.data.resonance.ThunderResonance;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class QiMingXingWeapon extends AbstractWeapon implements Weapon, ThunderResonance {
    public QiMingXingWeapon() {
        super(25, 500);
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("qǐ míng xīng");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("qi-ming-xing");
    }

    @Override
    public String getId() {
        return "qi-ming-xing";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.THUNDER;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.SUPPORT;
    }

    @Override
    protected boolean isRevertibleSkill(WeaponContext ctx) {
        return true;
    }
}
