package net.cassite.hottapcassistant.data.simulacra;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.*;

public abstract class AbstractSimulacra extends AbstractWithThreadStartStopAndExtraData implements Simulacra {
    protected final String name;
    protected Image image;

    public AbstractSimulacra() {
        this.name = buildName();
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public Image getImage() {
        if (image == null) {
            image = buildImage();
        }
        return image;
    }

    abstract protected String buildName();

    abstract protected Image buildImage();

    @Override
    protected String getThreadName() {
        return "thread-" + getName();
    }

    @Override
    public void init(WeaponContext ctx) {
    }

    @Override
    public void alertSkillUsed(WeaponContext ctx, Weapon w, Skill skill) {
    }
}
