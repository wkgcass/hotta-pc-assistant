package net.cassite.hottapcassistant.data.matrix;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.component.cooldown.WeaponCoolDown;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class LinYeMatrix extends AbstractMatrix implements Matrix {
    private long buffTime = 0;

    private final WeaponCoolDown linYeMatrixBuffTimer;

    public LinYeMatrix() {
        linYeMatrixBuffTimer = new WeaponCoolDown(this::getImage, 1.5, "linYe2MatrixBuffTimer", I18n.get().buffName("linYe2MatrixBuffTimer"));
    }

    @Override
    public void init(int[] stars) {
        super.init(stars);
        if (getEffectiveStars()[2] != -1) {
            extraIndicatorList.add(linYeMatrixBuffTimer);
        }
    }

    @Override
    protected void threadTick(long ts, long delta) {
        buffTime = Utils.subtractLongGE0(buffTime, delta);
    }

    @Override
    public void useSkill(WeaponContext ctx, Weapon w, Skill skill) {
        if (skill.hitTarget()) {
            hit();
        }
    }

    @Override
    public void attack(WeaponContext ctx, Weapon w, AttackType type) {
        hit();
    }

    private void hit() {
        if (getEffectiveStars()[2] == -1) return;
        buffTime = getTotalBuffTime();
    }

    public long getBuffTime() {
        return buffTime;
    }

    public long getTotalBuffTime() {
        return 18_000;
    }

    @Override
    protected String buildName() {
        return I18n.get().matrixName("lǐn yè");
    }

    @Override
    protected Image buildImage() {
        return Utils.getMatrixImageFromClasspath("lin-ye");
    }

    @Override
    public void updateExtraData() {
        linYeMatrixBuffTimer.setAllCoolDown(getBuffTime(), getTotalBuffTime());
    }
}
