package net.cassite.hottapcassistant.data;

import javafx.scene.image.Image;

public interface Relics {
    String getName();

    Image getImage();

    void init(int stars);

    void start();

    void stop();

    void use(WeaponContext ctx);

    long getTime();

    double[] getAllTime();
}
