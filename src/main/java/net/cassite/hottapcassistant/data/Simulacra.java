package net.cassite.hottapcassistant.data;

public interface Simulacra {
    String getName();

    void start();

    void stop();

    void alertSkillUsed(WeaponContext ctx, Weapon w);
}
