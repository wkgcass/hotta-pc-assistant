package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import io.vproxy.vfx.manager.audio.AudioGroup;
import net.cassite.hottapcassistant.util.Utils;

public class ShuangDongChangQiangWeapon extends AbstractWeapon implements Weapon {
    public ShuangDongChangQiangWeapon() {
        super(25, 500);
    }

    @Override
    public String getId() {
        return "shuang-dong-chang-qiang";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.ICE;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.CARRY;
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("shuāng dòng cháng qiāng");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("shuang-dong-chang-qiang");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return null;
    }
}
