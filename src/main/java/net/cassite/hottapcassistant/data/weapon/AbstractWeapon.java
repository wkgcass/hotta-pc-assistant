package net.cassite.hottapcassistant.data.weapon;

import io.vproxy.vfx.manager.audio.AudioGroup;
import io.vproxy.vfx.manager.task.TaskManager;
import io.vproxy.base.util.Logger;
import io.vproxy.vfx.util.MiscUtils;
import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.data.skill.NormalSkill;
import net.cassite.hottapcassistant.entity.WeaponArgs;

public abstract class AbstractWeapon extends AbstractWithThreadStartStopAndExtraData implements Weapon {
    protected String name;
    protected Image image;
    protected int stars;
    protected int cooldown;
    protected int attackPointTime;
    protected double considerCDIsClearedRatio = 0.1;
    protected Matrix[] matrix;
    protected WeaponContext ctx;
    protected volatile long cd = 0L;
    protected final AudioGroup skillAudio;

    public AbstractWeapon(int cooldown) {
        this(cooldown, false);
    }

    public AbstractWeapon(int cooldown, int attackPointTime) {
        this(cooldown, false, attackPointTime);
    }

    public AbstractWeapon(int cooldown, boolean isMillis) {
        this(cooldown, isMillis, 0);
    }

    public AbstractWeapon(int cooldown, boolean isMillis, int attackPointTime) {
        this.cooldown = cooldown * (isMillis ? 1 : 1000) + attackPointTime;
        this.attackPointTime = attackPointTime;
        this.name = buildName();
        this.image = buildImage();
        this.skillAudio = buildSkillAudio();
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final Image getImage() {
        return image;
    }

    @Override
    public AudioGroup getSkillAudio() {
        return skillAudio;
    }

    abstract protected String buildName();

    abstract protected Image buildImage();

    abstract protected AudioGroup buildSkillAudio();

    @Override
    public int getStars() {
        return stars;
    }

    @Override
    public void init(int stars, Matrix[] matrix) {
        this.stars = stars;
        this.matrix = matrix;
    }

    @Override
    public void init(WeaponArgs args) {
    }

    @Override
    public void init(WeaponContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public Matrix[] getMatrix() {
        return matrix;
    }

    @Override
    protected String getThreadName() {
        return "thread-" + getName();
    }

    @Override
    public void start() {
        super.start();
        for (var m : matrix) {
            m.start();
        }
    }

    @Override
    public void stop() {
        super.stop();
        for (var m : matrix) {
            m.stop();
        }
    }

    @Override
    protected final void mainThreadTick(long ts, long delta) {
        var cd = this.cd;
        if (cd > delta) {
            cd -= delta;
            this.cd = cd;
        } else {
            this.cd = 0;
        }
        super.mainThreadTick(ts, delta);
    }

    @Override
    public long getCoolDown() {
        return cd;
    }

    @Override
    public double[] getAllCoolDown() {
        if (cd == 0) return null;
        return new double[]{cd / (double) cooldown};
    }

    @Override
    public Skill pressSkill(WeaponContext ctx) {
        if (this.cd != 0) {
            long cd = this.cd;
            if (cooldown * considerCDIsClearedRatio < cd) {
                return null;
            }
        }
        return pressSkillIgnoreCD(ctx);
    }

    protected Skill pressSkillIgnoreCD(WeaponContext ctx) {
        var skill = pressSkill0(ctx);
        if (skill == null) {
            return null;
        }
        postPressSkill(ctx, skill);
        return skill;
    }

    protected Skill pressSkill0(WeaponContext ctx) {
        return null;
    }

    protected void postPressSkill(WeaponContext ctx, Skill skill) {
    }

    @Override
    public Skill useSkill(WeaponContext ctx) {
        if (this.cd != 0) {
            long cd = this.cd;
            if (cooldown * considerCDIsClearedRatio < cd) {
                return null;
            }
        }
        return useSkillIgnoreCD(ctx);
    }

    protected Skill useSkillIgnoreCD(WeaponContext ctx) {
        var skill = useSkill0(ctx);
        if (skill == null) {
            return null;
        }
        if (isRevertibleSkill(ctx) && attackPointTime > 0) {
            postUseRevertibleSkill(ctx, skill);
            return null;
        } else {
            postUseSkill(ctx, skill);
            return skill;
        }
    }

    private volatile int revertibleSkillStateVersion = 0;
    private volatile boolean handlingRevertibleSkill = false;

    private void postUseRevertibleSkill(WeaponContext ctx, Skill skill) {
        revertSkillIfNeeded(ctx);
        handlingRevertibleSkill = true;
        var oldVersion = ++revertibleSkillStateVersion;
        TaskManager.get().execute(() -> {
            MiscUtils.threadSleep(attackPointTime);
            if (oldVersion != revertibleSkillStateVersion) {
                assert Logger.lowLevelDebug("the skill of " + getName() + " was reverted");
                return;
            }
            postRevertibleSkill(ctx, skill);
            handlingRevertibleSkill = false;
        });
    }

    private void postRevertibleSkill(WeaponContext ctx, Skill skill) {
        postUseSkill(ctx, skill);
        ctx.postUseSkill(this, skill);
    }

    private void revertSkillIfNeeded(@SuppressWarnings("unused") WeaponContext ctx) {
        if (isRevertibleSkill(ctx) && handlingRevertibleSkill) {
            ++revertibleSkillStateVersion;
            handlingRevertibleSkill = false;
            revertSkill0(ctx);
        }
    }

    protected void revertSkill0(WeaponContext ctx) {
        cd = 0;
    }

    protected Skill skillInstance() {
        return new NormalSkill(skillAudio);
    }

    @SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted"})
    protected Skill useSkill0(WeaponContext ctx) {
        cd = cooldown;
        return skillInstance();
    }

    protected void postUseSkill(WeaponContext ctx, Skill skill) {
        for (var m : matrix) {
            m.useSkill(ctx, this, skill);
        }
    }

    @Override
    public void attack(WeaponContext ctx, AttackType type) {
        attack0(ctx, type);
        postAttack(ctx, type);
    }

    protected void attack0(@SuppressWarnings("unused") WeaponContext ctx, @SuppressWarnings("unused") AttackType type) {
    }

    protected void postAttack(WeaponContext ctx, AttackType type) {
        for (var m : matrix) {
            m.attack(ctx, this, type);
        }
    }

    @Override
    public void dodge(WeaponContext ctx) {
        revertSkillIfNeeded(ctx);
        dodge0(ctx);
    }

    protected void dodge0(@SuppressWarnings("unused") WeaponContext ctx) {
    }

    @Override
    public void jump(WeaponContext ctx) {
        revertSkillIfNeeded(ctx);
    }

    protected boolean isRevertibleSkill(WeaponContext ctx) {
        return false;
    }

    @Override
    public void alertSkillUsed(WeaponContext ctx, Weapon w, Skill skill) {
        alertSkillUsed0(ctx, w, skill);
        postAlertSkillUsed(ctx, w, skill);
    }

    protected void alertSkillUsed0(WeaponContext ctx, Weapon w, Skill skill) {
    }

    @SuppressWarnings("unused")
    protected void postAlertSkillUsed(WeaponContext ctx, Weapon w, Skill skill) {
    }

    @Override
    public void alertAttack(WeaponContext ctx, Weapon w, AttackType type) {
        alertAttack0(ctx, w, type);
        postAlertAttack(ctx, w, type);
    }

    protected void alertAttack0(@SuppressWarnings("unused") WeaponContext ctx,
                                @SuppressWarnings("unused") Weapon w,
                                @SuppressWarnings("unused") AttackType type) {
    }

    @SuppressWarnings("unused")
    protected void postAlertAttack(WeaponContext ctx, Weapon w, AttackType type) {
    }

    @Override
    public void alertWeaponSwitched(WeaponContext ctx, Weapon w, boolean discharge) {
        revertSkillIfNeeded(ctx);
        alertWeaponSwitched0(ctx, w, discharge);
    }

    protected void alertWeaponSwitched0(WeaponContext ctx, Weapon w, boolean discharge) {
    }

    @Override
    public void triggerDischarge(WeaponContext ctx, boolean withDischargeEffect) {
    }

    @Override
    public void resetCoolDown() {
        cd = 0;
    }

    @Override
    public void decreaseCoolDown(long time) {
        long cd = this.cd;
        if (cd < time) {
            this.cd = 0;
        } else {
            this.cd = cd - time;
        }
    }
}
