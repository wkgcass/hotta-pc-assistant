package net.cassite.hottapcassistant.data.weapon;

import net.cassite.hottapcassistant.data.AttackType;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;

public class GasSiYeShiZiWeapon extends AbstractSiYeShiZiWeapon {
    @Override
    protected void attack0(WeaponContext ctx, AttackType type) {
        super.attack0(ctx, type);
        if (type == AttackType.AIM || type == AttackType.SPECIAL)
            cd(ctx, 700);
        else if (type == AttackType.DODGE)
            cd(ctx, 1400); // gas blast will also decrease cd
    }

    private void cd(WeaponContext ctx, long decrease) {
        if (shotRemain <= 0) { // the last shot will not decrease cd
            return;
        }
        for (var w : ctx.weapons) {
            if (w == this) {
                continue;
            }
            if (w.element() == WeaponElement.FIRE) {
                w.decreaseCoolDown(decrease);
            }
        }
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("sì yè shí zì (gas)");
    }
}
