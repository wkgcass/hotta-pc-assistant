package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class LiuQuanCheXinWeapon extends AbstractWeapon implements Weapon {
    private int count = 0;
    private long yongDongCD = 0;

    public LiuQuanCheXinWeapon() {
        super(30, 200);
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("liú quán chè xīn");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("liu-quan-che-xin");
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.ICE;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.TANK;
    }

    @Override
    protected void attack0(WeaponContext ctx, AttackType type) {
        if (type == AttackType.AIM) {
            yongDong();
        }
    }

    @Override
    protected void threadTick(long ts, long delta) {
        yongDongCD = Utils.subtractLongGE0(yongDongCD, delta);
    }

    @Override
    protected void alertSkillUsed0(WeaponContext ctx, Weapon w) {
        if (stars < 1) {
            return;
        }
        boolean triggerred = ctx.resonanceInfo.iceResonance();
        if (!triggerred) {
            return;
        }
        int n = count + 1;
        if (n >= 5) {
            this.count = 0;
            ctx.resetCoolDown();
        } else {
            this.count = n;
        }
    }

    @Override
    protected void alertWeaponSwitched0(WeaponContext ctx, Weapon w, boolean discharge) {
        if (w != this) return;
        if (!discharge) return;
        yongDong();
    }

    private void yongDong() {
        if (yongDongCD == 0)
            yongDongCD = getTotalYongDongCD();
    }

    public int getCount() {
        return count;
    }

    public void addCount(WeaponContext ctx) {
        alertSkillUsed(ctx, null);
    }

    public long getYongDongCD() {
        return yongDongCD;
    }

    public long getTotalYongDongCD() {
        if (ctx.resonanceInfo.def() && stars >= 3) {
            return 5_000;
        }
        return 10_000;
    }
}
