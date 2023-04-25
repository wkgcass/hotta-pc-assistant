package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import io.vproxy.vfx.manager.audio.AudioGroup;
import net.cassite.hottapcassistant.util.Utils;

public class HuanHaiLunRenWeapon extends AbstractWeapon implements Weapon, SkipAudioCollection001 {
    public HuanHaiLunRenWeapon() {
        super(45, 800);
    }

    @Override
    public String getId() {
        return "huan-hai-lun-ren";
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
    protected String buildName() {
        return I18n.get().weaponName("huán hǎi lún rèn");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("huan-hai-lun-ren");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("xi-luo", 2);
    }
}
