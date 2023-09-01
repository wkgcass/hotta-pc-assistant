package net.cassite.hottapcassistant.data.matrix;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.*;

import java.util.ArrayList;

public abstract class AbstractMatrix extends AbstractWithThreadStartStopAndExtraData implements Matrix {
    private final String name;
    private Image image;
    protected int[] stars = new int[5];

    protected AbstractMatrix() {
        this.name = buildName();
        stars[0] = -1;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final Image getImage() {
        if (image == null) {
            image = buildImage();
        }
        return image;
    }

    abstract protected String buildName();

    abstract protected Image buildImage();

    @Override
    public void init(int[] stars) {
        var sorted = new ArrayList<Integer>();
        for (var s : stars) sorted.add(s);
        sorted.sort((a, b) -> b - a); // desc
        while (sorted.size() > 4) {
            sorted.remove(sorted.size() - 1);
        }
        while (sorted.size() < 4) {
            sorted.add(-1);
        }
        for (int i = 1; i <= 4; ++i) {
            this.stars[i] = sorted.get(i - 1);
        }
    }

    @Override
    public int[] getEffectiveStars() {
        return stars;
    }

    @Override
    protected String getThreadName() {
        return "thread-" + getName();
    }

    @Override
    public void useSkill(WeaponContext ctx, Weapon w, Skill skill) {
    }

    @Override
    public void attack(WeaponContext ctx, Weapon w, AttackType type) {
    }
}
