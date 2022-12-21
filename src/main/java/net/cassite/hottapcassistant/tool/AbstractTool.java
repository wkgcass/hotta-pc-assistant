package net.cassite.hottapcassistant.tool;

import javafx.scene.image.Image;
import javafx.stage.Stage;

public abstract class AbstractTool implements Tool {
    private final String name;
    private final Image icon;
    protected Stage stage;

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
        stage = buildStage();
        if (stage != null) {
            stage.setOnCloseRequest(e -> {
                stage = null;
                terminate0();
            });
            stage.show();
        }
    }

    protected abstract String buildName();

    protected abstract Image buildIcon();

    protected abstract Stage buildStage();

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
        terminate0();
        var stage = this.stage;
        this.stage = null;
        if (stage != null) {
            stage.close();
        }
    }

    protected void terminate0() {
    }
}