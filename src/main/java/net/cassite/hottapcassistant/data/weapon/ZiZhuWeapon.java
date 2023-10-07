package net.cassite.hottapcassistant.data.weapon;

import io.vproxy.vfx.manager.audio.AudioGroup;
import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class ZiZhuWeapon extends AbstractWeapon implements Weapon {
    public ZiZhuWeapon() {
        super(60);
    }

    @Override
    public String getId() {
        return "zi-zhu";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.ALTERED;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.DPS;
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("zǐ zhú");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("zi-zhu");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("nan-yin", 5);
    }
}
