package net.cassite.hottapcassistant.tool;

import io.vproxy.vfx.manager.image.ImageManager;
import io.vproxy.vfx.ui.scene.VScene;
import javafx.scene.image.Image;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.multi.MultiHottaInstanceConfig;
import net.cassite.hottapcassistant.multi.MultiHottaInstanceScene;
import vjson.JSONObject;

public class MultiHottaInstance extends AbstractTool implements Tool {
    public MultiHottaInstance() {
        setConfigRule("MultiHottaInstance.vjson.txt", MultiHottaInstanceConfig.rule);
    }

    @Override
    protected String buildName() {
        return I18n.get().toolName("multi-hotta-instance");
    }

    @Override
    protected Image buildIcon() {
        return ImageManager.get().load("/images/icon/multi-hotta-instance.png");
    }

    @Override
    protected VScene buildScene() {
        return new MultiHottaInstanceScene(this);
    }

    @Override
    protected void init(JSONObject config) {
        var s = (MultiHottaInstanceScene) scene;
        s.init((MultiHottaInstanceConfig) config);
    }

    public void save(MultiHottaInstanceConfig config) {
        super.save(config);
    }

    @Override
    protected void terminate0() {
        var s = (MultiHottaInstanceScene) scene;
        if (s != null) {
            s.terminate();
        }
    }

    public void launchMod() {
        var s = (MultiHottaInstanceScene) scene;
        if (s != null) {
            s.launch(true);
        }
    }
}
