package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class YingZhiWeapon extends AbstractWeapon implements Weapon {
    private long antiFalseTouchCD;
    private long fieldTime;

    public YingZhiWeapon() {
        super(30);
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
    public WeaponElement element() {
        return WeaponElement.YINENG;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.ATK;
    }

    @Override
    public boolean useSkill(WeaponContext ctx) {
        if (super.useSkill(ctx)) {
            antiFalseTouchCD = 500;
            resetFieldTime();
            return true;
        }
        if (stars >= 6) {
            if (antiFalseTouchCD == 0) {
                antiFalseTouchCD = 500;
                alertMatrix(ctx);
                resetFieldTime();
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
    public long getCoolDown() {
        if (stars >= 6) {
            return antiFalseTouchCD;
        } else {
            return super.getCoolDown();
        }
    }
}
