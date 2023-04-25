package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import io.vproxy.vfx.manager.audio.AudioGroup;
import net.cassite.hottapcassistant.util.Utils;

public class HongLianRenWeapon extends AbstractWeapon implements Weapon, SkipAudioCollection001 {
    public HongLianRenWeapon() {
        super(15);
    }

    @Override
    public String getId() {
        return "hong-lian-ren";
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
        return I18n.get().weaponName("hóng lián rèn");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("hong-lian-ren");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("ke-lao-di-ya", 3);
    }
}
