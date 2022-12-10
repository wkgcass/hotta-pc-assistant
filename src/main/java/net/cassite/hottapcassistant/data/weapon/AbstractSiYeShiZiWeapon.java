package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.util.Utils;

public abstract class AbstractSiYeShiZiWeapon extends AbstractWeapon implements Weapon {
    private int state = 0;
    // 0 -> normal
    // 1 -> skill used
    // 2 -> selected
    protected long burnBuff;
    protected int shotRemain = -1;

    public AbstractSiYeShiZiWeapon() {
        super(14);
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.FIRE;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.ATK;
    }

    @Override
    protected void threadTick(long ts, long delta) {
        if (cd == 0) {
            state = 0;
        }
        burnBuff = Utils.subtractLongGE0(burnBuff, delta);
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
            return true;
        } else {
            assert state == 2;
            return super.useSkill(ctx);
        }
    }

    @Override
    protected boolean skillHitTarget() {
        return false;
    }

    @Override
    public void aimAttack(WeaponContext ctx) {
        if (shotRemain == -1) return;
        --shotRemain;
    }

    @Override
    public void dodgeAttack(WeaponContext ctx) {
        if (shotRemain == -1) return;
        --shotRemain;
    }

    @Override
    public void alertWeaponSwitched(WeaponContext ctx, Weapon w) {
        shotRemain = -1;
    }

    @Override
    public void specialAttack(WeaponContext ctx) {
        if (shotRemain == -1) return;
        --shotRemain;
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

    public int getShotRemain() {
        return Math.max(shotRemain, 0);
    }
}
