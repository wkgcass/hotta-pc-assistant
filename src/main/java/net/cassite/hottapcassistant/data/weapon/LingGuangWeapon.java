package net.cassite.hottapcassistant.data.weapon;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.component.cooldown.WeaponCoolDown;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.data.resonance.FireResonance;
import net.cassite.hottapcassistant.i18n.I18n;
import io.vproxy.vfx.manager.audio.AudioGroup;
import net.cassite.hottapcassistant.util.Utils;

public class LingGuangWeapon extends AbstractWeapon implements Weapon, FireResonance {
    private final WeaponCoolDown lingGuangYuJing = new WeaponCoolDown(Utils.getBuffImageFromClasspath("ling-guang-yu-jing"), "lingGuangYuJing", I18n.get().buffName("lingGuangYuJing"));
    private final WeaponCoolDown lingGuangTaunt = new WeaponCoolDown(Utils.getBuffImageFromClasspath("ling-guang-taunt"), "lingGuangTaunt", I18n.get().buffName("lingGuangTaunt"));
    private long tauntTime = 0;
    private long lingGuangYuJingTime = 0;

    public LingGuangWeapon() {
        super(12);
        extraIndicatorList.add(lingGuangYuJing);
    }

    @Override
    public String getId() {
        return "ling-guang";
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.FIRE;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.TANK;
    }

    @Override
    protected void threadTick(long ts, long delta) {
        tauntTime = Utils.subtractLongGE0(tauntTime, delta);
        lingGuangYuJingTime = Utils.subtractLongGE0(lingGuangYuJingTime, delta);
    }

    @Override
    public void init(WeaponContext ctx) {
        super.init(ctx);
        if (ctx.resonanceInfo.tank()) {
            extraIndicatorList.add(lingGuangTaunt);
        }
    }

    @Override
    protected Skill useSkill0(WeaponContext ctx) {
        var skill = super.useSkill0(ctx);
        if (skill == null) return null;
        lingGuangYuJingTime = getTotalLingGuangYuJingTime();
        return skill;
    }

    @Override
    public void alertSkillUsed0(WeaponContext ctx, Weapon w, Skill skill) {
        tauntTime = getTotalTauntTime();
    }

    @Override
    public void updateExtraData() {
        lingGuangYuJing.setAllCoolDown(getLingGuangYuJingTime(), getTotalLingGuangYuJingTime());
        lingGuangTaunt.setAllCoolDown(getTauntTime(), getTotalTauntTime());
    }

    public long getTauntTime() {
        return tauntTime;
    }

    public long getTotalTauntTime() {
        return 2_500;
    }

    public long getLingGuangYuJingTime() {
        return lingGuangYuJingTime;
    }

    public long getTotalLingGuangYuJingTime() {
        return 8_000;
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("líng guāng");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("ling-guang");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("lan", 5);
    }
}
