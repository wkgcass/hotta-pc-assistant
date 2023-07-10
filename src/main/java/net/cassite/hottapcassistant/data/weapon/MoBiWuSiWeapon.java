package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.component.cooldown.WeaponCoolDown;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.i18n.I18n;
import io.vproxy.vfx.manager.audio.AudioGroup;
import net.cassite.hottapcassistant.util.Utils;

public class MoBiWuSiWeapon extends AbstractWeapon implements Weapon, SkipAudioCollection001 {
    private long moShuShiJianTime = 0;
    private final WeaponCoolDown moShuShiJian = new WeaponCoolDown(Utils.getBuffImageFromClasspath("mo-shu-shi-jian"), "moShuShiJian", I18n.get().buffName("moShuShiJian"));
    private long buffTime = 0;
    private long totalBuffTime = 0;
    private final WeaponCoolDown buff = new WeaponCoolDown(Utils.getBuffImageFromClasspath("mo-bi-wu-si-buff"), "moBiWuSiBuff", I18n.get().buffName("moBiWuSiBuff"));

    public MoBiWuSiWeapon() {
        super(30);
    }

    @Override
    public String getId() {
        return "mo-bi-wu-si";
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
    public void init(int stars, Matrix[] matrix) {
        super.init(stars, matrix);
        if (stars >= 3) {
            extraIndicatorList.add(moShuShiJian);
            extraIndicatorList.add(buff);
        }
    }

    @Override
    protected void threadTick(long ts, long delta) {
        moShuShiJianTime = Utils.subtractLongGE0(moShuShiJianTime, delta);
        buffTime = Utils.subtractLongGE0(buffTime, delta);
    }

    @Override
    protected Skill useSkill0(WeaponContext ctx) {
        var skill = super.useSkill0(ctx);
        if (skill == null) return null;
        moShuShiJianTime = getTotalMoShuShiJianTime();
        return skill;
    }

    @Override
    protected void alertWeaponSwitched0(WeaponContext ctx, Weapon w, boolean discharge) {
        if (w == null) return;
        var time = moShuShiJianTime;
        moShuShiJianTime = 0;
        if (stars >= 3) {
            if (time > 0) {
                currentCD = Utils.subtractLongGE0(currentCD, Math.min(13_000, time));
                totalBuffTime = 20_000 * Math.min(13_000, time) / 13_000;
                buffTime = totalBuffTime;
            }
        }
    }

    @Override
    public void resetCoolDown() {
        // do nothing
    }

    @Override
    public void decreaseCoolDown(long time) {
        // do nothing
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("mò bǐ wū sī");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("mo-bi-wu-si");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("wu-mi", 5);
    }

    public long getMoShuShiJianTime() {
        return moShuShiJianTime;
    }

    public long getTotalMoShuShiJianTime() {
        return 18_000;
    }

    public long getBuffTime() {
        return buffTime;
    }

    public long getTotalBuffTime() {
        return totalBuffTime;
    }

    @Override
    public void updateExtraData() {
        moShuShiJian.setAllCoolDown(moShuShiJianTime, getTotalMoShuShiJianTime());
        buff.setAllCoolDown(buffTime, totalBuffTime);
    }
}
