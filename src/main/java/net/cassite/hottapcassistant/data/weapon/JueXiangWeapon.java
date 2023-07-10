package net.cassite.hottapcassistant.data.weapon;

import io.vproxy.vfx.manager.audio.AudioGroup;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import net.cassite.hottapcassistant.component.cooldown.WeaponCoolDown;
import net.cassite.hottapcassistant.component.cooldown.WeaponSpecialInfo;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.entity.AssistantCoolDownOptions;
import net.cassite.hottapcassistant.entity.WeaponArgs;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class JueXiangWeapon extends AbstractWeapon implements Weapon {
    private final WeaponCoolDown haiLaZhiYongTime = new WeaponCoolDown(Utils.getBuffImageFromClasspath("hai-la-zhi-yong"), "hai-la-zhi-yong", I18n.get().buffName("haiLaZhiYongTime"));
    private final WeaponSpecialInfo huiXiangCount = new WeaponSpecialInfo(Utils.getBuffImageFromClasspath("hui-xiang"), "hui-xiang", I18n.get().buffName("huiXiangCount"));

    private boolean cdCannotChange = false;

    private long buffTime = 0;
    private int hxCount = 0;
    private boolean autoDischargeForJueXiang;

    public JueXiangWeapon() {
        super(30);
        extraIndicatorList.add(haiLaZhiYongTime);
        extraInfoList.add(huiXiangCount);
        huiXiangCount.setOnMouseClicked(e -> {
            hxCount += 1;
            if (hxCount > 4) {
                hxCount = 0;
            }
        });
        huiXiangCount.setCursor(Cursor.HAND);
    }

    @Override
    public String getId() {
        return "jue-xiang";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.VOLT;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.DPS;
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("jué xiǎng");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("jue-xiang");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("lei-bi-li-ya", 5);
    }

    @Override
    public void init(WeaponContext ctx) {
        super.init(ctx);
        if (MengZhangWeapon.hasMengZhangCDDecreasingAndDisableCDChanging(ctx)) {
            totalCoolDown = totalCoolDown * 2 / 3;
            cdCannotChange = true;
        }
    }

    @Override
    public void init(WeaponArgs args) {
        if (!(args instanceof AssistantCoolDownOptions opts)) {
            return;
        }
        autoDischargeForJueXiang = opts.autoDischargeForJueXiang;
    }

    @Override
    protected void threadTick(long ts, long delta) {
        buffTime = Utils.subtractLongGE0(buffTime, delta);
    }

    @Override
    protected void alertWeaponSwitched0(WeaponContext ctx, Weapon w, boolean discharge) {
        if (!discharge && !autoDischargeForJueXiang) {
            return;
        }
        if (w != this) {
            return;
        }
        buffTime = getTotalBuffTime() + 2_600; // takes 2.6s to release
        if (hxCount + 2 > 4) {
            hxCount = 4;
        } else {
            hxCount += 2;
        }
    }

    public static long getTotalBuffTime() {
        return 30_000;
    }

    @Override
    protected void attack0(WeaponContext ctx, AttackType type) {
        if (type != AttackType.DODGE) {
            return;
        }
        if (hxCount <= 0) {
            hxCount = 0;
            return;
        }
        --hxCount;
    }

    @Override
    public void updateExtraData() {
        haiLaZhiYongTime.setAllCoolDown(buffTime, getTotalBuffTime());
        huiXiangCount.setText("" + hxCount);
    }

    @Override
    public void decreaseCoolDown(long time) {
        if (cdCannotChange)
            return;
        super.decreaseCoolDown(time);
    }

    @Override
    public void resetCoolDown() {
        if (cdCannotChange)
            return;
        super.resetCoolDown();
    }
}
