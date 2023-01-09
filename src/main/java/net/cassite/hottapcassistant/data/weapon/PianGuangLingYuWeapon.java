package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.Cursor;
import javafx.scene.image.Image;
import net.cassite.hottapcassistant.component.cooldown.WeaponSpecialInfo;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.entity.AssistantCoolDownOptions;
import net.cassite.hottapcassistant.entity.WeaponArgs;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.AudioGroup;
import net.cassite.hottapcassistant.util.Utils;

public class PianGuangLingYuWeapon extends AbstractWeapon implements Weapon {
    private int subSkillCount = 0;
    private long antiFalseTouch = 0;
    private final WeaponSpecialInfo guiJiCounter = new WeaponSpecialInfo(Utils.getBuffImageFromClasspath("gui-ji"), "guiJiCounter", I18n.get().buffName("guiJiCounter"));
    private boolean autoFillSubSkill = false;

    public PianGuangLingYuWeapon() {
        super(60);
        guiJiCounter.setOnMouseClicked(e -> addCount());
        guiJiCounter.setCursor(Cursor.HAND);
        extraInfoList.add(guiJiCounter);
    }

    @Override
    public String getId() {
        return "pian-guang-ling-yu";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.ICE;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.CARRY;
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("piàn guāng líng yǚ");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("pian-guang-ling-yu");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("yi-ka-luo-si", 5);
    }

    @Override
    public void init(WeaponArgs args) {
        if (args instanceof AssistantCoolDownOptions) {
            autoFillSubSkill = ((AssistantCoolDownOptions) args).autoFillPianGuangLingYuSubSkill;
        }
    }

    @Override
    protected void threadTick(long ts, long delta) {
        antiFalseTouch = Utils.subtractLongGE0(antiFalseTouch, delta);
    }

    @Override
    public Skill useSkill(WeaponContext ctx) {
        if (cd > 0) {
            if (subSkillCount > 0) {
                if (antiFalseTouch > 200) { // 2_000 * (1 - 0.9)
                    return null;
                } else {
                    antiFalseTouch = 2_000;
                    subSkillCount -= 1;
                    return skillInstance();
                }
            } else {
                return null;
            }
        } else {
            antiFalseTouch = 1_000;
            return super.useSkill(ctx);
        }
    }

    @Override
    protected void alertWeaponSwitched0(WeaponContext ctx, Weapon w, boolean discharge) {
        if (w == this) {
            if (discharge || autoFillSubSkill) {
                subSkillCount = 2;
            }
        }
    }

    @Override
    public long getCoolDown() {
        if (cd > 0) {
            if (subSkillCount > 0) {
                return antiFalseTouch;
            } else {
                return cd;
            }
        } else {
            return cd;
        }
    }

    @Override
    public double[] getAllCoolDown() {
        if (cd > 0) {
            if (subSkillCount == 0 || antiFalseTouch == 0) {
                return super.getAllCoolDown();
            } else {
                return new double[]{
                    cd / (double) this.cooldown,
                    antiFalseTouch / 3_000d,
                };
            }
        } else {
            return super.getAllCoolDown();
        }
    }

    @Override
    public void updateExtraData() {
        guiJiCounter.setText("" + subSkillCount);
    }

    public void addCount() {
        if (subSkillCount == 2) {
            subSkillCount = 0;
        } else {
            ++subSkillCount;
        }
    }
}
