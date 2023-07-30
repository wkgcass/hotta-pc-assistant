package net.cassite.hottapcassistant.data.weapon;

import io.vproxy.vfx.manager.audio.AudioGroup;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import net.cassite.hottapcassistant.component.cooldown.WeaponSpecialInfo;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class MengZhangWeapon extends AbstractWeapon implements Weapon {
    private boolean hasStar3 = false;

    private int count = 2;
    private final WeaponSpecialInfo skillCounter;

    public MengZhangWeapon() {
        super(30);
        skillCounter = new WeaponSpecialInfo(Utils.getBuffImageFromClasspath("meng-zhang-skill"), "mengZhangSkillCounter", I18n.get().buffName("mengZhangSkillCounter"));
        skillCounter.setOnMouseClicked(e -> resetCount());
        skillCounter.setCursor(Cursor.HAND);
        extraInfoList.add(skillCounter);
    }

    @Override
    public String getId() {
        return "meng-zhang";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.VOLT;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.DEFENSE;
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("mèng zhāng");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("meng-zhang");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("huang", 5);
    }

    @Override
    public void init(int stars, Matrix[] matrix) {
        super.init(stars, matrix);
        if (stars >= 3) {
            hasStar3 = true;
            totalCoolDown = totalCoolDown * 2 / 3;
        }
    }

    @Override
    protected void threadTick(long ts, long delta) {
        if (currentCD == 0) {
            if (count < 2) {
                ++count;
            }
            if (count < 2) {
                currentCD = totalCoolDown - attackPointTime;
            }
        }
    }

    public int getSkillCount() {
        return count;
    }

    public void resetCount() {
        count = 2;
        currentCD = 0;
    }

    @Override
    public long getCoolDown() {
        return count == 0 ? currentCD : 0;
    }

    @Override
    public double[] getAllCoolDown() {
        if (count == 2) return null;
        return new double[]{currentCD / (double) totalCoolDown};
    }

    @Override
    public Skill useSkill(WeaponContext ctx) {
        if (count == 2) {
            count = 1;
            return super.useSkillIgnoreCD(ctx);
        }
        if (count > 0) {
            --count;
            return skillInstance();
        }
        return null;
    }

    @Override
    public void decreaseCoolDown(long time) {
        if (hasStar3)
            return;
        super.decreaseCoolDown(time);
    }

    @Override
    public void resetCoolDown() {
        if (hasStar3)
            return;
        super.resetCoolDown();
    }

    @Override
    public void updateExtraData() {
        skillCounter.setText(getSkillCount() + "");
    }

    public static boolean hasMengZhangCDDecreasingAndDisableCDChanging(WeaponContext ctx) {
        var opt = ctx.weapons.stream().filter(w -> w instanceof MengZhangWeapon).findAny();
        if (opt.isEmpty())
            return false;
        var w = opt.get();
        return w.getStars() >= 3;
    }
}
