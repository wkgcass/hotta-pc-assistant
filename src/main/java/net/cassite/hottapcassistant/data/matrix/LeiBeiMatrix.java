package net.cassite.hottapcassistant.data.matrix;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.component.cooldown.WeaponCoolDown;
import net.cassite.hottapcassistant.data.Matrix;
import net.cassite.hottapcassistant.data.Skill;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class LeiBeiMatrix extends AbstractMatrix implements Matrix {
    private long cd = 0;

    private final WeaponCoolDown leiBeiMatrixBuffTimer;

    public LeiBeiMatrix() {
        leiBeiMatrixBuffTimer = new WeaponCoolDown(this::getImage, 1.5, "leiBeiMatrixBuffTimer", I18n.get().buffName("leiBeiMatrixBuffTimer"));
    }

    @Override
    public void init(int[] stars) {
        super.init(stars);
        if (getEffectiveStars()[4] != -1) {
            extraIndicatorList.add(leiBeiMatrixBuffTimer);
        }
    }

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
    public void useSkill(WeaponContext ctx, Weapon w, Skill skill) {
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

    @Override
    public void updateExtraData() {
        leiBeiMatrixBuffTimer.setAllCoolDown(getCoolDown(), getTotalCoolDown());
    }
}
