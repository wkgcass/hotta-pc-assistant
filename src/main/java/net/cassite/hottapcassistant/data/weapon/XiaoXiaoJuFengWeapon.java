package net.cassite.hottapcassistant.data.weapon;

import io.vproxy.vfx.manager.audio.AudioGroup;
import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class XiaoXiaoJuFengWeapon extends AbstractWeapon implements Weapon {
    public XiaoXiaoJuFengWeapon() {
        super(20);
    }

    @Override
    public String getId() {
        return "xiao-xiao-ju-feng";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.PHYSICAL;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.DPS;
    }

    @Override
    protected Skill pressSkill0(WeaponContext ctx) {
        return super.useSkillIgnoreCD(ctx);
    }

    @Override
    public Skill useSkill(WeaponContext ctx) {
        return null;
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("xiǎo xiǎo jǜ fēng");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("xiao-xiao-ju-feng");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("ge-nuo-nuo", 5);
    }
}
