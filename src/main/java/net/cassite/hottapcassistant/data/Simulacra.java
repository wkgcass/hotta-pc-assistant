package net.cassite.hottapcassistant.data;

public interface Simulacra extends WithThreadStartStop, WithExtraData {
    String getName();

    void init(WeaponContext ctx);

    void alertSkillUsed(WeaponContext ctx, Weapon w, Skill skill);
}
