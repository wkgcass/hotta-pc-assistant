package net.cassite.hottapcassistant.ui;

import io.vertx.core.Vertx;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.scene.VScene;
import io.vproxy.vfx.ui.scene.VSceneGroup;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.status.Status;
import net.cassite.hottapcassistant.status.StatusComponent;
import net.cassite.hottapcassistant.status.StatusEnum;
import net.cassite.hottapcassistant.status.StatusManager;
import net.cassite.hottapcassistant.util.GlobalValues;
import net.cassite.xboxrelay.ui.ConfigureScene;

public class XBoxScene implements MainScene, Terminate {
    private final FusionButton menuButton = new FusionButton();
    private final Vertx vertx;
    private final ConfigureScene scene;

    public XBoxScene(VSceneGroup sceneGroup) {
        vertx = Vertx.vertx();
        scene = new ConfigureScene(vertx, () -> sceneGroup) {
            {
                getNode().setBackground(new Background(new BackgroundFill(
                    Theme.current().sceneBackgroundColor(),
                    CornerRadii.EMPTY, Insets.EMPTY
                )));
            }

            @Override
            protected boolean checkBeforeShowing() throws Exception {
                boolean ret = super.checkBeforeShowing();
                if (!ret) {
                    return false;
                }
                return getOverrideHelper().checkBeforeShowing();
            }

            @Override
            protected void beforeShowing() {
                super.beforeShowing();
                getOverrideHelper().beforeShowing();
            }

            @Override
            protected void onShown() {
                super.onShown();
                getOverrideHelper().onShown();
                GlobalValues.backFunction = scene::hideConfigTableScene;
            }

            @Override
            protected boolean checkBeforeHiding() throws Exception {
                boolean ret = super.checkBeforeHiding();
                if (!ret) {
                    return false;
                }
                return getOverrideHelper().checkBeforeHiding();
            }

            @Override
            protected void beforeHiding() {
                super.beforeHiding();
                getOverrideHelper().beforeHiding();
                GlobalValues.backFunction = null;
            }

            @Override
            protected void onHidden() {
                super.onHidden();
                getOverrideHelper().onHidden();
            }
        };
        scene.setOnStartEventHandler(() ->
            StatusManager.get().updateStatus(new Status(I18n.get().toolNameXBox(), StatusComponent.MODULE, StatusEnum.RUNNING)));
        scene.setOnStopEventHandler(() ->
            StatusManager.get().removeStatus(new Status(I18n.get().toolNameXBox(), StatusComponent.MODULE, StatusEnum.STOPPED)));
    }

    @Override
    public String title() {
        return I18n.get().toolNameXBox();
    }

    @Override
    public void setVisible(boolean visible, VScene current) {
        // always show
        menuButton.setManaged(true);
        menuButton.setVisible(true);
    }

    @Override
    public VScene getScene() {
        return scene;
    }

    @Override
    public FusionButton getMenuButton() {
        return menuButton;
    }

    @Override
    public void terminate() {
        scene.stop();
        vertx.close();
    }
}
