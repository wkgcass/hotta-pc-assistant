package net.cassite.hottapcassistant.data.weapon;

import io.vproxy.vfx.manager.audio.AudioGroup;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import net.cassite.hottapcassistant.component.cooldown.WeaponSpecialInfo;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.data.resonance.PhysicsResonance;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class WanDaoWeapon extends AbstractWeapon implements Weapon, PhysicsResonance {
    private int count = 3;
    private final WeaponSpecialInfo wanDaoHuiQiCounter;

    public WanDaoWeapon() {
        super(20, 500);
        wanDaoHuiQiCounter = new WeaponSpecialInfo(Utils.getBuffImageFromClasspath("hui-qi"), "wanDaoHuiQiCounter", I18n.get().buffName("wanDaoHuiQiCounter"));
        wanDaoHuiQiCounter.setOnMouseClicked(e -> resetCount());
        wanDaoHuiQiCounter.setCursor(Cursor.HAND);
    }

    @Override
    public void init(WeaponContext ctx) {
        super.init(ctx);
        if (ctx.resonanceInfo.support()) {
            extraInfoList.add(wanDaoHuiQiCounter);
        }
    }

    @Override
    public String getId() {
        return "wan-dao";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.PHYSICAL;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.SUPPORT;
    }

    @Override
    protected void threadTick(long ts, long delta) {
        if (!ctx.resonanceInfo.support()) return;
        if (cd == 0) {
            if (count < 3) {
                ++count;
            }
            if (count < 3) {
                cd = cooldown - attackPointTime;
            }
        }
    }

    @Override
    public Skill useSkill(WeaponContext ctx) {
        if (ctx.resonanceInfo.support()) {
            return super.useSkillIgnoreCD(ctx);
        }
        return super.useSkill(ctx);
    }

    @Override
    protected Skill useSkill0(WeaponContext ctx) {
        if (!ctx.resonanceInfo.support()) {
            return super.useSkill0(ctx);
        }
        if (count > 0) {
            --count;
            if (cd == 0) {
                return super.useSkill0(ctx);
            } else {
                return skillInstance();
            }
        } else {
            return null;
        }
    }

    @Override
    protected void revertSkill0(WeaponContext ctx) {
        if (ctx.resonanceInfo.support()) {
            if (count == 2) {
                count = 3;
                cd = 0;
            } else if (count < 2) {
                ++count;
            }
        } else {
            super.revertSkill0(ctx);
        }
    }

    @Override
    protected boolean isRevertibleSkill(WeaponContext ctx) {
        return true;
    }

    @Override
    public long getCoolDown() {
        if (ctx.resonanceInfo.support()) {
            if (count > 0) return 0;
        }
        return super.getCoolDown();
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("wǎn dǎo");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("wan-dao");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("xing-huan", 5);
    }

    public int getCount() {
        return count;
    }

    public void resetCount() {
        count = 3;
        cd = 0;
    }

    @Override
    public void updateExtraData() {
        wanDaoHuiQiCounter.setText(getCount() + "");
    }
}
