package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.Cursor;
import javafx.scene.image.Image;
import net.cassite.hottapcassistant.component.cooldown.WeaponCoolDown;
import net.cassite.hottapcassistant.component.cooldown.WeaponSpecialInfo;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.data.misc.TriggerLiuQuanCheXinStar1;
import net.cassite.hottapcassistant.data.resonance.IceResonance;
import net.cassite.hottapcassistant.i18n.I18n;
import io.vproxy.vfx.manager.audio.AudioGroup;
import net.cassite.hottapcassistant.util.Utils;

public class LiuQuanCheXinWeapon extends AbstractWeapon implements Weapon, IceResonance {
    private int count = 0;
    private long yongDongCD = 0;
    private final WeaponSpecialInfo liuQuanCheXinCounter;
    private final WeaponCoolDown yongDongCDIndicator;

    public LiuQuanCheXinWeapon() {
        super(30, 200);
        liuQuanCheXinCounter = new WeaponSpecialInfo(getImage(), "liuQuanCheXinCounter", I18n.get().buffName("liuQuanCheXinCounter"));
        yongDongCDIndicator = new WeaponCoolDown(Utils.getBuffImageFromClasspath("yong-dong"), "yongDongCD", I18n.get().buffName("yongDongCD"));
    }

    @Override
    public void init(WeaponContext ctx) {
        super.init(ctx);
        extraIndicatorList.add(yongDongCDIndicator);
        if (ctx.resonanceInfo.iceResonance() && stars >= 1) {
            extraInfoList.add(liuQuanCheXinCounter);
        }
        liuQuanCheXinCounter.setOnMouseClicked(e -> addCount(ctx));
        liuQuanCheXinCounter.setCursor(Cursor.HAND);
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
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("bu-po-xiao", 3);
    }

    @Override
    public String getId() {
        return "liu-quan-che-xin";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.FROST;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.DEFENSE;
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
    protected void alertSkillUsed0(WeaponContext ctx, Weapon w, Skill skill) {
        if (stars < 1) {
            return;
        }
        boolean triggerred = ctx.resonanceInfo.iceResonance();
        if (!triggerred) {
            return;
        }
        if (skill instanceof TriggerLiuQuanCheXinStar1) {
            if (!((TriggerLiuQuanCheXinStar1) skill).triggerLiuQuanCheXinStar1()) {
                return;
            }
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
        alertSkillUsed0(ctx, null, null);
    }

    public long getYongDongCD() {
        return yongDongCD;
    }

    public long getTotalYongDongCD() {
        if (ctx.resonanceInfo.tank() && stars >= 3) {
            return 5_000;
        }
        return 10_000;
    }

    @Override
    public void updateExtraData() {
        liuQuanCheXinCounter.setText(getCount() + "");
        yongDongCDIndicator.setAllCoolDown(getYongDongCD(), getTotalYongDongCD());
    }
}
