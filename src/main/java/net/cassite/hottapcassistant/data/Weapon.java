package net.cassite.hottapcassistant.data;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.entity.WeaponArgs;
import net.cassite.hottapcassistant.util.AudioGroup;

public interface Weapon extends WithThreadStartStop, WithExtraData {
    String getId();

    String getName();

    Image getImage();

    AudioGroup getSkillAudio();

    WeaponElement element();

    WeaponCategory category();

    int getStars();

    void init(int stars, Matrix[] matrix);

    void init(WeaponArgs args);

    void init(WeaponContext ctx);

    Matrix[] getMatrix();

    long getCoolDown();

    double[] getAllCoolDown();

    Skill useSkill(WeaponContext ctx);

    void attack(WeaponContext ctx, AttackType type);

    void dodge(WeaponContext ctx);

    void jump(WeaponContext ctx);

    void alertSkillUsed(WeaponContext ctx, Weapon w, Skill skill);

    void alertAttack(WeaponContext ctx, Weapon w, AttackType type);

    void alertWeaponSwitched(WeaponContext ctx, Weapon w, boolean discharge);

    void resetCoolDown();

    void decreaseCoolDown(long time);
}
