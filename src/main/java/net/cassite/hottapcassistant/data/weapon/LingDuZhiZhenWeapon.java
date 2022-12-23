package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.component.cooldown.WeaponCoolDown;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class LingDuZhiZhenWeapon extends AbstractWeapon implements Weapon {
    private long beeTime = 0;

    private final WeaponCoolDown lingDuZhiZhenBeeTimer;

    public LingDuZhiZhenWeapon() {
        super(60, 800);
        lingDuZhiZhenBeeTimer = new WeaponCoolDown(Utils.getBuffImageFromClasspath("bee"), "lingDuZhiZhenBeeTimer", I18n.get().buffName("lingDuZhiZhenBeeTimer"));
        extraIndicatorList.add(lingDuZhiZhenBeeTimer);
    }

    @Override
    public String getId() {
        return "ling-du-zhi-zhen";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.ICE;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.SUPPORT;
    }

    @Override
    protected void threadTick(long ts, long delta) {
        beeTime = Utils.subtractLongGE0(beeTime, delta);
    }

    @Override
    protected void dodge0(WeaponContext ctx) {
        if (stars >= 1) {
            if (beeTime == 0) {
                beeTime = getTotalBeeTime();
            }
        }
    }

    @Override
    protected Skill skillInstance() {
        return Skill.noHit();
    }

    @Override
    protected boolean isRevertibleSkill(WeaponContext ctx) {
        return true;
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("líng dù zhǐ zhēn");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("ling-du-zhi-zhen");
    }

    public long getBeeTime() {
        return beeTime;
    }

    public long getTotalBeeTime() {
        return 25_000;
    }

    @Override
    public void updateExtraData() {
        lingDuZhiZhenBeeTimer.setAllCoolDown(getBeeTime(), getTotalBeeTime());
    }
}
