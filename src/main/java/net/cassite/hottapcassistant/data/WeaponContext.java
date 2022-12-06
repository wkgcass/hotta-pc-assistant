package net.cassite.hottapcassistant.data;

import net.cassite.hottapcassistant.util.Logger;

import java.util.List;

public class WeaponContext {
    public final List<Weapon> weapons;
    public Weapon current;

    public WeaponContext(List<Weapon> weapons) {
        if (weapons.isEmpty()) throw new IllegalArgumentException();
        this.weapons = weapons;
        this.current = weapons.get(0);
    }

    public void start() {
        for (var w : weapons) {
            w.start();
        }
    }

    public void stop() {
        for (var w : weapons) {
            w.stop();
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
                w.alertSkillUsed(this, w);
            }
        }
    }

    public void attack() {
        Logger.info("weapon attack " + current.getName());
        current.attack(this);
    }

    public void dodgeAttack() {
        Logger.info("weapon dodge attack " + current.getName());
        current.dodgeAttack(this);
        for (var w : weapons) {
            w.alertDodgeAttack(this, w);
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
}
