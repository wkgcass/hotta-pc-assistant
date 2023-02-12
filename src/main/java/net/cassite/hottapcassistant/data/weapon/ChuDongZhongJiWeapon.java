package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.data.misc.TriggerLiuQuanCheXinStar1;
import net.cassite.hottapcassistant.data.skill.ChuDongZhongJiAttackSkill;
import net.cassite.hottapcassistant.data.skill.ChuDongZhongJiSwitchModeSkill;
import net.cassite.hottapcassistant.i18n.I18n;
import io.vproxy.vfx.manager.audio.AudioGroup;
import net.cassite.hottapcassistant.util.Utils;

public class ChuDongZhongJiWeapon extends AbstractWeapon implements Weapon, TriggerLiuQuanCheXinStar1, SkipAudioCollection001 {
    private int state = 0;
    // 0 -> normal
    // 1 -> pen-qi
    private long state0CD = 0;
    private long state1CD = 0;
    private long state1Time = 0;

    public ChuDongZhongJiWeapon() {
        super(45);
    }

    @Override
    public String getId() {
        return "chu-dong-zhong-ji";
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
    protected void threadTick(long ts, long delta) {
        state0CD = Utils.subtractLongGE0(state0CD, delta);
        state1CD = Utils.subtractLongGE0(state1CD, delta);
        state1Time = Utils.subtractLongGE0(state1Time, delta);

        if (state0CD == 0 || state1Time == 0) {
            resetState();
        }
    }

    private void resetState() {
        state = 0;
        state1Time = 0;
        cooldown = 45_000;
        cd = state0CD;
    }

    @Override
    protected Skill useSkill0(WeaponContext ctx) {
        if (state == 0) {
            var ok = super.useSkill0(ctx);
            if (ok == null) return null;
            state = 1;
            state0CD = cd;
            cd = state1CD;
            cooldown = 10_000;
            state1Time = 25_000;
            return ChuDongZhongJiSwitchModeSkill.instance;
        } else {
            var ok = super.useSkill0(ctx);
            if (ok == null) return null;
            state1CD = cd;
            return ChuDongZhongJiAttackSkill.instance;
        }
    }

    @Override
    protected void alertWeaponSwitched0(WeaponContext ctx, Weapon w, boolean discharge) {
        if (w == null) return;
        resetState();
    }

    @Override
    public long getCoolDown() {
        if (state == 0) return state0CD;
        else return state1CD;
    }

    @Override
    public double[] getAllCoolDown() {
        if (state == 0) return super.getAllCoolDown();
        if (state0CD == 0) return null;
        if (state1Time > 0) {
            return new double[]{state0CD / 45_000d, state1Time / 25_000d};
        } else {
            return new double[]{state0CD / 45_000d};
        }
    }

    @Override
    public void resetCoolDown() {
        super.resetCoolDown();
        state = 0;
        state0CD = 0;
        state1CD = 0;
        state1Time = 0;
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("chū dòng zhòng jī");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("chu-dong-zhong-ji");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("xi", 3);
    }

    @Override
    public boolean triggerLiuQuanCheXinStar1() {
        return false;
    }
}
