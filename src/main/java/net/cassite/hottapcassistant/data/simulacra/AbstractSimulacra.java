package net.cassite.hottapcassistant.data.simulacra;

import net.cassite.hottapcassistant.data.*;

public abstract class AbstractSimulacra extends AbstractWithThreadStartStopAndExtraData implements Simulacra {
    protected final String name;

    public AbstractSimulacra() {
        this.name = buildName();
    }

    @Override
    public final String getName() {
        return name;
    }

    abstract protected String buildName();

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
