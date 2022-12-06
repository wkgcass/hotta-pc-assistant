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
        burn();
    }

    @Override
    public void dodgeAttack(WeaponContext ctx) {
        super.dodgeAttack(ctx);
        burn();
    }

    @Override
    public void specialAttack(WeaponContext ctx) {
        super.specialAttack(ctx);
        burn();
    }

    private void burn() {
        if (shotRemain < 0) {
            return;
        }
        burnBuff = getTotalBurnBuff();
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("sì yè shí zì (burn)");
    }
}
