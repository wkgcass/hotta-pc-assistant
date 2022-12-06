package net.cassite.hottapcassistant.data.relics;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Relics;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

public class KaoEnTe2Relics extends KaoEnTeRelics implements Relics {
    public KaoEnTe2Relics() {
        buffTime = 12_000;
    }

    @Override
    protected String buildName() {
        return I18n.get().relicsName("kǎo-ēn-tè-2");
    }

    @Override
    protected Image buildImage() {
        return Utils.getRelicsImageFromClasspath("kao-en-te-2");
    }
}
