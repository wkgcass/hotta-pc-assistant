package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.data.skill.NoHitSkill;
import net.cassite.hottapcassistant.i18n.I18n;
import io.vproxy.vfx.manager.audio.AudioGroup;
import net.cassite.hottapcassistant.util.Utils;

public class ShengHenQuanZhangWeapon extends AbstractWeapon implements Weapon, SkipAudioCollection001 {
    private boolean cdCannotChange = false;

    public ShengHenQuanZhangWeapon() {
        super(60, 800);
    }

    @Override
    public String getId() {
        return "sheng-hen-quan-zhang";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.VOLT;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.SUPPORT;
    }

    @Override
    protected Skill skillInstance() {
        return new NoHitSkill(getSkillAudio());
    }

    @Override
    protected boolean isRevertibleSkill(WeaponContext ctx) {
        return true;
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("shèng hén quán zhàng");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("sheng-hen-quan-zhang");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("pei-pei", 3);
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
