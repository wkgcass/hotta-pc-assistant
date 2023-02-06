package net.cassite.hottapcassistant.data;

import javafx.scene.image.Image;

public interface Simulacra extends WithThreadStartStop, WithExtraData {
    String getName();

    Image getImage();

    void init(WeaponContext ctx);

    void alertSkillUsed(WeaponContext ctx, Weapon w, Skill skill);
}
