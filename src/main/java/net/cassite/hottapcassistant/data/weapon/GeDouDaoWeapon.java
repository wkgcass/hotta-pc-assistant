package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import io.vproxy.vfx.manager.audio.AudioGroup;
import net.cassite.hottapcassistant.util.Utils;

public class GeDouDaoWeapon extends AbstractWeapon implements Weapon, SkipAudioCollection001 {
    public GeDouDaoWeapon() {
        super(45);
    }

    @Override
    public String getId() {
        return "ge-dou-dao";
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
        return I18n.get().weaponName("gě dòu dāo");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("ge-dou-dao");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return null;
    }
}
