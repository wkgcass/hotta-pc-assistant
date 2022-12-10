package net.cassite.hottapcassistant.data.weapon;

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
    public void aimAttack(WeaponContext ctx) {
        super.aimAttack(ctx);
        burn(ctx);
    }

    @Override
    public void dodgeAttack(WeaponContext ctx) {
        super.dodgeAttack(ctx);
        burn(ctx);
    }

    @Override
    public void specialAttack(WeaponContext ctx) {
        super.specialAttack(ctx);
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
