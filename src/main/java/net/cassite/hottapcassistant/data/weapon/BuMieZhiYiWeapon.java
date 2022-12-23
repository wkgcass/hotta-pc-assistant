package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.component.cooldown.WeaponCoolDown;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class BuMieZhiYiWeapon extends AbstractWeapon implements Weapon {
    private final WeaponCoolDown zhiHanChangYu = new WeaponCoolDown(Utils.getBuffImageFromClasspath("zhi-han-chang-yu"), "zhiHanChangYu", I18n.get().buffName("zhiHanChangYu"));
    private long zhiHanChangYuTime = 0;

    public BuMieZhiYiWeapon() {
        super(25, 300);
        extraIndicatorList.add(zhiHanChangYu);
    }

    @Override
    public String getId() {
        return "bu-mie-zhi-yi";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.ICE;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.CARRY;
    }

    @Override
    protected void threadTick(long ts, long delta) {
        zhiHanChangYuTime = Utils.subtractLongGE0(zhiHanChangYuTime, delta);
    }

    private long lastZhiHanChangYuTime = 0;

    @Override
    protected boolean useSkill0(WeaponContext ctx) {
        if (!super.useSkill0(ctx)) return false;
        lastZhiHanChangYuTime = zhiHanChangYuTime;
        zhiHanChangYuTime = getTotalZhiHanChangYuTime() + 300;
        return true;
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("bú miè zhī yì");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("bu-mie-zhi-yi");
    }

    @Override
    protected void alertSkillUsed0(WeaponContext ctx, Weapon w) {
        if (w.element() != WeaponElement.ICE) {
            return;
        }
        if (stars < 1) {
            return;
        }
        if (zhiHanChangYuTime == 0) {
            return;
        }
        if (w == this && lastZhiHanChangYuTime == 0) {
            return;
        }
        ctx.alertDischargeUsed(null);
    }

    public long getZhiHanChangYuTime() {
        return zhiHanChangYuTime;
    }

    public long getTotalZhiHanChangYuTime() {
        return 30_000;
    }

    @Override
    public void updateExtraData() {
        zhiHanChangYu.setAllCoolDown(zhiHanChangYuTime, getTotalZhiHanChangYuTime());
    }
}
