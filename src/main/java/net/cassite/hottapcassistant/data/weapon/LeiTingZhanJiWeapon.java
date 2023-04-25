package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import io.vproxy.vfx.manager.audio.AudioGroup;
import net.cassite.hottapcassistant.util.Utils;

public class LeiTingZhanJiWeapon extends AbstractWeapon implements Weapon, SkipAudioCollection001 {
    public LeiTingZhanJiWeapon() {
        super(25, 300);
    }

    @Override
    public String getId() {
        return "lei-ting-zhan-ji";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.VOLT;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.DPS;
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("léi tíng zhàn jǐ");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("lei-ting-zhan-ji");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("ai-ge", 3);
    }
}
