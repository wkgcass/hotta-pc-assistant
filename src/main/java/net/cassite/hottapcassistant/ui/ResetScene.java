package net.cassite.hottapcassistant.ui;

import io.vproxy.base.util.LogType;
import io.vproxy.base.util.Logger;
import io.vproxy.commons.util.IOUtils;
import io.vproxy.vfx.ui.alert.SimpleAlert;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import net.cassite.hottapcassistant.Main;
import net.cassite.hottapcassistant.config.AssistantConfig;
import net.cassite.hottapcassistant.entity.Assistant;
import net.cassite.hottapcassistant.i18n.I18n;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResetScene extends MainScene {
    public ResetScene() {
        enableAutoContentWidthHeight();
        var vbox = new VBox();
        FXUtils.observeWidthHeightCenter(getContentPane(), vbox);

        vbox.setSpacing(40);
        vbox.setAlignment(Pos.CENTER);
        var label = new ThemeLabel(I18n.get().resetSceneDesc());
        var resetConfigBtn = new FusionButton(I18n.get().resetSceneResetConfigButton()) {{
            setMaxWidth(120);
            setPrefHeight(60);
        }};
        resetConfigBtn.setOnAction(e -> resetConfig());
        vbox.getChildren().addAll(
            label,
            resetConfigBtn
        );

        getContentPane().getChildren().add(vbox);
    }

    private void resetConfig() {
        Assistant ass = null;
        try {
            ass = AssistantConfig.readAssistant(false);
        } catch (Exception ignore) {
        }
        try {
            IOUtils.deleteDirectory(AssistantConfig.assistantDirPath.toFile());
        } catch (Exception ignore) {
        }

        String savedPath = null;
        if (ass == null) {
            var home = System.getProperty("user.home");
            var res = Path.of(home, "AppData", "Local", "Hotta", "Saved").toString();
            if (new File(res).isDirectory()) {
                savedPath = res;
            }
        } else {
            savedPath = ass.lastValues.savedPath;
        }

        deleteConfigFromSavedPath(savedPath);

        Main.cleanupLastRun();
        SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().resetSceneResetConfigSucceeded());
        System.exit(0); // do not give the program any chance to save config
    }

    private void deleteConfigFromSavedPath(String savedPath) {
        if (savedPath == null) {
            return;
        }
        var savedFile = new File(savedPath);
        var dir = savedFile.getParentFile();
        if (dir == null) {
            return;
        }
        var files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (var f : files) {
            if (!f.isDirectory()) {
                continue;
            }
            var path = Path.of(f.getAbsolutePath(), "Assistant.vjson.txt");
            var ff = path.toFile();
            if (ff.isFile()) {
                try {
                    Files.delete(path);
                    Logger.warn(LogType.ALERT, "assistant config in Saved dir deleted: " + path);
                } catch (IOException e) {
                    Logger.error(LogType.FILE_ERROR, "failed to delete assistant config in Saved dir: " + path);
                }
            }
        }
    }

    @Override
    public String title() {
        return I18n.get().toolNameReset();
    }
}
