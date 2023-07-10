package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import io.vproxy.vfx.manager.audio.AudioGroup;
import net.cassite.hottapcassistant.util.Utils;

public class ChaoDianCiShuangXingWeapon extends AbstractWeapon implements Weapon, SkipAudioCollection001 {
    private boolean cdCannotChange = false;

    public ChaoDianCiShuangXingWeapon() {
        super(45, 600);
    }

    @Override
    public String getId() {
        return "chao-dian-ci-shuang-xing";
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

    @Override
    public void init(WeaponContext ctx) {
        super.init(ctx);
        if (MengZhangWeapon.hasMengZhangCDDecreasingAndDisableCDChanging(ctx)) {
            totalCoolDown = totalCoolDown * 2 / 3;
            cdCannotChange = true;
        }
    }

    @Override
    public void decreaseCoolDown(long time) {
        if (cdCannotChange)
            return;
        super.decreaseCoolDown(time);
    }

    @Override
    public void resetCoolDown() {
        if (cdCannotChange)
            return;
        super.resetCoolDown();
    }
}
