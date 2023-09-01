package net.cassite.hottapcassistant.data.relics;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.component.cooldown.WeaponCoolDown;
import net.cassite.hottapcassistant.data.Relics;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class DiceRelics extends AbstractRelics implements Relics {
    private static final long totalCDStar2 = 45 * 1000;
    private static final long totalCDStar0 = 35 * 1000;
    private long cd = 0;

    private final WeaponCoolDown diceBuffTimer;

    public DiceRelics() {
        diceBuffTimer = new WeaponCoolDown(this::getImage, "diceBuffTimer", I18n.get().buffName("diceBuffTimer"));
        extraIndicatorList.add(diceBuffTimer);
    }

    @Override
    public void use(WeaponContext ctx) {
        buff();
        ctx.resetCoolDown();
    }

    @Override
    public void use(WeaponContext ctx, boolean holding) {
        if (!holding) {
            use(ctx);
            return;
        }
        buff();
    }

    private void buff() {
        cd = stars >= 2 ? totalCDStar2 : totalCDStar0;
    }

    @Override
    protected void threadTick(long ts, long delta) {
        var cd = this.cd;
        if (cd > 0) {
            if (cd < delta) {
                this.cd = 0;
            } else {
                cd -= delta;
                this.cd = cd;
            }
        }
    }

    @Override
    public long getTime() {
        return cd;
    }

    @Override
    public double[] getAllTime() {
        var cd = this.cd;
        if (cd == 0) return null;
        return new double[]{cd / (double) (stars >= 2 ? totalCDStar2 : totalCDStar0)};
    }

    @Override
    protected String buildName() {
        return I18n.get().relicsName("dice");
    }

    @Override
    protected Image buildImage() {
        return Utils.getRelicsImageFromClasspath("dice");
    }

    @Override
    public void updateExtraData() {
        diceBuffTimer.setCoolDown(getTime());
        diceBuffTimer.setAllCoolDown(getAllTime());
    }
}
