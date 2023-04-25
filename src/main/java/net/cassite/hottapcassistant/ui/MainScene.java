package net.cassite.hottapcassistant.ui;

import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.scene.VScene;

public interface MainScene {
    String title();

    void setVisible(boolean visible, VScene current);

    VScene getScene();

    FusionButton getMenuButton();

    default OverrideHelper getOverrideHelper() {
        throw new UnsupportedOperationException("require override");
    }

    class OverrideHelper {
        protected final MainScene scene;

        public OverrideHelper(MainScene scene) {
            this.scene = scene;
        }

        @SuppressWarnings("RedundantThrows")
        public boolean checkBeforeShowing() throws Exception {
            return true;
        }

        public void beforeShowing() {
        }

        public void onShown() {
        }

        @SuppressWarnings("RedundantThrows")
        public boolean checkBeforeHiding() throws Exception {
            return true;
        }

        public void beforeHiding() {
        }

        public void onHidden() {
        }
    }
}
