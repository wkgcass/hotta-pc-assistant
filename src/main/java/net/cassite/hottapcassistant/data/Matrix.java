package net.cassite.hottapcassistant.data;

import javafx.scene.image.Image;

public interface Matrix {
    String getName();

    Image getImage();

    void init(int[] stars);

    void useSkill(WeaponContext ctx, Weapon w, boolean hitTarget);
}
