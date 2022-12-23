package net.cassite.hottapcassistant.data.matrix;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Matrix;
import net.cassite.hottapcassistant.data.Skill;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class KeLaoDiYaMatrix extends AbstractMatrix implements Matrix {
    private long decreaseCD;

    public KeLaoDiYaMatrix() {
    }

    @Override
    protected String buildName() {
        return I18n.get().matrixName("kè láo dí yà");
    }

    @Override
    protected Image buildImage() {
        return Utils.getMatrixImageFromClasspath("ke-lao-di-ya");
    }

    @Override
    public void init(int[] stars) {
        super.init(stars);
        if (getEffectiveStars()[4] == 0) {
            decreaseCD = 1500;
        } else if (getEffectiveStars()[4] == 1) {
            decreaseCD = 2000;
        } else if (getEffectiveStars()[4] == 2) {
            decreaseCD = 2500;
        } else if (getEffectiveStars()[4] == 3) {
            decreaseCD = 3000;
        } else {
            decreaseCD = 0;
        }
    }

    @Override
    public void useSkill(WeaponContext ctx, Weapon w, Skill skill) {
        if (!skill.hitTarget()) return;
        if (decreaseCD == 0) return;
        ctx.decreaseCoolDown(decreaseCD);
    }
}
