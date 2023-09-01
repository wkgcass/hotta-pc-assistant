package net.cassite.hottapcassistant.data.relics;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.AbstractWithThreadStartStopAndExtraData;
import net.cassite.hottapcassistant.data.Relics;
import net.cassite.hottapcassistant.data.WeaponContext;

public abstract class AbstractRelics extends AbstractWithThreadStartStopAndExtraData implements Relics {
    protected final String name;
    protected Image image;
    protected int stars;

    public AbstractRelics() {
        this.name = buildName();
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
    public void init(int stars) {
        this.stars = stars;
    }

    @Override
    public void init(WeaponContext ctx) {
    }

    @Override
    protected String getThreadName() {
        return "thread-" + getName();
    }
}
