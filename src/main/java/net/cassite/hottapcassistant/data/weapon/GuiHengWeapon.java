package net.cassite.hottapcassistant.data.weapon;

import io.vproxy.vfx.manager.audio.AudioGroup;
import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class GuiHengWeapon extends AbstractWeapon implements Weapon {
    public GuiHengWeapon() {
        super(20, 1500);
    }

    @Override
    public String getId() {
        return "gui-heng";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.PHYSICAL;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.DPS;
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("guī héng");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("gui-heng");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("yan-miao", 5);
    }

    @Override
    protected boolean isRevertibleSkill(WeaponContext ctx) {
        return true;
    }
}
