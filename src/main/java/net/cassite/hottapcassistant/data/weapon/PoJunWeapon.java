package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.component.cooldown.WeaponCoolDown;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.i18n.I18n;
import io.vproxy.vfx.manager.audio.AudioGroup;
import net.cassite.hottapcassistant.util.Utils;

public class PoJunWeapon extends AbstractWeapon implements Weapon {
    private long leiDianGanYingTime = 0;
    private final WeaponCoolDown leiDianGanYing = new WeaponCoolDown(Utils.getBuffImageFromClasspath("lei-dian-gan-ying"), "leiDianGanYing", I18n.get().buffName("leiDianGanYing"));

    public PoJunWeapon() {
        super(30);
        extraIndicatorList.add(leiDianGanYing);
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("pò jūn");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("po-jun");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("tian-lang", 5);
    }

    @Override
    public String getId() {
        return "po-jun";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.VOLT;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.DPS;
    }

    @Override
    protected void threadTick(long ts, long delta) {
        leiDianGanYingTime = Utils.subtractLongGE0(leiDianGanYingTime, delta);
    }

    @Override
    protected Skill useSkill0(WeaponContext ctx) {
        var s = super.useSkill0(ctx);
        if (s == null) {
            return null;
        }
        leiDianGanYingTime = getTotalLeiDianGanYingTime();
        return s;
    }

    public long getLeiDianGanYingTime() {
        return leiDianGanYingTime;
    }

    public long getTotalLeiDianGanYingTime() {
        return 25_000;
    }

    @Override
    public void updateExtraData() {
        leiDianGanYing.setAllCoolDown(leiDianGanYingTime, getTotalLeiDianGanYingTime());
    }
}
