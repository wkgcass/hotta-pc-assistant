package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class ShengHenQuanZhangWeapon extends AbstractWeapon implements Weapon {
    public ShengHenQuanZhangWeapon() {
        super(60, 800);
    }

    @Override
    public String getId() {
        return "sheng-hen-quan-zhang";
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
    public boolean skillHitTarget() {
        return false;
    }

    @Override
    protected boolean isRevertibleSkill(WeaponContext ctx) {
        return true;
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("shèng hén quán zhàng");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("sheng-hen-quan-zhang");
    }
}