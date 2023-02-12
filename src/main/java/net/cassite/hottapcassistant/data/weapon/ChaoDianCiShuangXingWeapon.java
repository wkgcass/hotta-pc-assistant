package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import io.vproxy.vfx.manager.audio.AudioGroup;
import net.cassite.hottapcassistant.util.Utils;

public class ChaoDianCiShuangXingWeapon extends AbstractWeapon implements Weapon, SkipAudioCollection001 {
    public ChaoDianCiShuangXingWeapon() {
        super(45, 600);
    }

    @Override
    public String getId() {
        return "chao-dian-ci-shuang-xing";
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
        return I18n.get().weaponName("chāo diàn cí shuāng xīng");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("chao-dian-ci-shuang-xing");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("sai-mi-er", 3);
    }
}
