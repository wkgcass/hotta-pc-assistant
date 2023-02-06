package net.cassite.hottapcassistant.entity;

import javafx.scene.image.Image;
import kotlin.Pair;
import net.cassite.hottapcassistant.data.Simulacra;
import net.cassite.hottapcassistant.data.simulacra.AiLiSiSimulacra;
import net.cassite.hottapcassistant.data.simulacra.DummySimulacra;
import net.cassite.hottapcassistant.data.simulacra.XingHuanSimulacra;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SimulacraRef {
    public static List<SimulacraRef> all() {
        //noinspection unchecked
        Pair<Integer, Supplier<Simulacra>>[] ls = new Pair[]{
            new Pair<Integer, Supplier<Simulacra>>(0, DummySimulacra::new),
            new Pair<Integer, Supplier<Simulacra>>(1, XingHuanSimulacra::new),
            new Pair<Integer, Supplier<Simulacra>>(2, AiLiSiSimulacra::new),
        };
        var ret = new ArrayList<SimulacraRef>();
        for (var pair : ls) {
            ret.add(new SimulacraRef(pair.component1(), pair.component2()));
        }
        return ret;
    }

    public final int id;
    public final String name;
    public Image image;
    public final Supplier<Simulacra> simulacraSupplier;

    public SimulacraRef(int id, Supplier<Simulacra> simulacraSupplier) {
        this.id = id;
        this.simulacraSupplier = simulacraSupplier;
        if (simulacraSupplier == null) {
            name = "";
            this.image = null;
        } else {
            var s = simulacraSupplier.get();
            name = s.getName();
            this.image = s.getImage();
        }
    }

    public Simulacra make() {
        return simulacraSupplier.get();
    }

    @Override
    public String toString() {
        return "SimulacraRef{" +
               "index=" + id +
               ", name='" + name + '\'' +
               '}';
    }
}
