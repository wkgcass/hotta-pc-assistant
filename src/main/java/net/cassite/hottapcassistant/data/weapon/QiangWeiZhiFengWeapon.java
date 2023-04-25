package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import io.vproxy.vfx.manager.audio.AudioGroup;
import net.cassite.hottapcassistant.util.Utils;

public class QiangWeiZhiFengWeapon extends AbstractWeapon implements Weapon, SkipAudioCollection001 {
    public QiangWeiZhiFengWeapon() {
        super(30, 800);
    }

    @Override
    public String getId() {
        return "qiang-wei-zhi-feng";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.FROST;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.DEFENSE;
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("qiáng wēi zhī fēng");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("qiang-wei-zhi-feng");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("mei-li-er", 3);
    }
}
