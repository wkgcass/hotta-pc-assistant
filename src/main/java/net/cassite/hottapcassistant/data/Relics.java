package net.cassite.hottapcassistant.data;

import javafx.scene.image.Image;

public interface Relics extends WithThreadStartStop {
    String getName();

    Image getImage();

    void init(int stars);

    void use(WeaponContext ctx);

    @SuppressWarnings("unused")
    default void use(WeaponContext ctx, boolean holding) {
        use(ctx);
    }

    long getTime();

    double[] getAllTime();
}
