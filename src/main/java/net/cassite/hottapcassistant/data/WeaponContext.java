package net.cassite.hottapcassistant.data;

import net.cassite.hottapcassistant.component.cooldown.WeaponCoolDown;
import net.cassite.hottapcassistant.component.cooldown.WeaponSpecialInfo;
import net.cassite.hottapcassistant.data.weapon.*;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Logger;
import net.cassite.hottapcassistant.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WeaponContext implements WithExtraData {
    public final List<Weapon> weapons;
    public final Relics[] relics;
    public final Simulacra simulacra;
    public Weapon current;
    public final ResonanceInfo resonanceInfo;

    private volatile Thread thread;
    private BurnSettleContext burnSettleContext;

    private final WeaponCoolDown burnSettleTimer;
    private final long[] weaponSwitchCD;

    private final boolean playAudio;

    public WeaponContext(List<Weapon> weapons, Relics[] relics, Simulacra simulacra, boolean playAudio) {
        if (weapons.isEmpty()) throw new IllegalArgumentException();
        this.weapons = weapons;
        this.relics = relics;
        this.simulacra = simulacra;
        this.resonanceInfo = ResonanceInfo.build(weapons);
        this.current = weapons.get(0);
        this.playAudio = playAudio;

        for (var w : weapons) {
            w.init(this);
        }
        for (var r : relics) {
            if (r == null) continue;
            r.init(this);
        }
        simulacra.init(this);

        burnSettleTimer = new WeaponCoolDown(Utils.getBuffImageFromClasspath("burn-settle"), "burnSettleTimer", I18n.get().buffName("burnSettleTimer"));
        if (needBurnSettle()) {
            extraIndicators.add(burnSettleTimer);
        }
        weaponSwitchCD = new long[weapons.size()];
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
        for (var i = 0; i < weaponSwitchCD.length; ++i) {
            var cd = weaponSwitchCD[i];
            cd = Utils.subtractLongGE0(cd, delta);
            weaponSwitchCD[i] = cd;
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
        Skill skill = current.useSkill(this);
        if (skill != null) {
            postUseSkill(current, skill);
        }
    }

    public void postUseSkill(Weapon cw, Skill skill) {
        Logger.info("use weapon skill " + cw.getName() + "." + skill);
        for (var w : weapons) {
            w.alertSkillUsed(this, cw, skill);
        }
        simulacra.alertSkillUsed(this, cw, skill);
        playSkillAudio(skill);
    }

    private void playSkillAudio(Skill skill) {
        if (!playAudio) return;
        var audio = skill.getAudio();
        if (audio != null) {
            audio.play();
        }
    }

    public void attack() {
        Logger.info("weapon attack " + current.getName());
        current.attack(this, AttackType.NORMAL);
        for (var w : weapons) {
            w.alertAttack(this, current, AttackType.NORMAL);
        }
    }

    public void dodge() {
        Logger.info("weapon dodge " + current.getName());
        current.dodge(this);
    }

    public void dodgeAttack() {
        Logger.info("weapon dodge attack " + current.getName());
        current.attack(this, AttackType.DODGE);
        for (var w : weapons) {
            w.alertAttack(this, current, AttackType.DODGE);
        }
    }

    public void aimAttack() {
        Logger.info("weapon aim/charge attack " + current.getName());
        current.attack(this, AttackType.AIM);
        for (var w : weapons) {
            w.alertAttack(this, current, AttackType.AIM);
        }
    }

    public void specialAttack() {
        Logger.info("weapon special attack " + current.getName());
        current.attack(this, AttackType.SPECIAL);
        for (var w : weapons) {
            w.alertAttack(this, current, AttackType.SPECIAL);
        }
    }

    public boolean switchWeapon(int index, boolean discharge) {
        var w = weapons.get(index);
        if (current == w) {
            return true;
        }
        if (weaponSwitchCD[index] > getTotalSwitchWeaponCoolDown() * 0.1) {
            return false;
        }
        Logger.info("weapon switched from " + current.getName() + " to " + w.getName() + (discharge ? " and discharges" : ""));
        int oldIndex = weapons.indexOf(current);
        if (oldIndex != -1) {
            weaponSwitchCD[oldIndex] = getTotalSwitchWeaponCoolDown();
        }
        current = w;
        for (var ww : weapons) {
            ww.alertWeaponSwitched(this, w, discharge);
        }
        if (discharge) {
            if (playAudio) {
                var group = w.getSkillAudio();
                if (group != null) {
                    group.play();
                }
            }
        }
        return true;
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
        if (yingZhi != null && resonanceInfo.fire()) {
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

    public void jump() {
        current.jump(this);
    }

    public boolean needBurnSettle() {
        var needBurnSettle = false;
        for (var w : weapons) {
            if (w instanceof SiPaKeWeapon) {
                needBurnSettle = true;
                break;
            }
        }
        if (needBurnSettle) {
            needBurnSettle = false;
            for (var w : weapons) {
                if (w instanceof BurnSiYeShiZiWeapon || w instanceof ChiYanZuoLunWeapon || w instanceof LingGuangWeapon) {
                    needBurnSettle = true;
                    break;
                }
            }
        }
        return needBurnSettle;
    }

    private final List<WeaponCoolDown> extraIndicators = new ArrayList<>();
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final List<WeaponSpecialInfo> extraInfo = new ArrayList<>();

    @Override
    public List<WeaponCoolDown> extraIndicators() {
        return Collections.unmodifiableList(extraIndicators);
    }

    @Override
    public List<WeaponSpecialInfo> extraInfo() {
        return Collections.unmodifiableList(extraInfo);
    }

    @Override
    public void updateExtraData() {
        for (var w : weapons) {
            w.updateExtraData();
            var matrix = w.getMatrix();
            for (var m : matrix) {
                m.updateExtraData();
            }
        }
        for (var r : relics) {
            if (r == null) continue;
            r.updateExtraData();
        }
        simulacra.updateExtraData();
        selfUpdateExtraData();
    }

    private void selfUpdateExtraData() {
        if (burnSettleContext != null) {
            var ctx = getBurnSettleContext();
            burnSettleTimer.setAllCoolDown(ctx.getCd(), ctx.getLastTotalCD());
        }
    }

    public long getSwitchWeaponCoolDown(int index) {
        return weaponSwitchCD[index];
    }

    public long getTotalSwitchWeaponCoolDown() {
        return 3_000;
    }

    public void alertDischargeUsed(Weapon source) {
        for (var w : weapons) {
            w.alertWeaponSwitched(this, source, true);
        }
    }
}
