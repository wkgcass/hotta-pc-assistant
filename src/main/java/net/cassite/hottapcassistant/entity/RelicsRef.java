package net.cassite.hottapcassistant.entity;

import io.vproxy.base.util.coll.Tuple;
import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Relics;
import net.cassite.hottapcassistant.data.relics.DiceRelics;
import net.cassite.hottapcassistant.data.relics.DummyRelics;
import net.cassite.hottapcassistant.data.relics.KaoEnTe2Relics;
import net.cassite.hottapcassistant.data.relics.KaoEnTeRelics;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RelicsRef {
    public static List<RelicsRef> all() {
        //noinspection unchecked
        Tuple<Integer, Supplier<Relics>>[] ls = new Tuple[]{
            new Tuple<Integer, Supplier<Relics>>(0, DummyRelics::new),
            new Tuple<Integer, Supplier<Relics>>(1, DiceRelics::new),
            new Tuple<Integer, Supplier<Relics>>(2, KaoEnTeRelics::new),
            new Tuple<Integer, Supplier<Relics>>(3, KaoEnTe2Relics::new),
        };
        var ret = new ArrayList<RelicsRef>();
        for (var pair : ls) {
            ret.add(new RelicsRef(pair._1, pair._2));
        }
        return ret;
    }

    public final int id;
    public final String name;
    public Image image;
    private final Supplier<Relics> relicsSupplier;
    public Supplier<Integer> starsSupplier;

    public RelicsRef(int id, Supplier<Relics> relicsSupplier) {
        this.id = id;
        this.relicsSupplier = relicsSupplier;
        if (relicsSupplier != null) {
            var r = relicsSupplier.get();
            this.name = r.getName();
            this.image = r.getImage();
        } else {
            this.name = "";
            this.image = null;
        }
    }

    public Relics make() {
        var r = relicsSupplier.get();
        r.init(getStars());
        return r;
    }

    public int getStars() {
        if (starsSupplier == null) return 5;
        var stars = starsSupplier.get();
        if (stars == null) return 5;
        return stars;
    }

    @Override
    public String toString() {
        return "RelicsRef{" +
               "id=" + id +
               ", name='" + name + '\'' +
               '}';
    }
}
