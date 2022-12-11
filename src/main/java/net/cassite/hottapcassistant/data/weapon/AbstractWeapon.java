package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Matrix;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponContext;

public abstract class AbstractWeapon implements Weapon {
    protected String name;
    protected Image image;
    protected int stars;
    protected int cooldown;
    protected double considerCDIsClearedRatio = 0.1;
    protected Matrix[] matrix;
    protected WeaponContext ctx;
    private volatile Thread thread;
    protected volatile long cd = 0L;

    protected AbstractWeapon(int cooldown) {
        this(cooldown, false);
    }

    protected AbstractWeapon(int cooldown, int attackPointTime) {
        this(cooldown, false, attackPointTime);
    }

    protected AbstractWeapon(int cooldown, boolean isMillis) {
        this(cooldown, isMillis, 0);
    }

    protected AbstractWeapon(int cooldown, boolean isMillis, int attackPointTime) {
        this.cooldown = cooldown * (isMillis ? 1 : 1000) + attackPointTime;
        this.name = buildName();
        this.image = buildImage();
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final Image getImage() {
        return image;
    }

    abstract protected String buildName();

    abstract protected Image buildImage();

    @Override
    public int getStars() {
        return stars;
    }

    @Override
    public void init(int stars, Matrix[] matrix) {
        this.stars = stars;
        this.matrix = matrix;
    }

    @Override
    public void init(WeaponContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void start() {
        cd = 0;

        if (thread != null) {
            throw new IllegalStateException();
        }
        var thread = new Thread(this::threadRun, "thread-" + getName());
        this.thread = thread;
        thread.start();
    }

    private void threadRun() {
        long lastTs = System.currentTimeMillis();
        while (this.thread != null) {
            try {
                //noinspection BusyWait
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
            long ts = System.currentTimeMillis();
            long delta = ts - lastTs;
            lastTs = ts;

            var cd = this.cd;
            if (cd > delta) {
                cd -= delta;
                this.cd = cd;
            } else {
                this.cd = 0;
            }

            threadTick(ts, delta);
        }
    }

    @SuppressWarnings("unused")
    protected void threadTick(long ts, long delta) {
    }

    @Override
    public void stop() {
        var thread = this.thread;
        if (thread != null) {
            this.thread = null;
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException ignore) {
            }
        }
    }

    @Override
    public long getCoolDown() {
        return cd;
    }

    @Override
    public double[] getAllCoolDown() {
        if (cd == 0) return null;
        return new double[]{cd / (double) cooldown};
    }

    @Override
    public boolean useSkill(WeaponContext ctx) {
        if (this.cd != 0) {
            long cd = this.cd;
            if (cooldown * considerCDIsClearedRatio < cd) {
                return false;
            }
        }
        cd = cooldown;
        alertMatrix(ctx);
        return true;
    }

    protected void alertMatrix(WeaponContext ctx) {
        for (var m : matrix) {
            m.useSkill(ctx, this, skillHitTarget());
        }
    }

    protected boolean skillHitTarget() {
        return true;
    }

    @Override
    public void attack(WeaponContext ctx) {
    }

    @Override
    public void dodge(WeaponContext ctx) {
    }

    @Override
    public void dodgeAttack(WeaponContext ctx) {
    }

    @Override
    public void aimAttack(WeaponContext ctx) {
    }

    @Override
    public void specialAttack(WeaponContext ctx) {
    }

    @Override
    public void alertSkillUsed(WeaponContext ctx, Weapon w) {
    }

    @Override
    public void alertDodgeAttack(WeaponContext ctx, Weapon w) {
    }

    @Override
    public void alertWeaponSwitched(WeaponContext ctx, Weapon w) {
    }

    @Override
    public void resetCoolDown() {
        cd = 0;
    }

    @Override
    public void decreaseCoolDown(long time) {
        long cd = this.cd;
        if (cd < time) {
            this.cd = 0;
        } else {
            this.cd = cd - time;
        }
    }
}
