package net.cassite.hottapcassistant.data.simulacra;

import net.cassite.hottapcassistant.component.cooldown.WeaponCoolDown;
import net.cassite.hottapcassistant.data.Simulacra;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.data.weapon.BuMieZhiYiWeapon;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class AiLiSiSimulacra extends AbstractSimulacra implements Simulacra {
    private final WeaponCoolDown buff = new WeaponCoolDown(Utils.getBuffImageFromClasspath("ai-li-si-simulacra"), 1.25, "aiLiSiSimulacraBuff", I18n.get().buffName("aiLiSiSimulacraBuff"));
    private long buffTime = 0;

    public AiLiSiSimulacra() {
        extraIndicatorList.add(buff);
    }

    @Override
    protected void threadTick(long ts, long delta) {
        buffTime = Utils.subtractLongGE0(buffTime, delta);
    }

    @Override
    protected String buildName() {
        return I18n.get().simulacraName("ài lì sī");
    }

    @Override
    public void alertSkillUsed(WeaponContext ctx, Weapon w) {
        buffTime = getTotalBuffTime();
        if (w.element() != WeaponElement.PHYSICS && w.element() != WeaponElement.FIRE && w.element() != WeaponElement.THUNDER) {
            return;
        }
        var bmzyOpt = ctx.weapons.stream().filter(ww -> ww instanceof BuMieZhiYiWeapon).findAny();
        if (bmzyOpt.isEmpty()) {
            return;
        }
        var bmzy = bmzyOpt.get();
        if (bmzy.getStars() < 1) {
            return;
        }
        if (((BuMieZhiYiWeapon) bmzy).getZhiHanChangYuTime() == 0) {
            return;
        }
        ctx.alertDischargeUsed(null);
    }

    public long getBuffTime() {
        return buffTime;
    }

    public long getTotalBuffTime() {
        return 18_000;
    }

    @Override
    public void updateExtraData() {
        buff.setAllCoolDown(buffTime, getTotalBuffTime());
    }
}
