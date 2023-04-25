package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.Cursor;
import javafx.scene.image.Image;
import net.cassite.hottapcassistant.component.cooldown.WeaponCoolDown;
import net.cassite.hottapcassistant.component.cooldown.WeaponSpecialInfo;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.data.skill.NoHitSkill;
import net.cassite.hottapcassistant.entity.AssistantCoolDownOptions;
import net.cassite.hottapcassistant.entity.WeaponArgs;
import net.cassite.hottapcassistant.i18n.I18n;
import io.vproxy.vfx.manager.audio.AudioGroup;
import net.cassite.hottapcassistant.util.Utils;

public class YingZhiWeapon extends AbstractWeapon implements Weapon {
    private long antiFalseTouchCD;
    private long fieldTime;
    private final WeaponCoolDown yingYueZhiJingBuffTimer;
    private final WeaponSpecialInfo star6Counter;
    private boolean handleDischargeAlert = false;
    private boolean hasDischargeAcquiredSkill = true;
    private int dischargeAcquiredSkillCounter = 0;

    public YingZhiWeapon() {
        super(30);
        yingYueZhiJingBuffTimer = new WeaponCoolDown(Utils.getBuffImageFromClasspath("ying-yue-zhi-jing"), "yingYueZhiJingBuffTimer", I18n.get().buffName("yingYueZhiJingBuffTimer"));
        star6Counter = new WeaponSpecialInfo(getImage(), "yingZhiStar6Counter", I18n.get().buffName("yingZhiStar6Counter"));
        extraIndicatorList.add(yingYueZhiJingBuffTimer);

        star6Counter.setOnMouseClicked(e -> increaseDischargeAcquiredSkillCount());
        star6Counter.setCursor(Cursor.HAND);
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("yǐng zhī");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("ying-zhi");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("lin-ye", 5);
    }

    @Override
    public String getId() {
        return "ying-zhi";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.ALTERED;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.DPS;
    }

    @Override
    public void init(int stars, Matrix[] matrix) {
        super.init(stars, matrix);
    }

    @Override
    public void init(WeaponArgs args) {
        if (args instanceof AssistantCoolDownOptions) {
            handleDischargeAlert = ((AssistantCoolDownOptions) args).applyDischargeForYingZhi;
            if (handleDischargeAlert && stars >= 6) {
                extraInfoList.add(star6Counter);
            }
        }
    }

    @Override
    public Skill useSkill(WeaponContext ctx) {
        if (antiFalseTouchCD != 0) {
            return null;
        }
        if (super.useSkill(ctx) != null) {
            antiFalseTouchCD = 300;
            resetFieldTime();
            return skillInstance();
        }
        if (stars >= 6) {
            if (handleDischargeAlert) {
                if (antiFalseTouchCD == 0) {
                    if (hasDischargeAcquiredSkill) {
                        hasDischargeAcquiredSkill = false;
                        resetFieldTime();
                        postUseSkill(ctx, skillInstance());
                        return skillInstance();
                    }
                }
            } else {
                resetFieldTime(); // no cd check, user may hit skill button very quickly
                if (antiFalseTouchCD == 0) {
                    antiFalseTouchCD = 3000;
                    postUseSkill(ctx, skillInstance());
                    return skillInstance();
                }
            }
        }
        return null;
    }

    private void resetFieldTime() {
        if (stars >= 3) {
            fieldTime = 20 * 1000;
        } else {
            fieldTime = 15 * 1000;
        }
    }

    @Override
    protected void threadTick(long ts, long delta) {
        var antiFalseTouchCD = this.antiFalseTouchCD;
        if (antiFalseTouchCD < delta) {
            this.antiFalseTouchCD = 0;
        } else {
            antiFalseTouchCD -= delta;
            this.antiFalseTouchCD = antiFalseTouchCD;
        }
        var fieldTime = this.fieldTime;
        if (fieldTime < delta) {
            this.fieldTime = 0;
        } else {
            fieldTime -= delta;
            this.fieldTime = fieldTime;
        }
    }

    public long getFieldTime() {
        return fieldTime;
    }

    public long getTotalFieldTime() {
        return stars >= 3 ? 20_000 : 15_000;
    }

    @Override
    protected Skill skillInstance() {
        return new NoHitSkill(skillAudio);
    }

    @Override
    protected void alertWeaponSwitched0(WeaponContext ctx, Weapon w, boolean discharge) {
        if (w != this)
            return;
        if (!discharge) {
            return;
        }
        if (!handleDischargeAlert) {
            return;
        }
        triggerDischarge(ctx, true);
    }

    @Override
    public void triggerDischarge(WeaponContext ctx, boolean withDischargeEffect) {
        increaseDischargeAcquiredSkillCount();
    }

    private void increaseDischargeAcquiredSkillCount() {
        if (dischargeAcquiredSkillCounter >= 2) {
            dischargeAcquiredSkillCounter = 0;
            hasDischargeAcquiredSkill = true;
        } else {
            ++dischargeAcquiredSkillCounter;
        }
    }

    @Override
    public long getCoolDown() {
        if (stars >= 6) {
            if (handleDischargeAlert) {
                if (hasDischargeAcquiredSkill) {
                    return antiFalseTouchCD;
                } else {
                    return super.getCoolDown();
                }
            } else {
                return antiFalseTouchCD;
            }
        } else {
            return super.getCoolDown();
        }
    }

    @Override
    public void updateExtraData() {
        yingYueZhiJingBuffTimer.setAllCoolDown(getFieldTime(), getTotalFieldTime());
        star6Counter.setText("" + dischargeAcquiredSkillCounter);
    }
}
