package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.i18n.I18n;
import io.vproxy.vfx.manager.audio.AudioGroup;
import net.cassite.hottapcassistant.util.Utils;

public class V2RongQuDunWeapon extends AbstractWeapon implements Weapon, SkipAudioCollection001 {
    private int state = 0;
    // 0 -> shield
    // 1 -> ax

    public V2RongQuDunWeapon() {
        super(25);
    }

    @Override
    public String getId() {
        return "v2-rong-qu-dun";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.FLAME;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.DEFENSE;
    }

    @Override
    public Skill useSkill(WeaponContext ctx) {
        if (state == 1) {
            state = 0;
            postUseSkill(ctx, skillInstance());
            return skillInstance();
        }
        var ok = super.useSkill(ctx);
        if (ok == null) {
            return null;
        }
        state = 1;
        return skillInstance();
    }

    @Override
    protected void alertWeaponSwitched0(WeaponContext ctx, Weapon w, boolean discharge) {
        if (w == null) return;
        state = 0;
    }

    @Override
    public long getCoolDown() {
        if (state == 1) return 0;
        return super.getCoolDown();
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("v2 róng qǖ dùn");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("v2-rong-qv-dun");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("xiu-ma", 3);
    }
}
