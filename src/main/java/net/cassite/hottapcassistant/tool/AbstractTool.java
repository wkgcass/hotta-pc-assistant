package net.cassite.hottapcassistant.tool;

import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.cassite.hottapcassistant.ui.JSONJavaObject;
import net.cassite.hottapcassistant.util.Logger;
import net.cassite.hottapcassistant.util.StackTraceAlert;
import net.cassite.hottapcassistant.util.Utils;
import vjson.JSON;
import vjson.deserializer.rule.Rule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractTool implements Tool {
    private final String name;
    private final Image icon;
    protected Stage stage;
    private Path configPath = null;
    private Rule<? extends JSONJavaObject> configRule = null;
    protected JSONJavaObject config = null;

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
        if (stage != null) {
            throw new IllegalStateException();
        }
        try {
            stage = buildStage();
            load();
        } catch (Exception e) {
            new StackTraceAlert(e).showAndWait();
            stage = null;
            return;
        }
        if (stage != null) {
            if (stage.getTitle() == null || stage.getTitle().isBlank()) {
                stage.setTitle(getName());
            }
            if (stage.getIcons().isEmpty()) {
                stage.getIcons().add(getIcon());
            }
            stage.setOnCloseRequest(e -> terminate(false));
            stage.show();
        }
    }

    protected abstract String buildName();

    protected abstract Image buildIcon();

    protected abstract Stage buildStage() throws Exception;

    @Override
    public boolean isRunning() {
        return stage != null;
    }

    @Override
    public void alert() {
        var stage = this.stage;
        if (stage != null) {
            stage.requestFocus();
        }
    }

    @Override
    public void terminate() {
        terminate(true);
    }

    protected void terminate(boolean callClose) {
        terminate0();
        var stage = this.stage;
        this.stage = null;
        if (stage != null) {
            autoSave();
            if (callClose) {
                stage.close();
            }
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
            Utils.writeFile(configPath, str);
        } catch (IOException e) {
            Logger.error("failed saving config for " + getName(), e);
        }
    }
}
