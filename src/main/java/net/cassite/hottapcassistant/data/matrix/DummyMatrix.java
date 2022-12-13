package net.cassite.hottapcassistant.data.matrix;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Matrix;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class DummyMatrix extends AbstractMatrix implements Matrix {
    public DummyMatrix() {
    }

    @Override
    protected String buildName() {
        return I18n.get().matrixName("?");
    }

    @Override
    protected Image buildImage() {
        return Utils.getMatrixImageFromClasspath("dummy");
    }
}
