package net.cassite.hottapcassistant.data.matrix;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Matrix;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class LeiBeiMatrix extends AbstractMatrix implements Matrix {
    private long cd = 0;

    @Override
    protected String buildName() {
        return I18n.get().matrixName("léi bèi");
    }

    @Override
    protected Image buildImage() {
        return Utils.getMatrixImageFromClasspath("lei-bei");
    }

    @Override
    protected void threadTick(long ts, long delta) {
        cd = Utils.subtractLongGE0(cd, delta);
    }

    @Override
    public void useSkill(WeaponContext ctx, Weapon w) {
        if (cd == 0 && getEffectiveStars()[4] != -1) {
            cd = getTotalCoolDown();
        }
    }

    public long getCoolDown() {
        return cd;
    }

    public long getTotalCoolDown() {
        return 15_000;
    }
}
