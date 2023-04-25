package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import io.vproxy.vfx.manager.audio.AudioGroup;
import net.cassite.hottapcassistant.util.Utils;

public class BaErMengKeWeapon extends AbstractWeapon implements Weapon {
    public BaErMengKeWeapon() {
        super(30, 700);
    }

    @Override
    public String getId() {
        return "ba-er-meng-ke";
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
        return I18n.get().weaponName("bā ěr méng kè");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("ba-er-meng-ke");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("fu-li-jia", 3);
    }
}
