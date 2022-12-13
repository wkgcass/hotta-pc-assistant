package net.cassite.hottapcassistant.data;

import javafx.scene.image.Image;

public interface Matrix extends WithThreadStartStop {
    String getName();

    Image getImage();

    void init(int[] stars);

    int[] getEffectiveStars();

    void useSkill(WeaponContext ctx, Weapon w);

    void attack(WeaponContext ctx, Weapon w, AttackType type);
}
