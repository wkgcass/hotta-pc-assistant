package net.cassite.hottapcassistant.data.weapon;

import io.vproxy.vfx.manager.audio.AudioGroup;
import javafx.scene.image.Image;
import net.cassite.hottapcassistant.component.cooldown.WeaponCoolDown;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class ChongRuiWeapon extends AbstractWeapon implements Weapon {
    private int state = 0; // 0: normal, 1: fu-yao used
    private final WeaponCoolDown shenLouTime;
    private long _shenLouTime;

    public ChongRuiWeapon() {
        super(25);
        shenLouTime = new WeaponCoolDown(Utils.getBuffImageFromClasspath("shen-lou"), "shenLouTime", I18n.get().buffName("shenLouTime"));
        extraIndicatorList.add(shenLouTime);
    }

    @Override
    public String getId() {
        return "chong-rui";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.FLAME;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.DPS;
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("chóng ruǐ");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("chong-rui");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("fei-se", 5);
    }

    @Override
    protected void threadTick(long ts, long delta) {
        super.threadTick(ts, delta);
        _shenLouTime = Utils.subtractLongGE0(_shenLouTime, delta);
        if (_shenLouTime == 0) {
            state = 0;
        }
    }

    public long getShenLouTime() {
        return _shenLouTime;
    }

    @Override
    public long getCoolDown() {
        if (state == 0) {
            return currentCD;
        } else {
            assert state == 1;
            if (_shenLouTime < 5_000) {
                return 0;
            }
            return _shenLouTime - 5_000;
        }
    }

    @Override
    public double[] getAllCoolDown() {
        if (state == 0) {
            if (currentCD == 0)
                return null;
            else
                return new double[]{currentCD / (double) totalCoolDown};
        }
        if (_shenLouTime > 5_000) {
            return new double[]{(_shenLouTime - 5_000) / (double) 20_000};
        } else {
            return null;
        }
    }

    @Override
    public Skill useSkill(WeaponContext ctx) {
        var cd = getCoolDown();
        if (cd != 0) {
            return null;
        }
        if (state == 0) {
            state = 1;
            _shenLouTime = 25_000;
        } else {
            state = 0;
            currentCD = 200;
            _shenLouTime = 0;
        }
        return skillInstance();
    }

    @Override
    public void decreaseCoolDown(long time) {
        // do nothing
    }

    @Override
    public void resetCoolDown() {
        // do nothing
    }

    @Override
    public void updateExtraData() {
        shenLouTime.setCoolDown(getShenLouTime());
    }
}
