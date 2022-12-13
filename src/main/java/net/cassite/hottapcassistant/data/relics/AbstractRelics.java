package net.cassite.hottapcassistant.data.relics;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.AbstractWithThreadStartStop;
import net.cassite.hottapcassistant.data.Relics;

public abstract class AbstractRelics extends AbstractWithThreadStartStop implements Relics {
    protected final String name;
    protected final Image image;
    protected int stars;

    public AbstractRelics() {
        this.name = buildName();
        this.image = buildImage();
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final Image getImage() {
        return image;
    }

    abstract protected String buildName();

    abstract protected Image buildImage();

    @Override
    public void init(int stars) {
        this.stars = stars;
    }

    @Override
    protected String getThreadName() {
        return "thread-" + getName();
    }
}
