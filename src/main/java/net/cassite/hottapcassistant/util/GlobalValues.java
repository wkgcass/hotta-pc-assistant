package net.cassite.hottapcassistant.util;

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

public class GlobalValues {
    private GlobalValues() {
    }

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
            new SimpleAlert(Alert.AlertType.ERROR, I18n.get().swapConfigFailedMissingVersionInConfig()).showAndWait();
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
            new SimpleAlert(Alert.AlertType.ERROR, I18n.get().swapConfigFailedTargetFileIsNotDir()).showAndWait();
            return false;
        }

        var moveToDir = savedPath.get() + "_" + ass.version.name();
        var moveToDirFile = new File(moveToDir);
        if (moveToDirFile.exists()) {
            new SimpleAlert(Alert.AlertType.ERROR, I18n.get().swapConfigFailedPathToMoveToIsOccupied()).showAndWait();
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
                new SimpleAlert(Alert.AlertType.ERROR, I18n.get().swapConfigFailedCreatingSavedDirFailed()).showAndWait();
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
            new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().gamePathNotSet()).show();
            return false;
        }
        var f = new File(gamePath.get());
        if (!f.isDirectory()) {
            new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().gamePathIsNotDirectory()).show();
            return false;
        }
        return checkSavedPath();
    }

    public static boolean checkGlobalServerGamePath() {
        if (globalServerGamePath.get() == null) {
            new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().gamePathNotSet()).show();
            return false;
        }
        var f = new File(globalServerGamePath.get());
        if (!f.isDirectory()) {
            new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().gamePathIsNotDirectory()).show();
            return false;
        }
        return checkSavedPath();
    }

    private static boolean checkSavedPath() {
        if (savedPath.get() == null) {
            new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().savedPathNotSet()).show();
            return false;
        }
        var f = new File(savedPath.get());
        if (!f.isDirectory()) {
            new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().savedPathIsNotDirectory()).show();
            return false;
        }
        return true;
    }
}
