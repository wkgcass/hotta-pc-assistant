package net.cassite.hottapcassistant.data;

import net.cassite.hottapcassistant.component.cooldown.WeaponCoolDown;
import net.cassite.hottapcassistant.component.cooldown.WeaponSpecialInfo;

import java.util.List;

public interface WithExtraData {
    List<WeaponCoolDown> extraIndicators();

    List<WeaponSpecialInfo> extraInfo();

    void updateExtraData();
}
