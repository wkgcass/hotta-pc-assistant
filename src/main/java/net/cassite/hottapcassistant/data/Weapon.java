package net.cassite.hottapcassistant.data;

import io.vproxy.vfx.manager.audio.AudioGroup;
import javafx.scene.image.Image;
import net.cassite.hottapcassistant.entity.WeaponArgs;

public interface Weapon extends WithThreadStartStop, WithExtraData {
    String getId();

    String getName();

    Image getImage();

    AudioGroup getSkillAudio();

    WeaponElement element();

    WeaponCategory category();

    int getStars();

    // order:first
    void init(int stars, Matrix[] matrix);

    // order:second
    void init(WeaponArgs args);

    // order:third
    void init(WeaponContext ctx);

    Matrix[] getMatrix();

    long getCoolDown();

    double[] getAllCoolDown();

    Skill pressSkill(WeaponContext ctx);

    Skill useSkill(WeaponContext ctx);

    void attack(WeaponContext ctx, AttackType type);

    void dodge(WeaponContext ctx);

    void jump(WeaponContext ctx);

    void alertSkillUsed(WeaponContext ctx, Weapon w, Skill skill);

    void alertAttack(WeaponContext ctx, Weapon w, AttackType type);

    void alertWeaponSwitched(WeaponContext ctx, Weapon w, boolean discharge);

    void triggerDischarge(WeaponContext ctx, boolean withDischargeEffect);

    void resetCoolDown();

    void decreaseCoolDown(long time);
}
