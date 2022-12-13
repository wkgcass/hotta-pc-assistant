package net.cassite.hottapcassistant.data;

import net.cassite.hottapcassistant.data.weapon.*;

import java.util.List;

public record ResonanceInfo(boolean atk, boolean sup, boolean def, boolean balance,
                            boolean fire, boolean ice,
                            boolean thunder, boolean physics, boolean balancedElement,
                            boolean fireResonance, boolean iceResonance,
                            boolean thunderResonance, boolean physicsResonance) {

    public static ResonanceInfo build(List<Weapon> weapons) {
        int atkCount = 0;
        int supCount = 0;
        int defCount = 0;
        int fireCount = 0;
        int iceCount = 0;
        int thunderCount = 0;
        int physicsCount = 0;
        boolean hasYingZhi = false;
        boolean hasFireResonance = false;
        boolean hasIceResonance = false;
        boolean hasThunderResonance = false;
        boolean hasPhysicsResonance = false;
        for (var w : weapons) {
            switch (w.element()) {
                case FIRE -> fireCount++;
                case ICE -> iceCount++;
                case THUNDER -> thunderCount++;
                case PHYSICS -> physicsCount++;
            }
            switch (w.category()) {
                case CARRY -> atkCount++;
                case SUPPORT -> supCount++;
                case TANK -> defCount++;
            }
            if (w instanceof YingZhiWeapon) {
                hasYingZhi = true;
            } else if (w instanceof SiPaKeWeapon || w instanceof LingGuangWeapon || w instanceof FouJueLiFangWeapon) {
                hasFireResonance = true;
            } else if (w instanceof LiuQuanCheXinWeapon) {
                hasIceResonance = true;
            } else if (w instanceof QiMingXingWeapon || w instanceof GeLaiPuNiWeapon) {
                hasThunderResonance = true;
            } else if (w instanceof WanDaoWeapon) {
                hasPhysicsResonance = true;
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
            fireCount <= 1 && iceCount <= 1 && thunderCount <= 1 && physicsCount <= 1,
            hasFireResonance && (fireCount >= 2 || hasYingZhi),
            hasIceResonance && (iceCount >= 2 || hasYingZhi),
            hasThunderResonance && (thunderCount >= 2 || hasYingZhi),
            hasPhysicsResonance && (physicsCount >= 2 || hasYingZhi)
        );
    }
}
