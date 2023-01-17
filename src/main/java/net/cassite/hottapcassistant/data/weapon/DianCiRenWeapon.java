package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import io.vproxy.vfx.manager.audio.AudioGroup;
import net.cassite.hottapcassistant.util.Utils;

public class DianCiRenWeapon extends AbstractWeapon implements Weapon {
    public DianCiRenWeapon() {
        super(10);
    }

    @Override
    public String getId() {
        return "dian-ci-ren";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.THUNDER;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.CARRY;
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("diàn cí rèn");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("dian-ci-ren");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return null;
    }
}
