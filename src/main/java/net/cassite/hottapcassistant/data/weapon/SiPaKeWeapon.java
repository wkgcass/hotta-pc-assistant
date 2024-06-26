package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.data.resonance.FireResonance;
import net.cassite.hottapcassistant.i18n.I18n;
import io.vproxy.vfx.manager.audio.AudioGroup;
import net.cassite.hottapcassistant.util.Utils;

public class SiPaKeWeapon extends AbstractWeapon implements Weapon, FireResonance {
    public SiPaKeWeapon() {
        super(30, 400);
    }

    @Override
    public void init(int stars, Matrix[] matrix) {
        super.init(stars, matrix);
        if (stars >= 1) {
            totalCoolDown = 24_400;
        }
        if (stars >= 6) {
            totalCoolDown = 16_400;
        }
    }

    @Override
    public String getId() {
        return "si-pa-ke";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.FLAME;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.DPS;
    }

    @Override
    protected Skill useSkill0(WeaponContext ctx) {
        var skill = super.useSkill0(ctx);
        if (skill == null) {
            return null;
        }
        var settle = ctx.getBurnSettleContext();
        settle.trigger(ctx, 6_000);
        return skill;
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("sī pà kè");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("si-pa-ke");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("lei-bei", 3);
    }
}
