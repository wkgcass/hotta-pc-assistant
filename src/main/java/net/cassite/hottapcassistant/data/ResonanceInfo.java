package net.cassite.hottapcassistant.data;

import java.util.List;

public class ResonanceInfo {
    public final boolean atk;
    public final boolean sup;
    public final boolean def;
    public final boolean balance;
    public final boolean fire;
    public final boolean ice;
    public final boolean thunder;
    public final boolean pyhsics;
    public final boolean balancedElement;

    public ResonanceInfo(
        boolean atk,
        boolean sup,
        boolean def,
        boolean balance,
        boolean fire,
        boolean ice,
        boolean thunder,
        boolean pyhsics,
        boolean balancedElement) {

        this.atk = atk;
        this.sup = sup;
        this.def = def;
        this.balance = balance;
        this.fire = fire;
        this.ice = ice;
        this.thunder = thunder;
        this.pyhsics = pyhsics;
        this.balancedElement = balancedElement;
    }

    public static ResonanceInfo build(List<Weapon> weapons) {
        int atkCount = 0;
        int supCount = 0;
        int defCount = 0;
        int fireCount = 0;
        int iceCount = 0;
        int thunderCount = 0;
        int physicsCount = 0;
        for (var w : weapons) {
            switch (w.element()) {
                case FIRE -> fireCount++;
                case ICE -> iceCount++;
                case THUNDER -> thunderCount++;
                case PHYSICS -> physicsCount++;
            }
            switch (w.category()) {
                case ATK -> atkCount++;
                case SUP -> supCount++;
                case DEF -> defCount++;
            }
        }
        return new ResonanceInfo(
            atkCount >= 2,
            supCount >= 2,
            defCount >= 2,
            atkCount <= 1 && supCount <= 1 && defCount <= 1,
            fireCount >= 2,
            iceCount >= 2,
            thunderCount >= 2,
            physicsCount >= 2,
            fireCount <= 1 && iceCount <= 1 && thunderCount <= 1 && physicsCount <= 1
        );
    }
}
