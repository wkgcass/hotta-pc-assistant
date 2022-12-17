package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.component.cooldown.WeaponCoolDown;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.data.resonance.FireResonance;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class LingGuangWeapon extends AbstractWeapon implements Weapon, FireResonance {
    private final WeaponCoolDown lingGuangTaunt = new WeaponCoolDown(Utils.getBuffImageFromClasspath("ling-guang-taunt"), I18n.get().buffName("lingGuangTaunt"));
    private long tauntTime = 0;

    public LingGuangWeapon() {
        super(12);
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.FIRE;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.TANK;
    }

    @Override
    protected void threadTick(long ts, long delta) {
        tauntTime = Utils.subtractLongGE0(tauntTime, delta);
    }

    @Override
    public void init(WeaponContext ctx) {
        super.init(ctx);
        if (ctx.resonanceInfo.tank()) {
            extraIndicatorList.add(lingGuangTaunt);
        }
    }

    @Override
    public void alertSkillUsed0(WeaponContext ctx, Weapon w) {
        tauntTime = getTotalTauntTime();
    }

    @Override
    public void updateExtraData() {
        lingGuangTaunt.setAllCoolDown(getTauntTime(), getTotalTauntTime());
    }

    public long getTauntTime() {
        return tauntTime;
    }

    public long getTotalTauntTime() {
        return 2_500;
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("líng guāng");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("ling-guang");
    }
}
