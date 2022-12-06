package net.cassite.hottapcassistant.data.matrix;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Matrix;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class DummyMatrix implements Matrix {
    private final String name = I18n.get().matrixName("?");
    private final Image image = Utils.getMatrixImageFromClasspath("dummy");

    public DummyMatrix() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Image getImage() {
        return image;
    }

    @Override
    public void init(int[] stars) {
    }

    @Override
    public void useSkill(WeaponContext ctx, Weapon w, boolean hitTarget) {
    }
}
