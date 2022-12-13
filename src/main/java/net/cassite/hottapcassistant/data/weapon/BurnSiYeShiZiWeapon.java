package net.cassite.hottapcassistant.data.weapon;

import net.cassite.hottapcassistant.data.AttackType;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.i18n.I18n;

public class BurnSiYeShiZiWeapon extends AbstractSiYeShiZiWeapon {
    public long getBurnBuff() {
        return burnBuff;
    }

    public long getTotalBurnBuff() {
        return 13_000;
    }

    @Override
    protected void attack0(WeaponContext ctx, AttackType type) {
        super.attack0(ctx, type);
        if (type == AttackType.AIM || type == AttackType.DODGE || type == AttackType.SPECIAL)
            burn(ctx);
    }

    private void burn(WeaponContext ctx) {
        if (shotRemain < 0) {
            return;
        }
        var burnBuff = getTotalBurnBuff();
        burnBuff = ctx.calcExtraBurnTime(burnBuff);
        this.burnBuff = burnBuff;
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("sì yè shí zì (burn)");
    }
}
