package net.cassite.hottapcassistant.data.weapon;

import io.vproxy.vfx.manager.audio.AudioGroup;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import net.cassite.hottapcassistant.component.cooldown.WeaponCoolDown;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.entity.AssistantCoolDownOptions;
import net.cassite.hottapcassistant.entity.AssistantCoolDownYueXingChuanSanLiuSkill;
import net.cassite.hottapcassistant.entity.YueXingChuanSanLiuSkillOptions;
import net.cassite.hottapcassistant.entity.WeaponArgs;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class YueXingChuanWeapon extends AbstractWeapon implements Weapon {
    private final WeaponCoolDown shuiYiShuoHuaBuff = new WeaponCoolDown(
        Utils.getBuffImageFromClasspath("shui-yi-shuo-hua"),
        "shuiYiShuoHuaBuff", I18n.get().buffName("shuiYiShuoHuaBuff")
    );
    private SanLiu main;
    private SanLiu sub = null;
    private boolean autoDischarge = false;
    private long dischargeBuffTime = 0;

    public YueXingChuanWeapon() {
        super(-1, 500);
        extraIndicatorList.add(shuiYiShuoHuaBuff);
    }

    @Override
    public String getId() {
        return "yue-xing-chuan";
    }

    @Override
    public void init(WeaponArgs args) {
        if (args instanceof AssistantCoolDownOptions opts) {
            autoDischarge = opts.autoDischargeForYueXingChuan;
        }
        if (!(args instanceof YueXingChuanSanLiuSkillOptions opts)) {
            return;
        }
        main = getSanLiu(opts.skill1);
        if (main == null)
            main = new PuLongYing();
        sub = getSanLiu(opts.skill2);

        super.totalCoolDown = (int) (main.totalCD() + super.attackPointTime);
        if (sub != null) {
            var indicator = sub.cdIndicator();
            indicator.setEffect(new Glow(0.5));
            extraIndicatorList.add(indicator);
        }

        var mainBuff = main.buffIndicator();
        if (mainBuff != null)
            extraIndicatorList.add(mainBuff);
        var subBuff = sub == null ? null : sub.buffIndicator();
        if (subBuff != null)
            extraIndicatorList.add(subBuff);
    }

    private static SanLiu getSanLiu(AssistantCoolDownYueXingChuanSanLiuSkill s) {
        if (s == null)
            return null;
        return switch (s) {
            case JU_SHUI -> new JuShui();
            case YONG_JUAN -> new YongJuan();
            case TAO_YA -> new TaoYa();
            case WO_XUAN -> new WoXuan();
            case YU_GU -> new YuGu();
            case ZI_QUAN -> new ZiQuan();
        };
    }

    @Override
    public WeaponElement element() {
        return WeaponElement.ALTERED;
    }

    @Override
    public WeaponCategory category() {
        return WeaponCategory.SUPPORT;
    }

    @Override
    protected void threadTick(long ts, long delta) {
        main.threadTick(delta);
        if (sub != null)
            sub.threadTick(delta);
        dischargeBuffTime = Utils.subtractLongGE0(dischargeBuffTime, delta);
    }

    @Override
    public void updateExtraData() {
        shuiYiShuoHuaBuff.setAllCoolDown(dischargeBuffTime, getTotalDischargeBuffTime());
        main.updateExtraData();
        if (sub != null)
            sub.updateExtraData();
    }

    @Override
    protected Skill useSkill0(WeaponContext ctx) {
        var skill = super.useSkill0(ctx);
        main.use(ctx);
        return skill;
    }

    public void useAdditionalSkill(WeaponContext ctx) {
        if (sub == null)
            return;
        sub.use(ctx);
    }

    @Override
    protected void alertWeaponSwitched0(WeaponContext ctx, Weapon w, boolean discharge) {
        if (w != this) return;
        if (!discharge && !autoDischarge) return;
        dischargeBuffTime = getTotalDischargeBuffTime() + 2_000 /* it takes 2 seconds to apply damage */;
        if (main instanceof TaoYa taoya)
            taoya.resetCD();
        if (sub instanceof TaoYa taoya)
            taoya.resetCD();
    }

    public long getDischargeBuffTime() {
        return dischargeBuffTime;
    }

    public long getTotalDischargeBuffTime() {
        return 30_000;
    }

    @Override
    protected String buildName() {
        return I18n.get().weaponName("yuè xīng chuàn");
    }

    @Override
    protected Image buildImage() {
        return Utils.getWeaponImageFromClasspath("yue-xing-chuan");
    }

    @Override
    protected AudioGroup buildSkillAudio() {
        return Utils.getSkillAudioGroup("fei-ou-na", 5);
    }

    private interface SanLiu {
        void init(boolean isMain);

        long totalCD();

        WeaponCoolDown cdIndicator();

        WeaponCoolDown buffIndicator();

        boolean use(WeaponContext ctx);

        void threadTick(long delta);

        void updateExtraData();
    }

    private static abstract class AbstractSanLiu implements SanLiu {
        final WeaponCoolDown cdIndicator;
        final WeaponCoolDown buffIndicator;
        long currentCD = 0;
        long buffTime = 0;
        private boolean isMain = false;

        protected AbstractSanLiu(WeaponCoolDown cdIndicator, WeaponCoolDown buffIndicator) {
            this.cdIndicator = cdIndicator;
            this.buffIndicator = buffIndicator;
        }

        @Override
        public void init(boolean isMain) {
            this.isMain = isMain;
        }

        @Override
        public boolean use(WeaponContext ctx) {
            if (!isMain) { // for main skill, the cd check is already done before calling this method
                if (currentCD > 0) {
                    return false;
                }
            }
            currentCD = totalCD();
            buffTime = totalBuffTime();
            return true;
        }

        @Override
        public void threadTick(long delta) {
            currentCD = Utils.subtractLongGE0(currentCD, delta);
            buffTime = Utils.subtractLongGE0(buffTime, delta);
        }

        @Override
        public void updateExtraData() {
            if (cdIndicator != null) {
                cdIndicator.setAllCoolDown(currentCD, totalCD());
            }
            if (buffIndicator != null) {
                buffIndicator.setAllCoolDown(buffTime, totalBuffTime());
            }
        }

        @Override
        public WeaponCoolDown cdIndicator() {
            return cdIndicator;
        }

        @Override
        public WeaponCoolDown buffIndicator() {
            return buffIndicator;
        }

        abstract int totalBuffTime();
    }

    private static class PuLongYing extends AbstractSanLiu implements SanLiu {
        protected PuLongYing() {
            super(null, null);
        }

        @Override
        public long totalCD() {
            return 30_000;
        }

        @Override
        int totalBuffTime() {
            return 0;
        }
    }

    private static class JuShui extends AbstractSanLiu implements SanLiu {
        protected JuShui() {
            super(
                new WeaponCoolDown(
                    Utils.getSkillImageFromClasspath("ju-shui"),
                    "ju-shui", I18n.get().yueXingChuanSanLiuSkillCoolDownDesc(I18n.get().yueXingChuanJuShuiSkill())),
                new WeaponCoolDown(
                    Utils.getSkillImageFromClasspath("ju-shui"),
                    "ju-shui-buff", I18n.get().yueXingChuanSanLiuSkillBuffDesc(I18n.get().yueXingChuanJuShuiSkill()))
            );
        }

        @Override
        public long totalCD() {
            return 14_000;
        }

        @Override
        int totalBuffTime() {
            return 20_000;
        }
    }

    private static class YongJuan extends AbstractSanLiu implements SanLiu {
        protected YongJuan() {
            super(
                new WeaponCoolDown(
                    Utils.getSkillImageFromClasspath("yong-juan"),
                    "yong-juan", I18n.get().yueXingChuanSanLiuSkillCoolDownDesc(I18n.get().yueXingChuanYongJuanSkill())),
                null
            );
        }

        @Override
        public long totalCD() {
            return 12_000;
        }

        @Override
        int totalBuffTime() {
            return 0;
        }
    }

    private static class TaoYa extends AbstractSanLiu implements SanLiu {
        protected TaoYa() {
            super(
                new WeaponCoolDown(
                    Utils.getSkillImageFromClasspath("tao-ya"),
                    "tao-ya", I18n.get().yueXingChuanSanLiuSkillCoolDownDesc(I18n.get().yueXingChuanTaoYaSkill())),
                new WeaponCoolDown(
                    Utils.getSkillImageFromClasspath("tao-ya"),
                    "tao-ya-buff", I18n.get().yueXingChuanSanLiuSkillBuffDesc(I18n.get().yueXingChuanTaoYaSkill()))
            );
        }

        @Override
        public boolean use(WeaponContext ctx) {
            boolean ok = super.use(ctx);
            if (ok) {
                ctx.current.triggerDischarge(ctx, false);
            }
            return ok;
        }

        @Override
        public long totalCD() {
            return 30_000;
        }

        @Override
        int totalBuffTime() {
            return 35_000;
        }

        public void resetCD() {
            currentCD = 0;
        }
    }

    private static class WoXuan extends AbstractSanLiu implements SanLiu {
        protected WoXuan() {
            super(
                new WeaponCoolDown(
                    Utils.getSkillImageFromClasspath("wo-xuan"),
                    "wo-xuan", I18n.get().yueXingChuanSanLiuSkillCoolDownDesc(I18n.get().yueXingChuanWoXuanSkill())),
                null
            );
        }

        @Override
        public long totalCD() {
            return 30_000;
        }

        @Override
        int totalBuffTime() {
            return 0;
        }
    }

    private static class YuGu extends AbstractSanLiu implements SanLiu {
        protected YuGu() {
            super(
                new WeaponCoolDown(
                    Utils.getSkillImageFromClasspath("yu-gu"),
                    "yu-gu", I18n.get().yueXingChuanSanLiuSkillCoolDownDesc(I18n.get().yueXingChuanYuGuSkill())),
                null
            );
        }

        @Override
        public long totalCD() {
            return 15_000;
        }

        @Override
        int totalBuffTime() {
            return 0;
        }
    }

    private static class ZiQuan extends AbstractSanLiu implements SanLiu {
        protected ZiQuan() {
            super(
                new WeaponCoolDown(
                    Utils.getSkillImageFromClasspath("zi-quan"),
                    "zi-quan", I18n.get().yueXingChuanSanLiuSkillCoolDownDesc(I18n.get().yueXingChuanZiQuanSkill())),
                new WeaponCoolDown(
                    Utils.getSkillImageFromClasspath("zi-quan"),
                    "zi-quan-buff", I18n.get().yueXingChuanSanLiuSkillBuffDesc(I18n.get().yueXingChuanZiQuanSkill()))
            );
        }

        @Override
        public long totalCD() {
            return 15_000;
        }

        @Override
        int totalBuffTime() {
            return 20_000;
        }
    }
}
