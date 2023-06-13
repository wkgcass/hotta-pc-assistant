package net.cassite.hottapcassistant.data.weapon;

import io.vproxy.vfx.manager.audio.AudioGroup;
import javafx.scene.image.Image;
import net.cassite.hottapcassistant.component.cooldown.WeaponCoolDown;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class ZhiMingWeapon extends AbstractWeapon implements Weapon {
    private long guanFengCD = 0;
    private final WeaponCoolDown guanFengCDTimer = new WeaponCoolDown(Utils.getBuffImageFromClasspath("guan-feng"), "guanFengCD", I18n.get().buffName("guanFengCD"));

    public ZhiMingWeapon() {
        super(20);
        extraIndicatorList.add(guanFengCDTimer);
    }

    @Override
    public String getId() {
        return "zhi-ming";
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
        return I18n.get().weaponName("zhí míng");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("zhi-ming");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("ming-jing", 5);
    }

    @Override
    protected void threadTick(long ts, long delta) {
        guanFengCD = Utils.subtractLongGE0(guanFengCD, delta);
    }

    @Override
    public void updateExtraData() {
        guanFengCDTimer.setAllCoolDown(getGuanFengCD(), getTotalGuanFengCD());
    }

    @Override
    protected void attack0(WeaponContext ctx, AttackType type) {
        if (type == AttackType.DODGE_POWER) {
            guanFengCD = getTotalGuanFengCD();
        }
    }

    public long getGuanFengCD() {
        return guanFengCD;
    }

    public long getTotalGuanFengCD() {
        return 200_000;
    }
}
