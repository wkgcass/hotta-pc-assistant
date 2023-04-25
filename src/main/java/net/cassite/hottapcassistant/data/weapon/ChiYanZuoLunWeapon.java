package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.component.cooldown.WeaponCoolDown;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.i18n.I18n;
import io.vproxy.vfx.manager.audio.AudioGroup;
import net.cassite.hottapcassistant.util.Utils;

public class ChiYanZuoLunWeapon extends AbstractWeapon implements Weapon {
    private long burnBuff = 0;
    private long lastDecreaseCD = 0;

    private final WeaponCoolDown liZiZhuoShaoBuffTimer;

    public ChiYanZuoLunWeapon() {
        super(60, 200);
        liZiZhuoShaoBuffTimer = new WeaponCoolDown(Utils.getBuffImageFromClasspath("li-zi-zhuo-shao"), "liZiZhuoShaoBuffTimer", I18n.get().buffName("liZiZhuoShaoBuffTimer"));
        extraIndicatorList.add(liZiZhuoShaoBuffTimer);
    }

    @Override
    public String getId() {
        return "chi-yan-zuo-lun";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.FLAME;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.DPS;
    }

    @Override
    protected void threadTick(long ts, long delta) {
        burnBuff = Utils.subtractLongGE0(burnBuff, delta);
        if (stars >= 6 && cd > 0 && burnBuff > 0) {
            if (ts - lastDecreaseCD >= 2000) { // the burn tick does not correspond to the timer, so the interval will be longer
                cd = Utils.subtractLongGE0(cd, 4000);
                lastDecreaseCD = ts;
            }
        }
    }

    @Override
    protected void attack0(WeaponContext ctx, AttackType type) {
        if (type == AttackType.AIM) {
            burn(ctx);
        }
    }

    @Override
    protected void alertAttack0(WeaponContext ctx, Weapon w, AttackType type) {
        if (type == AttackType.DODGE) {
            if (w == this || stars >= 5) {
                burn(ctx);
            }
        }
    }

    private void burn(WeaponContext ctx) {
        if (stars >= 3) {
            var burnBuff = getTotalBurnBuff();
            burnBuff = ctx.calcExtraBurnTime(burnBuff);
            this.burnBuff = burnBuff;
        }
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("chì yàn zuǒ lún");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("chi-yan-zuo-lun");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("gu-lan", 3);
    }

    public long getBurnBuff() {
        return burnBuff;
    }

    public long getTotalBurnBuff() {
        return 10_000;
    }

    @Override
    public void updateExtraData() {
        liZiZhuoShaoBuffTimer.setAllCoolDown(getBurnBuff(), getTotalBurnBuff());
    }
}
