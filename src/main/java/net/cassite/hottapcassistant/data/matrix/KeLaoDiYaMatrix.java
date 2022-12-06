package net.cassite.hottapcassistant.data.matrix;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Matrix;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class KeLaoDiYaMatrix implements Matrix {
    private final String name = I18n.get().matrixName("kè láo dí yà");
    private final Image image = Utils.getMatrixImageFromClasspath("ke-lao-di-ya");
    private long decreaseCD;

    public KeLaoDiYaMatrix() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Image getImage() {
        return image;
    }

    @Override
    public void init(int[] stars) {
        if (stars.length < 4) {
            decreaseCD = 0;
        } else {
            int s = 3;
            for (var n : stars) {
                if (n < s) {
                    s = n;
                }
            }
            if (s == 0) {
                decreaseCD = 1500;
            } else if (s == 1) {
                decreaseCD = 2000;
            } else if (s == 2) {
                decreaseCD = 2500;
            } else if (s == 3) {
                decreaseCD = 3000;
            }
        }
    }

    @Override
    public void useSkill(WeaponContext ctx, Weapon w, boolean hitTarget) {
        if (decreaseCD == 0) return;
        ctx.decreaseCoolDown(decreaseCD);
    }
}
