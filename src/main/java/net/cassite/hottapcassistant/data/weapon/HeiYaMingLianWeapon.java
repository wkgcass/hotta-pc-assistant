package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import io.vproxy.vfx.manager.audio.AudioGroup;
import net.cassite.hottapcassistant.util.Utils;

public class HeiYaMingLianWeapon extends AbstractWeapon implements Weapon, SkipAudioCollection001 {
    public HeiYaMingLianWeapon() {
        super(45, 400);
    }

    @Override
    public String getId() {
        return "hei-ya-ming-lian";
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
        return I18n.get().weaponName("hēi yā míng lián");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("hei-ya-ming-lian");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("king", 3);
    }
}
