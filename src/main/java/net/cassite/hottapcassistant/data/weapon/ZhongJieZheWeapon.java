package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.data.misc.TriggerLiuQuanCheXinStar1;
import net.cassite.hottapcassistant.data.skill.ZhongJieZheSkill;
import net.cassite.hottapcassistant.i18n.I18n;
import io.vproxy.vfx.manager.audio.AudioGroup;
import net.cassite.hottapcassistant.util.Utils;

public class ZhongJieZheWeapon extends AbstractWeapon implements Weapon, TriggerLiuQuanCheXinStar1, SkipAudioCollection001 {
    public ZhongJieZheWeapon() {
        super(0);
    }

    @Override
    public String getId() {
        return "zhong-jie-zhe";
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
    public Skill useSkill(WeaponContext ctx) {
        return ZhongJieZheSkill.instance;
    }

    @Override
    public long getCoolDown() {
        return 0;
    }

    @Override
    public double[] getAllCoolDown() {
        return null;
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("zhōng jié zhě");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("zhong-jie-zhe");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("xi-er-da", 3);
    }

    @Override
    public boolean triggerLiuQuanCheXinStar1() {
        return false;
    }
}
