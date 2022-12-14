package net.cassite.hottapcassistant.data;

import javafx.scene.image.Image;

public interface Relics extends WithThreadStartStop, WithExtraData {
    String getName();

    Image getImage();

    void init(int stars);

    void init(WeaponContext ctx);

    void use(WeaponContext ctx);

    @SuppressWarnings("unused")
    default void use(WeaponContext ctx, boolean holding) {
        use(ctx);
    }

    long getTime();

    double[] getAllTime();
}
