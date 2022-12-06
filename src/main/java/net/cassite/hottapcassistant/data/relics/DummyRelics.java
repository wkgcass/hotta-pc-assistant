package net.cassite.hottapcassistant.data.relics;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Relics;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class DummyRelics extends AbstractRelics implements Relics {
    @Override
    public void use(WeaponContext ctx) {
    }

    @Override
    public long getTime() {
        return 0;
    }

    @Override
    public double[] getAllTime() {
        return null;
    }

    @Override
    protected String buildName() {
        return I18n.get().relicsName("?");
    }

    @Override
    protected Image buildImage() {
        return Utils.getRelicsImageFromClasspath("dummy");
    }
}
