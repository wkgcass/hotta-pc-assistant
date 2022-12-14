package net.cassite.hottapcassistant.data.relics;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.component.cooldown.WeaponCoolDown;
import net.cassite.hottapcassistant.data.Relics;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class KaoEnTeRelics extends AbstractRelics implements Relics {
    private static final long buffTakeEffectTime = 5_000;
    protected long buffTime = 10_000;
    private int state = 0;
    // 0: normal
    // 1: used but without effect
    // 2: used and with effect
    private long time1 = 0;
    private long time2 = 0;

    private final WeaponCoolDown kaoEnTeBuffTimer;

    public KaoEnTeRelics() {
        kaoEnTeBuffTimer = new WeaponCoolDown(getImage(), I18n.get().buffName("kaoEnTeBuffTimer"));
        extraIndicatorList.add(kaoEnTeBuffTimer);
    }

    @Override
    public void use(WeaponContext ctx) {
        state = 1;
        time1 = buffTakeEffectTime;
        time2 = buffTime;
    }

    @Override
    protected void threadTick(long ts, long delta) {
        var time1 = this.time1;
        if (time1 > 0) {
            if (time1 < delta) {
                if (state == 1) {
                    this.time1 = buffTime;
                    state = 2;
                } else {
                    this.time1 = 0;
                    state = 0;
                }
            } else {
                time1 -= delta;
                this.time1 = time1;
            }
        }
        var time2 = this.time2;
        if (time2 > 0) {
            if (time2 < delta) {
                this.time2 = 0;
            } else {
                time2 -= delta;
                this.time2 = time2;
            }
        }
    }

    @Override
    public long getTime() {
        return time1;
    }

    @Override
    public double[] getAllTime() {
        if (time1 == 0 && time2 == 0) return null;
        double t1;
        if (state == 1) {
            t1 = time1 / (double) buffTakeEffectTime;
        } else {
            t1 = time1 / (double) buffTime;
        }
        if (time2 == 0) {
            return new double[]{t1};
        } else {
            return new double[]{t1, time2 / (double) buffTime};
        }
    }

    @Override
    protected String buildName() {
        return I18n.get().relicsName("kǎo-ēn-tè");
    }

    @Override
    protected Image buildImage() {
        return Utils.getRelicsImageFromClasspath("kao-en-te");
    }

    @Override
    public void updateExtraData() {
        kaoEnTeBuffTimer.setCoolDown(getTime());
        kaoEnTeBuffTimer.setAllCoolDown(getAllTime());
    }
}
