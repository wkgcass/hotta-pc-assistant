package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import io.vproxy.vfx.manager.audio.AudioGroup;
import net.cassite.hottapcassistant.util.Utils;

public class FuHeGongWeapon extends AbstractWeapon implements Weapon, SkipAudioCollection001 {
    public FuHeGongWeapon() {
        super(12, 1000);
    }

    @Override
    public String getId() {
        return "fu-he-gong";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.FIRE;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.CARRY;
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("fù hé gōng");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("fu-he-gong");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return null;
    }
}
