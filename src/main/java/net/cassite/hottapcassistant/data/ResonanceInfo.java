package net.cassite.hottapcassistant.data;

import net.cassite.hottapcassistant.data.resonance.FireResonance;
import net.cassite.hottapcassistant.data.resonance.IceResonance;
import net.cassite.hottapcassistant.data.resonance.PhysicsResonance;
import net.cassite.hottapcassistant.data.resonance.ThunderResonance;
import net.cassite.hottapcassistant.data.weapon.*;

import java.util.List;

public record ResonanceInfo(boolean carry, boolean support, boolean tank, boolean balance,
                            boolean fire, boolean ice,
                            boolean thunder, boolean physics, boolean balancedElement,
                            boolean fireResonance, boolean iceResonance,
                            boolean thunderResonance, boolean physicsResonance) {

    public static ResonanceInfo build(List<Weapon> weapons) {
        int carryCount = 0;
        int supportCount = 0;
        int tankCount = 0;
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
                case FLAME -> fireCount++;
                case FROST -> iceCount++;
                case VOLT -> thunderCount++;
                case PHYSICAL -> physicsCount++;
            }
            switch (w.category()) {
                case DPS -> carryCount++;
                case SUPPORT -> supportCount++;
                case DEFENSE -> tankCount++;
            }
            if (w instanceof YingZhiWeapon) {
                hasYingZhi = true;
            } else if (w instanceof FireResonance) {
                hasFireResonance = true;
            } else if (w instanceof IceResonance) {
                hasIceResonance = true;
            } else if (w instanceof ThunderResonance) {
                hasThunderResonance = true;
            } else if (w instanceof PhysicsResonance) {
                hasPhysicsResonance = true;
            }
        }
        return new ResonanceInfo(
            carryCount >= 2,
            supportCount >= 2,
            tankCount >= 2,
            carryCount <= 1 && supportCount <= 1 && tankCount <= 1,
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
