package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class LiuQuanCheXinWeapon extends AbstractWeapon implements Weapon {
    private int count = 0;

    public LiuQuanCheXinWeapon() {
        super(30, 200);
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("liú quán chè xīn");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("liu-quan-che-xin");
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.ICE;
    }
    @Override
    public WeaponCategory category() {
        return WeaponCategory.DEF;
    }

    @Override
    public void alertSkillUsed(WeaponContext ctx, Weapon w) {
        if (stars < 1) {
            return;
        }
        boolean triggerred = ctx.resonanceInfo.ice;
        if (!triggerred) {
            for (var ww : ctx.weapons) {
                if (ww instanceof YingZhiWeapon) {
                    triggerred = true;
                    break;
                }
            }
        }
        if (!triggerred) {
            return;
        }
        int n = count + 1;
        if (n >= 5) {
            this.count = 0;
            ctx.resetCoolDown();
        } else {
            this.count = n;
        }
    }

    public int getCount() {
        return count;
    }

    public void addCount(WeaponContext ctx) {
        alertSkillUsed(ctx, null);
    }
}
