package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class V2RongQuDunWeapon extends AbstractWeapon implements Weapon {
    private int state = 0;
    // 0 -> shield
    // 1 -> ax

    public V2RongQuDunWeapon() {
        super(25);
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.FIRE;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.DEF;
    }

    @Override
    public boolean useSkill(WeaponContext ctx) {
        if (state == 1) {
            state = 0;
            alertMatrix(ctx);
            return true;
        }
        var ok = super.useSkill(ctx);
        if (!ok) {
            return false;
        }
        state = 1;
        return true;
    }

    @Override
    public void alertWeaponSwitched(WeaponContext ctx, Weapon w) {
        state = 0;
    }

    @Override
    public long getCoolDown() {
        if (state == 1) return 0;
        return super.getCoolDown();
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("v2 róng qǖ dùn");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("v2-rong-qv-dun");
    }
}
