package net.cassite.hottapcassistant.data.simulacra;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.component.cooldown.WeaponCoolDown;
import net.cassite.hottapcassistant.data.Simulacra;
import net.cassite.hottapcassistant.data.Skill;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class XingHuanSimulacra extends AbstractSimulacra implements Simulacra {
    private long buffTime;

    private final WeaponCoolDown xingHuanSimulacraTimer;

    public XingHuanSimulacra() {
        xingHuanSimulacraTimer = new WeaponCoolDown(this::getImage, 1.25, "xingHuanSimulacraTimer", I18n.get().buffName("xingHuanSimulacraTimer"));
    }

    @Override
    public void init(WeaponContext ctx) {
        super.init(ctx);
        if (ctx.resonanceInfo.support()) {
            extraIndicatorList.add(xingHuanSimulacraTimer);
        }
    }

    @Override
    protected String buildName() {
        return I18n.get().simulacraName("xīng huán");
    }

    @Override
    protected Image buildImage() {
        return Utils.getSimulacraImageFromClasspath("xing-huan").get();
    }

    @Override
    protected void threadTick(long ts, long delta) {
        buffTime = Utils.subtractLongGE0(buffTime, delta);
    }

    @Override
    public void alertSkillUsed(WeaponContext ctx, Weapon w, Skill skill) {
        if (!ctx.resonanceInfo.support()) {
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

    @Override
    public void updateExtraData() {
        xingHuanSimulacraTimer.setAllCoolDown(getBuffTime(), getTotalBuffTime());
    }
}
