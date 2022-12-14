package net.cassite.hottapcassistant.data.simulacra;

import net.cassite.hottapcassistant.data.AbstractWithThreadStartStopAndExtraData;
import net.cassite.hottapcassistant.data.Simulacra;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponContext;

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
    public void alertSkillUsed(WeaponContext ctx, Weapon w) {
    }
}
