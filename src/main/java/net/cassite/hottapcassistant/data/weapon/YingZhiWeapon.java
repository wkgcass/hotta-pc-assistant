package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.component.cooldown.WeaponCoolDown;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class YingZhiWeapon extends AbstractWeapon implements Weapon {
    private long antiFalseTouchCD;
    private long fieldTime;
    private final WeaponCoolDown yingYueZhiJingBuffTimer;

    public YingZhiWeapon() {
        super(30);
        yingYueZhiJingBuffTimer = new WeaponCoolDown(Utils.getBuffImageFromClasspath("ying-yue-zhi-jing"), "yingYueZhiJingBuffTimer", I18n.get().buffName("yingYueZhiJingBuffTimer"));
        extraIndicatorList.add(yingYueZhiJingBuffTimer);
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("yǐng zhī");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("ying-zhi");
    }

    @Override
    public String getId() {
        return "ying-zhi";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.YINENG;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.CARRY;
    }

    @Override
    public boolean useSkill(WeaponContext ctx) {
        if (antiFalseTouchCD != 0) {
            return false;
        }
        if (super.useSkill(ctx)) {
            antiFalseTouchCD = 500;
            resetFieldTime();
            return true;
        }
        if (stars >= 6) {
            resetFieldTime(); // no cd check, user may hit skill button very quickly
            if (antiFalseTouchCD == 0) {
                antiFalseTouchCD = 3000;
                postUseSkill(ctx);
                return true;
            }
        }
        return true;
    }

    private void resetFieldTime() {
        if (stars >= 3) {
            fieldTime = 20 * 1000;
        } else {
            fieldTime = 15 * 1000;
        }
    }

    @Override
    protected void threadTick(long ts, long delta) {
        var antiFalseTouchCD = this.antiFalseTouchCD;
        if (antiFalseTouchCD < delta) {
            this.antiFalseTouchCD = 0;
        } else {
            antiFalseTouchCD -= delta;
            this.antiFalseTouchCD = antiFalseTouchCD;
        }
        var fieldTime = this.fieldTime;
        if (fieldTime < delta) {
            this.fieldTime = 0;
        } else {
            fieldTime -= delta;
            this.fieldTime = fieldTime;
        }
    }

    public long getFieldTime() {
        return fieldTime;
    }

    public long getTotalFieldTime() {
        return stars >= 3 ? 20_000 : 15_000;
    }

    @Override
    public boolean skillHitTarget() {
        return false;
    }

    @Override
    public long getCoolDown() {
        if (stars >= 6) {
            return antiFalseTouchCD;
        } else {
            return super.getCoolDown();
        }
    }

    @Override
    public void updateExtraData() {
        yingYueZhiJingBuffTimer.setAllCoolDown(getFieldTime(), getTotalFieldTime());
    }
}
