package net.cassite.hottapcassistant.data;

import net.cassite.hottapcassistant.data.weapon.YingZhiWeapon;
import net.cassite.hottapcassistant.util.Logger;

import java.util.List;

public class WeaponContext {
    public final List<Weapon> weapons;
    public final Relics[] relics;
    public final Simulacra simulacra;
    public Weapon current;
    public final ResonanceInfo resonanceInfo;

    private volatile Thread thread;
    private BurnSettleContext burnSettleContext;

    public WeaponContext(List<Weapon> weapons, Relics[] relics, Simulacra simulacra) {
        if (weapons.isEmpty()) throw new IllegalArgumentException();
        this.weapons = weapons;
        this.relics = relics;
        this.simulacra = simulacra;
        this.resonanceInfo = ResonanceInfo.build(weapons);
        this.current = weapons.get(0);

        for (var w : weapons) {
            w.init(this);
        }
    }

    public void start() {
        for (var w : weapons) {
            w.start();
        }
        for (var r : relics) {
            if (r != null)
                r.start();
        }
        simulacra.start();
        selfStart();
    }

    private void selfStart() {
        if (thread != null) {
            throw new IllegalStateException();
        }
        var thread = new Thread(this::threadRun, "thread-weapon-ctx");
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

            threadTick(ts, delta);
        }
    }

    @SuppressWarnings("unused")
    private void threadTick(long ts, long delta) {
        if (burnSettleContext != null) {
            burnSettleContext.tick(this, delta);
        }
    }

    public void stop() {
        for (var w : weapons) {
            w.stop();
        }
        for (var r : relics) {
            if (r != null)
                r.stop();
        }
        simulacra.stop();
        selfStop();
    }

    private void selfStop() {
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

    public void resetCoolDown() {
        for (var w : weapons) {
            w.resetCoolDown();
        }
    }

    public void decreaseCoolDown(long time) {
        for (var w : weapons) {
            w.decreaseCoolDown(time);
        }
    }

    public void useSkill() {
        boolean ok = current.useSkill(this);
        if (ok) {
            Logger.info("use weapon skill " + current.getName());
            for (var w : weapons) {
                w.alertSkillUsed(this, current);
            }
            simulacra.alertSkillUsed(this, current);
        }
    }

    public void attack() {
        Logger.info("weapon attack " + current.getName());
        current.attack(this);
    }

    public void dodge() {
        Logger.info("weapon dodge " + current.getName());
        current.dodge(this);
    }

    public void dodgeAttack() {
        Logger.info("weapon dodge attack " + current.getName());
        current.dodgeAttack(this);
        for (var w : weapons) {
            w.alertDodgeAttack(this, current);
        }
    }

    public void aimAttack() {
        Logger.info("weapon aim/charge attack " + current.getName());
        current.aimAttack(this);
    }

    public void specialAttack() {
        Logger.info("weapon special attack " + current.getName());
        current.specialAttack(this);
    }

    public void switchWeapon(int index) {
        var w = weapons.get(index);
        if (current == w) {
            return;
        }
        Logger.info("weapon switched from " + current.getName() + " to " + w.getName());
        current = w;
        for (var ww : weapons) {
            ww.alertWeaponSwitched(this, w);
        }
    }

    public void useRelics(int index, boolean holding) {
        if (relics[index] == null) return;
        relics[index].use(this, holding);
    }

    public long calcExtraBurnTime(long t) {
        YingZhiWeapon yingZhi = null;
        for (var w : weapons) {
            if (w instanceof YingZhiWeapon) {
                yingZhi = (YingZhiWeapon) w;
                break;
            }
        }
        if (yingZhi != null && resonanceInfo.fire) {
            if (yingZhi.getFieldTime() > 0) {
                return t + 4_000;
            }
        }
        return t;
    }

    public BurnSettleContext getBurnSettleContext() {
        if (burnSettleContext == null) {
            burnSettleContext = new BurnSettleContext();
        }
        return burnSettleContext;
    }
}
