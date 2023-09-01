package net.cassite.hottapcassistant.data.weapon;

import io.vproxy.vfx.manager.audio.AudioGroup;
import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class JianBingWeapon extends AbstractWeapon implements Weapon {
    public JianBingWeapon() {
        super(30);
    }

    @Override
    public String getId() {
        return "bu-mie-zhi-yi";
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
        return I18n.get().weaponName("jiān bīng");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("jian-bing");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("ling-han", 5);
    }
}
