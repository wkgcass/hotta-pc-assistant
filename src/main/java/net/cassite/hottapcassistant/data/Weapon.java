package net.cassite.hottapcassistant.data;

import javafx.scene.image.Image;

public interface Weapon {
    String getName();

    Image getImage();

    WeaponElement element();

    WeaponCategory category();

    void init(int stars, Matrix[] matrix);

    void start();

    void stop();

    long getCoolDown();

    double[] getAllCoolDown();

    boolean useSkill(WeaponContext ctx);

    void attack(WeaponContext ctx);

    void dodge(WeaponContext ctx);

    void dodgeAttack(WeaponContext ctx);

    void aimAttack(WeaponContext ctx);

    void specialAttack(WeaponContext ctx);

    void alertSkillUsed(WeaponContext ctx, Weapon w);

    void alertDodgeAttack(WeaponContext ctx, Weapon w);

    void alertWeaponSwitched(WeaponContext ctx, Weapon w);

    void resetCoolDown();

    void decreaseCoolDown(long time);
}
