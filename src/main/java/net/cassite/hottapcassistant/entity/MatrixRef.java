package net.cassite.hottapcassistant.entity;

import javafx.scene.image.Image;
import io.vproxy.base.util.coll.Tuple;
import net.cassite.hottapcassistant.data.Matrix;
import net.cassite.hottapcassistant.data.matrix.DummyMatrix;
import net.cassite.hottapcassistant.data.matrix.KeLaoDiYaMatrix;
import net.cassite.hottapcassistant.data.matrix.LeiBeiMatrix;
import net.cassite.hottapcassistant.data.matrix.LinYeMatrix;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MatrixRef {
    public static List<MatrixRef> all() {
        //noinspection unchecked
        Tuple<Integer, Supplier<Matrix>>[] ls = new Tuple[]{
            new Tuple<Integer, Supplier<Matrix>>(0, DummyMatrix::new),
            new Tuple<Integer, Supplier<Matrix>>(1, KeLaoDiYaMatrix::new),
            new Tuple<Integer, Supplier<Matrix>>(2, LinYeMatrix::new),
            new Tuple<Integer, Supplier<Matrix>>(3, LeiBeiMatrix::new),
        };
        var ret = new ArrayList<MatrixRef>();
        for (var pair : ls) {
            ret.add(new MatrixRef(pair._1, pair._2));
        }
        return ret;
    }

    public static Matrix make(List<MatrixRef> ls) {
        var matrix = ls.get(0).matrixSupplier.get();
        int[] stars = new int[ls.size()];
        for (var i = 0; i < ls.size(); ++i) {
            stars[i] = ls.get(i).getStars();
        }
        matrix.init(stars);
        return matrix;
    }

    public final int id;
    public final String name;
    public final Image image;
    private final Supplier<Matrix> matrixSupplier;
    public Supplier<Integer> starsSupplier = null;

    public MatrixRef(int id, Supplier<Matrix> matrixSupplier) {
        this.id = id;
        this.matrixSupplier = matrixSupplier;
        if (matrixSupplier != null) {
            var m = matrixSupplier.get();
            this.name = m.getName();
            this.image = m.getImage();
        } else {
            this.name = "";
            this.image = null;
        }
    }

    public int getStars() {
        if (starsSupplier == null) return 3;
        var stars = starsSupplier.get();
        if (stars == null) return 3;
        return stars;
    }

    @Override
    public String toString() {
        return "MatrixRef{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", stars=" + getStars() +
            '}';
    }
}
