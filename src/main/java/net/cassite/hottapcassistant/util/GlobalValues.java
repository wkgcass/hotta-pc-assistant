package net.cassite.hottapcassistant.util;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.dns.AddressResolverOptions;
import io.vproxy.vfx.ui.alert.SimpleAlert;
import io.vproxy.vfx.ui.button.FusionImageButton;
import io.vproxy.vfx.util.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import net.cassite.hottapcassistant.config.AssistantConfig;
import net.cassite.hottapcassistant.entity.GameAssistant;
import net.cassite.hottapcassistant.entity.GameVersion;
import net.cassite.hottapcassistant.i18n.I18n;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class GlobalValues {
    private GlobalValues() {
    }

    public static final Vertx vertx = Vertx.vertx(new VertxOptions()
        .setAddressResolverOptions(new AddressResolverOptions()
            .setHostsValue(Buffer.buffer(""))
            .setServers(List.of("114.114.114.114"))));

    public static final SimpleStringProperty savedPath = new SimpleStringProperty(null) {
        @Override
        public void set(String newValue) {
            if (newValue != null) {
                if (newValue.endsWith("/") || newValue.endsWith("\\")) {
                    newValue = newValue.substring(0, newValue.length() - 1);
                }
            }
            super.set(newValue);
        }
    };
    public static final SimpleStringProperty gamePath = new SimpleStringProperty(null);
    public static final SimpleStringProperty globalServerGamePath = new SimpleStringProperty(null);
    public static final SimpleObjectProperty<GameVersion> useVersion = new SimpleObjectProperty<>(GameVersion.CN);

    public static FusionImageButton backButton;
    public static Runnable backFunction;
    public static FusionImageButton closeButton;
    public static Runnable closeFunction;

    public static AssistantConfig getGameAssistantConfig() {
        if (savedPath.get() == null) throw new IllegalStateException();
        return AssistantConfig.ofSaved(Path.of(savedPath.get(), "Assistant.vjson.txt").toAbsolutePath().toString());
    }

    public static boolean swapConfig() throws Exception {
        if (savedPath.get() == null) {
            throw new IllegalStateException();
        }
        GameAssistant ass;
        try {
            ass = getGameAssistantConfig().readGameAssistant();
        } catch (IOException e) {
            Logger.error("failed reading assistant config", e);
            throw e;
        }
        if (ass.version == null) {
            SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().swapConfigFailedMissingVersionInConfig());
            return false;
        }
        if (useVersion.get() == GameVersion.CN) {
            if (ass.version.equals(GameVersion.CN)) return true;
        } else if (useVersion.get() == GameVersion.Global) {
            if (ass.version.equals(GameVersion.Global)) return true;
        } else {
            Logger.error("unknown version " + useVersion.get());
            throw new Exception("unknown version " + useVersion.get());
        }

        var name = useVersion.get().name();
        var targetDir = savedPath.get() + "_" + name;
        var targetDirFile = new File(targetDir);
        if (targetDirFile.exists() && !targetDirFile.isDirectory()) {
            SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().swapConfigFailedTargetFileIsNotDir());
            return false;
        }

        var moveToDir = savedPath.get() + "_" + ass.version.name();
        var moveToDirFile = new File(moveToDir);
        if (moveToDirFile.exists()) {
            SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().swapConfigFailedPathToMoveToIsOccupied());
            return false;
        }

        var savedFile = new File(savedPath.get());
        if (!savedFile.renameTo(moveToDirFile)) {
            var err = "failed moving " + savedPath.get() + " to " + moveToDir;
            Logger.error(err);
            throw new IOException(err);
        }
        if (targetDirFile.exists()) {
            if (!targetDirFile.renameTo(savedFile)) {
                var err = "failed moving " + targetDir + " to " + savedPath.get();
                Logger.error(err);
                throw new IOException(err);
            }
        } else {
            if (!savedFile.mkdirs()) {
                SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().swapConfigFailedCreatingSavedDirFailed());
                return false;
            }
        }
        return true;
    }

    public static boolean checkGamePath() {
        if (useVersion.get() == GameVersion.CN) {
            return checkCNGamePath();
        } else if (useVersion.get() == GameVersion.Global) {
            return checkGlobalServerGamePath();
        } else {
            Logger.warn("unknown game version: " + useVersion.get() + ", check with CN version rules");
            return checkCNGamePath();
        }
    }

    public static boolean checkCNGamePath() {
        if (gamePath.get() == null) {
            SimpleAlert.show(Alert.AlertType.INFORMATION, I18n.get().gamePathNotSet());
            return false;
        }
        var f = new File(gamePath.get());
        if (!f.isDirectory()) {
            SimpleAlert.show(Alert.AlertType.INFORMATION, I18n.get().gamePathIsNotDirectory());
            return false;
        }
        return checkSavedPath();
    }

    public static boolean checkGlobalServerGamePath() {
        if (globalServerGamePath.get() == null) {
            SimpleAlert.show(Alert.AlertType.INFORMATION, I18n.get().gamePathNotSet());
            return false;
        }
        var f = new File(globalServerGamePath.get());
        if (!f.isDirectory()) {
            SimpleAlert.show(Alert.AlertType.INFORMATION, I18n.get().gamePathIsNotDirectory());
            return false;
        }
        return checkSavedPath();
    }

    private static boolean checkSavedPath() {
        if (savedPath.get() == null) {
            SimpleAlert.show(Alert.AlertType.INFORMATION, I18n.get().savedPathNotSet());
            return false;
        }
        var f = new File(savedPath.get());
        if (!f.isDirectory()) {
            SimpleAlert.show(Alert.AlertType.INFORMATION, I18n.get().savedPathIsNotDirectory());
            return false;
        }
        return true;
    }

    public static void setBackFunction(Runnable f) {
        if (backFunction != null) {
            throw new IllegalStateException();
        }
        backButton.setVisible(true);
        backFunction = f;
    }

    public static void setCloseFunction(Runnable f) {
        if (closeFunction != null) {
            throw new IllegalStateException();
        }
        closeButton.setVisible(true);
        closeFunction = f;
    }
}
