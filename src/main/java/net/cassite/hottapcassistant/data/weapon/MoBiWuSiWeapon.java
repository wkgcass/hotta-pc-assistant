package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.component.cooldown.WeaponCoolDown;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class MoBiWuSiWeapon extends AbstractWeapon implements Weapon {
    private long moShuShiJianTime = 0;
    private final WeaponCoolDown moShuShiJian = new WeaponCoolDown(Utils.getBuffImageFromClasspath("mo-shu-shi-jian"), "moShuShiJian", I18n.get().buffName("moShuShiJian"));

    public MoBiWuSiWeapon() {
        super(30);
    }

    @Override
    public String getId() {
        return "mo-bi-wu-si";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.PHYSICS;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.CARRY;
    }

    @Override
    public void init(int stars, Matrix[] matrix) {
        super.init(stars, matrix);
        if (stars >= 3) {
            extraIndicatorList.add(moShuShiJian);
        }
    }

    @Override
    protected void threadTick(long ts, long delta) {
        moShuShiJianTime = Utils.subtractLongGE0(moShuShiJianTime, delta);
    }

    @Override
    protected boolean useSkill0(WeaponContext ctx) {
        if (!super.useSkill0(ctx)) return false;
        moShuShiJianTime = getTotalMoShuShiJianTime();
        return true;
    }

    @Override
    protected void alertWeaponSwitched0(WeaponContext ctx, Weapon w, boolean discharge) {
        if (w == null) return;
        var time = moShuShiJianTime;
        moShuShiJianTime = 0;
        if (stars >= 3) {
            if (time > 0) {
                cd = Utils.subtractLongGE0(cd, Math.min(13_000, time));
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

    public long getMoShuShiJianTime() {
        return moShuShiJianTime;
    }

    public long getTotalMoShuShiJianTime() {
        return 18_000;
    }

    @Override
    public void updateExtraData() {
        moShuShiJian.setAllCoolDown(moShuShiJianTime, getTotalMoShuShiJianTime());
    }
}
