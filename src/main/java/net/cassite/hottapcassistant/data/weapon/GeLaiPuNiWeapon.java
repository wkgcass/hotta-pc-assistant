package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponCategory;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.data.WeaponElement;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Logger;
import net.cassite.hottapcassistant.util.Utils;

import java.util.LinkedList;

public class GeLaiPuNiWeapon extends AbstractWeapon implements Weapon {
    private volatile int state = 0;
    private static final int STATE_NORMAL = 0;
    private static final int STATE_SKILL_USED = 1;
    private static final int STATE_CAN_BE_REFRESHED = 2;
    private static final int STATE_IS_REFRESHED = 3;
    private static final int STATE_USED_AND_CANNOT_REFRESH = 4;

    private volatile long mainSkillCD = 0;

    public GeLaiPuNiWeapon() {
        super(-1);
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.THUNDER;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.ATK;
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("gé lái pǔ ní");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("ge-lai-pu-ni");
    }

    @Override
    protected void threadTick(long ts, long delta) {
        if (mainSkillCD == 0) {
            return;
        }
        if (cd == 0 && (state == STATE_CAN_BE_REFRESHED || state == STATE_USED_AND_CANNOT_REFRESH)) {
            setState(STATE_SKILL_USED);
        }
        var mainSkillCD = this.mainSkillCD;
        if (mainSkillCD < delta) {
            this.mainSkillCD = 0;
            cd = 0;
            setState(STATE_NORMAL);
        } else {
            mainSkillCD -= delta;
            this.mainSkillCD = mainSkillCD;
        }
    }

    @Override
    public long getCoolDown() {
        return Math.min(cd, mainSkillCD);
    }

    @Override
    public double[] getAllCoolDown() {
        if (mainSkillCD == 0) return null;
        long cd = this.cd;
        if (cd == 0 || state == STATE_NORMAL) return new double[]{mainSkillCD / 30_000d};
        return new double[]{mainSkillCD / 30_000d, cd / 15_000d};
    }

    private final LinkedList<Long> clickedTs = new LinkedList<>();
    private long lastTimeSkillUsed = 0;

    @Override
    public boolean useSkill(WeaponContext ctx) {
        long current = System.currentTimeMillis();
        if (stars >= 1) {
            clickedTs.add(current);
            {
                long last = clickedTs.getLast();
                clickedTs.removeIf(ts -> last - ts >= 800);
            }
            if (clickedTs.size() >= 5) {
                long last = clickedTs.getLast();
                long first = clickedTs.getFirst();
                if (last - first < 800) {
                    Logger.debug("ge-lai-pu-ni quick cd refresh: triggered");
                    mainSkillCD = 30 * 1000;
                    cd = 15 * 1000;
                    setState(STATE_CAN_BE_REFRESHED);
                    clickedTs.clear();

                    alertMatrix(ctx);
                    if (current - lastTimeSkillUsed >= 800) {
                        Logger.debug("ge-lai-pu-ni quick cd refresh: free matrix alert");
                        alertMatrix(ctx);
                    }

                    return true;
                }
            }
        }

        if (this.cd != 0) {
            return false;
        }
        try {
            if (state == STATE_NORMAL) {
                if (stars < 1) {
                    cd = 30 * 1000;
                    return true;
                }
                mainSkillCD = 30 * 1000;
                cd = 400;
                setState(STATE_SKILL_USED);
            } else if (state == STATE_SKILL_USED) {
                cd = 15_000;
                setState(STATE_CAN_BE_REFRESHED);
            } else if (state == STATE_IS_REFRESHED) {
                cd = 15 * 1000;
                setState(STATE_USED_AND_CANNOT_REFRESH);
            }
        } finally {
            alertMatrix(ctx);
        }
        lastTimeSkillUsed = current;
        return true;
    }

    private void setState(int state) {
        Logger.debug("ge-lai-pu-ni state change: from " + this.state + " to " + state);
        this.state = state;
    }

    @Override
    public void dodgeAttack(WeaponContext ctx) {
        refreshState2();
    }

    @Override
    public void aimAttack(WeaponContext ctx) {
        refreshState2();
    }

    private void refreshState2() {
        if (state != STATE_CAN_BE_REFRESHED) return;
        setState(STATE_IS_REFRESHED);
        cd = 0;
    }

    @Override
    public void resetCoolDown() {
        setState(STATE_NORMAL);
        mainSkillCD = 0;
        cd = 0;
    }

    @Override
    public void decreaseCoolDown(long time) {
        var oldState = state;
        var oldCD = cd;

        super.decreaseCoolDown(time);
        var mainSkillCD = this.mainSkillCD;
        if (mainSkillCD < time) {
            this.mainSkillCD = 0;
            cd = 0;
            setState(STATE_NORMAL);
        } else {
            mainSkillCD -= time;
            this.mainSkillCD = mainSkillCD;
        }

        if (oldState == STATE_SKILL_USED && state != STATE_NORMAL) {
            // this is not cooldown, it's just here to prevent user from clicking keyboard too fast
            setState(oldState);
            cd = oldCD;
        }
    }
}
