package net.cassite.hottapcassistant.data;

public interface Simulacra extends WithThreadStartStop {
    String getName();

    void alertSkillUsed(WeaponContext ctx, Weapon w);
}
