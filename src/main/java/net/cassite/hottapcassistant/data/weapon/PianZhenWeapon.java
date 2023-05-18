package net.cassite.hottapcassistant.data.weapon;

import io.vproxy.vfx.manager.audio.AudioGroup;
import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.entity.AssistantCoolDownOptions;
import net.cassite.hottapcassistant.entity.WeaponArgs;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class PianZhenWeapon extends AbstractWeapon implements Weapon {
    private boolean alwaysCanUseSkillOfPianZhen;

    public PianZhenWeapon() {
        super(15, 1500);
    }

    @Override
    public String getId() {
        return "pian-zhen";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.FROST;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.DPS;
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("pián zhēn");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("pian-zhen");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("yu-lan", 5);
    }

    @Override
    public void init(WeaponArgs args) {
        if (!(args instanceof AssistantCoolDownOptions opts)) {
            return;
        }
        alwaysCanUseSkillOfPianZhen = opts.alwaysCanUseSkillOfPianZhen;
    }

    @Override
    protected boolean isRevertibleSkill(WeaponContext ctx) {
        return !alwaysCanUseSkillOfPianZhen;
    }

    @Override
    public Skill useSkill(WeaponContext ctx) {
        if (alwaysCanUseSkillOfPianZhen) {
            return super.useSkillIgnoreCD(ctx);
        }
        return super.useSkill(ctx);
    }
}
