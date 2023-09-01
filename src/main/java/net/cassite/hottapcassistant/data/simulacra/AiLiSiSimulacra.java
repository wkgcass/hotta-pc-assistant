package net.cassite.hottapcassistant.data.simulacra;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.component.cooldown.WeaponCoolDown;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.data.misc.TriggerAiLiSiSimulacra;
import net.cassite.hottapcassistant.data.misc.TriggerBuMieZhiYiStar1;
import net.cassite.hottapcassistant.data.weapon.BuMieZhiYiWeapon;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class AiLiSiSimulacra extends AbstractSimulacra implements Simulacra {
    private final WeaponCoolDown buff = new WeaponCoolDown(this::getImage, 1.25, "aiLiSiSimulacraBuff", I18n.get().buffName("aiLiSiSimulacraBuff"));
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
    protected Image buildImage() {
        return Utils.getBuffImageFromClasspath("ai-li-si-simulacra").get();
    }

    @Override
    public void alertSkillUsed(WeaponContext ctx, Weapon w, Skill skill) {
        boolean triggerBuffTime = true;
        if (skill instanceof TriggerAiLiSiSimulacra) {
            triggerBuffTime = ((TriggerAiLiSiSimulacra) skill).triggerAiLiSiSimulacra();
        }
        if (triggerBuffTime) {
            buffTime = getTotalBuffTime();
        }
        if (w.element() != WeaponElement.PHYSICAL && w.element() != WeaponElement.FLAME && w.element() != WeaponElement.VOLT) {
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
        if (skill instanceof TriggerBuMieZhiYiStar1) {
            if (!((TriggerBuMieZhiYiStar1) skill).triggerBuMieZhiYiStar1()) {
                return;
            }
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
