package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.data.resonance.FireResonance;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class SiPaKeWeapon extends AbstractWeapon implements Weapon, FireResonance {
    public SiPaKeWeapon() {
        super(30, 400);
    }

    @Override
    public void init(int stars, Matrix[] matrix) {
        super.init(stars, matrix);
        if (stars >= 1) {
            cooldown = 24_400;
        }
        if (stars >= 6) {
            cooldown = 16_400;
        }
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.FIRE;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.CARRY;
    }

    @Override
    protected boolean useSkill0(WeaponContext ctx) {
        if (!super.useSkill0(ctx)) {
            return false;
        }
        var settle = ctx.getBurnSettleContext();
        settle.trigger(ctx, 6_000);
        return true;
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
