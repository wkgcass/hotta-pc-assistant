package net.cassite.hottapcassistant.data;

import net.cassite.hottapcassistant.data.weapon.AbstractSiYeShiZiWeapon;
import net.cassite.hottapcassistant.util.Utils;

public class BurnSettleContext {
    private long lastTotalCD;
    private long cd;
    private int settleByOpticalSpaceCountRemain = 0;

    public long getCd() {
        return cd;
    }

    public long getLastTotalCD() {
        return lastTotalCD;
    }

    private boolean canTrigger() {
        return cd == 0;
    }

    public void trigger(WeaponContext ctx, long cd) {
        if (!canTrigger()) return;
        this.cd = cd;
        this.lastTotalCD = cd;
        handleOpticalSpace(ctx);
    }

    private void handleOpticalSpace(WeaponContext ctx) {
        AbstractSiYeShiZiWeapon sysz = null;
        for (var w : ctx.weapons) {
            if (w instanceof AbstractSiYeShiZiWeapon) {
                sysz = (AbstractSiYeShiZiWeapon) w;
                break;
            }
        }
        if (sysz == null) {
            return;
        }
        if (sysz.getOpticalSpaceTime() > 0) {
            settleByOpticalSpaceCountRemain = 2;
        }
    }

    public void tick(@SuppressWarnings("unused") WeaponContext ctx, long delta) {
        if (cd == 0) return;
        cd = Utils.subtractLongGE0(cd, delta);
        if (cd != 0) return;
        if (settleByOpticalSpaceCountRemain > 0) {
            --settleByOpticalSpaceCountRemain;
            cd = lastTotalCD;
        }
    }
}
