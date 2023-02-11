package net.cassite.hottapcassistant.tool;

import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.manager.image.ImageManager;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.layout.HPadding;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.pane.FusionPane;
import io.vproxy.vfx.ui.scene.VScene;
import io.vproxy.vfx.ui.stage.VStage;
import io.vproxy.vfx.ui.stage.VStageInitParams;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.status.Status;
import net.cassite.hottapcassistant.status.StatusEnum;
import net.cassite.hottapcassistant.status.StatusManager;
import net.cassite.hottapcassistant.util.Consts;

public class StatusIndicator extends AbstractTool implements Tool {
    private VStage stage;

    @Override
    protected String buildName() {
        return I18n.get().toolName("status-indicator");
    }

    @Override
    protected Image buildIcon() {
        return ImageManager.get().load("/images/icon/status-indicator.png");
    }

    @Override
    protected VScene buildScene() {
        stage = new S();
        stage.setTitle(I18n.get().statusIndicatorTitle());
        stage.show();

        return new ToolScene() {{
            enableAutoContentWidthHeight();

            var descLabel = new ThemeLabel(I18n.get().statusIndicatorDesc());
            FXUtils.observeWidthHeightCenter(getContentPane(), descLabel);
            getContentPane().getChildren().add(descLabel);
        }};
    }

    @Override
    protected void terminate0() {
        var stage = this.stage;
        this.stage = null;
        if (stage != null) {
            stage.close();
        }
    }

    private static class S extends VStage {
        private final VBox vbox;

        public S() {
            super(new VStageInitParams()
                .setResizable(false)
                .setMaximizeAndResetButton(false)
                .setCloseButton(false));

            final int width = 250;
            final int height = 300;
            getStage().setWidth(width);
            getStage().setHeight(height);
            getStage().setAlwaysOnTop(true);
            getStage().showingProperty().addListener((ob, old, now) -> {
                if (now == null) return;
                if (!now) return;
                var screen = FXUtils.getScreenOf(getStage());
                if (screen == null) return;
                var bounds = screen.getBounds();
                getStage().setX(bounds.getMinX() + bounds.getWidth() - width);
                getStage().setY(bounds.getMinY() + bounds.getHeight() - height);
            });

            getInitialScene().enableAutoContentWidth();
            vbox = new VBox();
            vbox.setPadding(new Insets(10));
            var root = getInitialScene().getContentPane();
            FXUtils.observeWidth(root, vbox);
            root.getChildren().add(vbox);
            watch();
        }

        @SuppressWarnings("FieldCanBeLocal") private Runnable cb;

        private void watch() {
            this.cb = this::update;
            StatusManager.get().registerCallback(cb);
            update();
        }

        private void update() {
            vbox.getChildren().clear();
            var ls = StatusManager.get().getAllStatus();
            for (var s : ls) {
                var pane = makeStatusPane(s);
                vbox.getChildren().add(pane.getNode());
            }
        }

        private FusionPane makeStatusPane(Status status) {
            var label = new ThemeLabel(status.component.text + ": " + status.componentName) {{
                FontManager.get().setFont(Consts.NotoFont, this);
            }};
            var statusDot = new Circle(6);
            statusDot.setStrokeWidth(1);
            statusDot.setStroke(Color.WHITE);
            if (status.status == StatusEnum.RUNNING) {
                statusDot.setFill(Theme.current().progressBarProgressColor());
            } else if (status.status == StatusEnum.STOPPED) {
                statusDot.setFill(new Color(0xff / 255d, 0x5f / 255d, 0x5f / 255d, 1));
            } else {
                statusDot.setFill(Color.ORANGE);
            }

            return new FusionPane(false, new HBox(new HPadding(10), new VBox(new VPadding(6), statusDot), new HPadding(10), label));
        }
    }
}
