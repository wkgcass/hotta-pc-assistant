package net.cassite.hottapcassistant.data.simulacra;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Simulacra;
import net.cassite.hottapcassistant.i18n.I18n;

public class DummySimulacra extends AbstractSimulacra implements Simulacra {
    public DummySimulacra() {
    }

    @Override
    protected String buildName() {
        return I18n.get().simulacraName("?");
    }

    @Override
    protected Image buildImage() {
        return null;
    }
}
