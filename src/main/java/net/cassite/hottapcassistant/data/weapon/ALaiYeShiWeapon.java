package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import io.vproxy.vfx.manager.audio.AudioGroup;
import net.cassite.hottapcassistant.util.Utils;

public class ALaiYeShiWeapon extends AbstractWeapon implements Weapon {
    private boolean cdCannotChange = false;

    public ALaiYeShiWeapon() {
        super(25);
    }

    @Override
    public String getId() {
        return "a-lai-ye-shi";
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
        return I18n.get().weaponName("ā lài yē shí");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("a-lai-ye-shi");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("bai-yue-kui", 3);
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
