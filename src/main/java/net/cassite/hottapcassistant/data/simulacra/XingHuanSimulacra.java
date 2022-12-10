package net.cassite.hottapcassistant.data.simulacra;

import net.cassite.hottapcassistant.data.Simulacra;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class XingHuanSimulacra extends AbstractSimulacra implements Simulacra {
    private long buffTime;

    public XingHuanSimulacra() {
    }

    @Override
    protected String buildName() {
        return I18n.get().simulacraName("xīng huán");
    }

    @Override
    protected void threadTick(long ts, long delta) {
        buffTime = Utils.subtractLongGE0(buffTime, delta);
    }

    @Override
    public void alertSkillUsed(WeaponContext ctx, Weapon w) {
        var supCnt = 0;
        for (var ww : ctx.weapons) {
            if (ww.category() == WeaponCategory.SUP) {
                ++supCnt;
            }
        }
        if (supCnt < 2) {
            return;
        }
        buffTime = getTotalBuffTime();
    }

    public long getBuffTime() {
        return buffTime;
    }

    public long getTotalBuffTime() {
        return 12_000;
    }
}
