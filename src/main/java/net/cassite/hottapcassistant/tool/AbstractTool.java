package net.cassite.hottapcassistant.tool;

import io.vproxy.base.util.LogType;
import io.vproxy.base.util.Logger;
import io.vproxy.commons.util.IOUtils;
import io.vproxy.vfx.ui.alert.StackTraceAlert;
import io.vproxy.vfx.ui.scene.VScene;
import javafx.scene.image.Image;
import net.cassite.hottapcassistant.config.AssistantConfig;
import vjson.JSON;
import vjson.JSONObject;
import vjson.deserializer.rule.Rule;

import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractTool implements Tool {
    private final String name;
    private final Image icon;
    protected VScene scene = null;
    private Path configPath = null;
    private Rule<? extends JSONObject> configRule = null;
    protected JSONObject config = null;
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

    protected void setConfigRule(String path, Rule<? extends JSONObject> rule) {
        if (configRule != null || configPath != null) {
            throw new IllegalStateException();
        }
        this.configPath = Path.of(AssistantConfig.assistantDirPath.toString(), path);
        this.configRule = rule;
    }

    protected void init(@SuppressWarnings("unused") JSONObject config) {
    }

    private void load() throws Exception {
        if (configRule == null || configPath == null) {
            return;
        }
        JSONObject c = null;
        var configFile = configPath.toFile();
        if (configFile.isFile()) {
            var str = Files.readString(configPath);
            if (!str.isBlank()) {
                try {
                    c = JSON.deserialize(str, configRule);
                } catch (Exception e) {
                    Logger.error(LogType.INVALID_EXTERNAL_DATA, "failed deserializing config from " + configPath, e);
                    // silently delete the file and proceed
                    try {
                        Files.delete(configPath);
                    } catch (Exception ee) {
                        Logger.error(LogType.INVALID_EXTERNAL_DATA, "failed deleting config file " + configPath, ee);
                    }
                }
            }
        }
        if (c != null) {
            init(c);
        }
    }

    protected <T extends JSONObject> T getConfig() {
        //noinspection unchecked
        return (T) config;
    }

    protected void autoSave() {
        if (configRule == null || configPath == null || config == null) {
            return;
        }
        save(config);
    }

    protected void save(JSONObject config) {
        if (configRule == null || configPath == null) {
            throw new IllegalStateException();
        }
        var str = config.toJson().pretty();
        try {
            IOUtils.writeFileWithBackup(configPath.toString(), str);
        } catch (Exception e) {
            Logger.error(LogType.FILE_ERROR, "failed saving config for " + getName(), e);
        }
    }
}
