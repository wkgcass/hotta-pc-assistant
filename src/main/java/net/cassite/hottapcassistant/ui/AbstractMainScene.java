package net.cassite.hottapcassistant.ui;

import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.scene.VScene;
import io.vproxy.vfx.ui.scene.VSceneRole;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;

public abstract class AbstractMainScene extends VScene implements MainScene {
    protected final FusionButton menuButton = new FusionButton();

    public AbstractMainScene() {
        super(VSceneRole.MAIN);
        getNode().setBackground(new Background(new BackgroundFill(
            Theme.current().sceneBackgroundColor(),
            CornerRadii.EMPTY, Insets.EMPTY
        )));
    }

    public abstract String title();

    protected boolean hideMenuButton() {
        return false;
    }

    public void setVisible(boolean visible, VScene current) {
        if (this == current || visible || !hideMenuButton()) {
            menuButton.setManaged(true);
            menuButton.setVisible(true);
        } else {
            menuButton.setManaged(false);
            menuButton.setVisible(false);
        }
    }

    @Override
    public VScene getScene() {
        return this;
    }

    @Override
    public FusionButton getMenuButton() {
        return menuButton;
    }

    @Override
    protected boolean checkBeforeShowing() throws Exception {
        return getOverrideHelper().checkBeforeShowing();
    }

    @Override
    protected void beforeShowing() {
        getOverrideHelper().beforeShowing();
    }

    @Override
    protected void onShown() {
        getOverrideHelper().onShown();
    }

    @Override
    protected boolean checkBeforeHiding() throws Exception {
        return getOverrideHelper().checkBeforeHiding();
    }

    @Override
    protected void beforeHiding() {
        getOverrideHelper().beforeHiding();
    }

    @Override
    protected void onHidden() {
        getOverrideHelper().onHidden();
    }
}
