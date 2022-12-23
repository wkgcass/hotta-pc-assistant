package net.cassite.hottapcassistant.data;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.entity.WeaponArgs;

public interface Weapon extends WithThreadStartStop, WithExtraData {
    String getId();

    String getName();

    Image getImage();

    WeaponElement element();

    WeaponCategory category();

    int getStars();

    void init(int stars, Matrix[] matrix);

    void init(WeaponArgs args);

    void init(WeaponContext ctx);

    Matrix[] getMatrix();

    long getCoolDown();

    double[] getAllCoolDown();

    boolean useSkill(WeaponContext ctx);

    boolean skillHitTarget();

    void attack(WeaponContext ctx, AttackType type);

    void dodge(WeaponContext ctx);

    void jump(WeaponContext ctx);

    void alertSkillUsed(WeaponContext ctx, Weapon w);

    void alertAttack(WeaponContext ctx, Weapon w, AttackType type);

    void alertWeaponSwitched(WeaponContext ctx, Weapon w, boolean discharge);

    void resetCoolDown();

    void decreaseCoolDown(long time);
}
