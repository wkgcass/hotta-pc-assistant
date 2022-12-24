package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.AudioGroup;
import net.cassite.hottapcassistant.util.Utils;

public class PoJunWeapon extends AbstractWeapon implements Weapon {
    public PoJunWeapon() {
        super(30);
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("pò jūn");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("po-jun");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("tian-lang", 5);
    }

    @Override
    public String getId() {
        return "po-jun";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.THUNDER;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.CARRY;
    }
}
