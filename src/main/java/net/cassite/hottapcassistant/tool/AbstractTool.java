package net.cassite.hottapcassistant.tool;

import io.vproxy.vfx.ui.alert.StackTraceAlert;
import io.vproxy.vfx.ui.scene.VScene;
import io.vproxy.vfx.util.IOUtils;
import io.vproxy.vfx.util.Logger;
import javafx.scene.image.Image;
import net.cassite.hottapcassistant.ui.JSONJavaObject;
import vjson.JSON;
import vjson.deserializer.rule.Rule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractTool implements Tool {
    private final String name;
    private final Image icon;
    protected VScene scene = null;
    private Path configPath = null;
    private Rule<? extends JSONJavaObject> configRule = null;
    protected JSONJavaObject config = null;
    protected Runnable runOnTerminated = null;

    public AbstractTool() {
        name = buildName();
        icon = buildIcon();
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final Image getIcon() {
        return icon;
    }

    @Override
    public void launch() {
        if (scene != null) {
            throw new IllegalStateException();
        }
        try {
            scene = buildScene();
            load();
        } catch (Exception e) {
            scene = null;
            StackTraceAlert.showAndWait(e);
        }
    }

    protected abstract String buildName();

    protected abstract Image buildIcon();

    protected abstract VScene buildScene() throws Exception;

    @Override
    public boolean isRunning() {
        return scene != null;
    }

    @Override
    public VScene getScene() {
        return scene;
    }

    @Override
    public void setOnTerminated(Runnable f) {
        if (runOnTerminated != null)
            throw new IllegalStateException();
        runOnTerminated = f;
    }

    @Override
    public void terminate() {
        terminate0();
        var runOnTerminated = this.runOnTerminated;
        this.runOnTerminated = null;
        if (runOnTerminated != null) {
            runOnTerminated.run();
        }
        var scene = this.scene;
        this.scene = null;
        if (scene != null) {
            autoSave();
        }
    }

    protected void terminate0() {
    }

    protected void setConfigRule(Path path, Rule<? extends JSONJavaObject> rule) {
        if (configRule != null || configPath != null) {
            throw new IllegalStateException();
        }
        this.configPath = path;
        this.configRule = rule;
    }

    protected void init(@SuppressWarnings("unused") JSONJavaObject config) {
    }

    private void load() throws Exception {
        if (configRule == null || configPath == null) {
            return;
        }
        JSONJavaObject c = null;
        var configFile = configPath.toFile();
        if (configFile.isFile()) {
            var str = Files.readString(configPath);
            if (!str.isBlank()) {
                try {
                    c = JSON.deserialize(str, configRule);
                } catch (Exception e) {
                    Logger.error("failed deserializing config from " + configPath, e);
                    // silently delete the file and proceed
                    try {
                        Files.delete(configPath);
                    } catch (Exception ee) {
                        Logger.error("failed deleting config file " + configPath, ee);
                    }
                }
            }
        }
        if (c != null) {
            init(c);
        }
    }

    protected <T extends JSONJavaObject> T getConfig() {
        //noinspection unchecked
        return (T) config;
    }

    protected void autoSave() {
        if (configRule == null || configPath == null || config == null) {
            return;
        }
        save(config);
    }

    protected void save(JSONJavaObject config) {
        if (configRule == null || configPath == null) {
            throw new IllegalStateException();
        }
        var str = config.toJson().pretty();
        try {
            IOUtils.writeFile(configPath, str);
        } catch (IOException e) {
            Logger.error("failed saving config for " + getName(), e);
        }
    }
}
