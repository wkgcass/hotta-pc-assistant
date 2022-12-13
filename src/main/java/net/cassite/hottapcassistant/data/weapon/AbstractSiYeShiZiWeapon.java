package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.util.Utils;

public abstract class AbstractSiYeShiZiWeapon extends AbstractWeapon implements Weapon {
    private int state = 0;
    // 0 -> normal
    // 1 -> skill used
    // 2 -> selected
    protected long burnBuff;

    private long opticalSpaceTime;
    private int countForOpticalSpace = 0;
    private long lastShotTs = 0;

    protected int shotRemain = -1;
    protected int dodgeRemain = 0; // will / 2

    public AbstractSiYeShiZiWeapon() {
        super(14);
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.FIRE;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.CARRY;
    }

    @Override
    protected void threadTick(long ts, long delta) {
        if (cd == 0) {
            state = 0;
        }
        burnBuff = Utils.subtractLongGE0(burnBuff, delta);
        opticalSpaceTime = Utils.subtractLongGE0(opticalSpaceTime, delta);
    }

    @Override
    public boolean useSkill(WeaponContext ctx) {
        if (state == 0) {
            if (super.useSkill(ctx)) {
                state = 1;
                return true;
            } else {
                return false;
            }
        } else if (state == 1) {
            state = 2;
            cd = 14_000;
            shotRemain = 8;
            dodgeRemain = 8; // will / 2
            return true;
        } else {
            assert state == 2;
            return super.useSkill(ctx);
        }
    }

    @Override
    public boolean skillHitTarget() {
        return false;
    }

    @Override
    protected void attack0(WeaponContext ctx, AttackType type) {
        if (type == AttackType.DODGE || type == AttackType.AIM || type == AttackType.SPECIAL)
            shot(ctx);
    }

    @Override
    protected void dodge0(WeaponContext ctx) {
        if (dodgeRemain == 0) return;
        --dodgeRemain;
    }

    private void shot(WeaponContext ctx) {
        if (shotRemain != -1) {
            var opt = ctx.weapons.stream().filter(e -> e instanceof LingGuangWeapon).findAny();
            if (opt.isPresent() && opt.get().getStars() >= 1) {
                if (shotRemain >= 3) {
                    shotRemain -= 4;
                } else {
                    shotRemain = -1;
                }
            } else {
                --shotRemain;
            }
        }
        var now = System.currentTimeMillis();
        if (now - lastShotTs < 35_000) {
            countForOpticalSpace += 1;
            if (countForOpticalSpace == 2) {
                countForOpticalSpace = 0;
                triggerOpticalSpace();
            }
        } else {
            countForOpticalSpace = 1;
        }
        lastShotTs = now;
    }

    private void triggerOpticalSpace() {
        if (stars < 1) {
            return;
        }
        if (opticalSpaceTime == 0) {
            opticalSpaceTime = getTotalOpticalSpaceTime();
        }
    }

    @Override
    protected void alertWeaponSwitched0(WeaponContext ctx, Weapon w, boolean discharge) {
        shotRemain = -1;
        dodgeRemain = 0;
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("si-ye-shi-zi");
    }

    @Override
    public long getCoolDown() {
        if (state == 1) return 0;
        return cd;
    }

    public long getOpticalSpaceTime() {
        return opticalSpaceTime;
    }

    public long getTotalOpticalSpaceTime() {
        return 12_000;
    }

    public int getShotRemain() {
        return Math.max(shotRemain, 0);
    }

    public int getDodgeRemain() {
        return (dodgeRemain + 1) / 2; // 8 -> 4, 7 -> 4, 6 -> 3, etc...
    }
}
