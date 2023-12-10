package net.cassite.hottapcassistant.tool;

import io.vproxy.base.util.LogType;
import io.vproxy.base.util.Logger;
import io.vproxy.vfx.control.dialog.VConfirmDialog;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.manager.image.ImageManager;
import io.vproxy.vfx.ui.alert.SimpleAlert;
import io.vproxy.vfx.ui.alert.StackTraceAlert;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.layout.HPadding;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.pane.FusionPane;
import io.vproxy.vfx.ui.scene.VScene;
import io.vproxy.vfx.ui.stage.VStage;
import io.vproxy.vfx.ui.table.RowInformer;
import io.vproxy.vfx.ui.table.RowInformerAware;
import io.vproxy.vfx.ui.table.VTableColumn;
import io.vproxy.vfx.ui.table.VTableView;
import io.vproxy.vfx.ui.wrapper.FusionW;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Consts;
import vjson.JSON;
import vjson.ex.JsonParseException;
import vjson.util.ObjectBuilder;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class PatchManager extends AbstractTool {
    @Override
    protected String buildName() {
        return I18n.get().toolName("patch-manager");
    }

    @Override
    protected Image buildIcon() {
        return ImageManager.get().load("/images/icon/patch-manager-icon.png");
    }

    @Override
    protected VScene buildScene() throws Exception {
        return new S();
    }

    private static class S extends ToolScene {
        private final File patchDir;
        private final VTableView<PatchInfo> table = new VTableView<>();

        private class PatchInfo implements RowInformerAware {
            boolean enabled;
            String name;
            boolean isCNCompatible;
            boolean isGlobalCompatible;
            String description;

            PatchInfo(String name, PatchInfoBuilder b) {
                this.enabled = b.enabled;
                this.name = name;
                this.isCNCompatible = b.isCNCompatible;
                this.isGlobalCompatible = b.isGlobalCompatible;
                this.description = b.description;
                if (this.description == null) {
                    this.description = "";
                }
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
                persist();
            }

            public void setCNCompatible(boolean CNCompatible) {
                isCNCompatible = CNCompatible;
                persist();
            }

            public void setGlobalCompatible(boolean globalCompatible) {
                isGlobalCompatible = globalCompatible;
                persist();
            }

            public void setDescription(String description) {
                this.description = description;
                persist();
                if (rowInformer != null) {
                    rowInformer.informRowUpdate();
                }
            }

            private void persist() {
                var json = new ObjectBuilder()
                    .put("enabled", enabled)
                    .put("isCNCompatible", isCNCompatible)
                    .put("isGlobalCompatible", isGlobalCompatible)
                    .put("description", description)
                    .build()
                    .pretty();
                var p = Path.of(patchDir.getAbsolutePath(), name + PatchInfoBuilder.CONFIG_SUFFIX);
                try {
                    Files.writeString(p, json);
                } catch (IOException e) {
                    SimpleAlert.show(
                        I18n.get().patchManagerAlertFailedToWriteConfigTitle(),
                        I18n.get().patchManagerAlertFailedToWriteConfigContent());
                    Logger.error(LogType.FILE_ERROR, "failed to write config file to " + p, e);
                }
            }

            private RowInformer rowInformer;

            @Override
            public void setRowInformer(RowInformer rowInformer) {
                this.rowInformer = rowInformer;
            }
        }

        S() throws Exception {
            enableAutoContentWidthHeight();

            patchDir = PatchInfoBuilder.getPatchManagerDir();

            initUI();
            load();
        }

        private void initUI() {
            var enabledCol = new VTableColumn<PatchInfo, PatchInfo>(I18n.get().patchManagerEnabledCol(), i -> i);
            var nameCol = new VTableColumn<PatchInfo, String>(I18n.get().patchManagerNameCol(), i -> i.name);
            var cnCol = new VTableColumn<PatchInfo, PatchInfo>(I18n.get().patchManagerCNCol(), i -> i);
            var globalCol = new VTableColumn<PatchInfo, PatchInfo>(I18n.get().patchManagerGlobalCol(), i -> i);
            var descCol = new VTableColumn<PatchInfo, String>(I18n.get().patchManagerDescCol(), i -> i.description);
            //noinspection unchecked
            table.getColumns().addAll(enabledCol, nameCol, cnCol, globalCol, descCol);
            enabledCol.setMaxWidth(80);
            nameCol.setMinWidth(100);
            nameCol.setComparator(String::compareTo);
            cnCol.setMaxWidth(80);
            globalCol.setMaxWidth(80);
            descCol.setMinWidth(400);

            enabledCol.setAlignment(Pos.CENTER);
            enabledCol.setNodeBuilder(i -> {
                var checkBox = new CheckBox();
                FXUtils.disableFocusColor(checkBox);
                checkBox.setSelected(i.enabled);
                checkBox.setOnAction(e -> i.setEnabled(checkBox.isSelected()));
                return checkBox;
            });
            nameCol.setAlignment(Pos.CENTER);
            cnCol.setAlignment(Pos.CENTER);
            cnCol.setNodeBuilder(i -> {
                var checkBox = new CheckBox();
                FXUtils.disableFocusColor(checkBox);
                checkBox.setSelected(i.isCNCompatible);
                checkBox.setOnAction(e -> i.setCNCompatible(checkBox.isSelected()));
                return checkBox;
            });
            globalCol.setAlignment(Pos.CENTER);
            globalCol.setNodeBuilder(i -> {
                var checkBox = new CheckBox();
                FXUtils.disableFocusColor(checkBox);
                checkBox.setSelected(i.isGlobalCompatible);
                checkBox.setOnAction(e -> i.setGlobalCompatible(checkBox.isSelected()));
                return checkBox;
            });

            var editBtn = new FusionButton(I18n.get().patchManagerEditBtn()) {{
                setPrefWidth(120);
                setPrefHeight(32);
            }};
            var removeBtn = new FusionButton(I18n.get().patchManagerRemoveBtn()) {{
                setPrefWidth(120);
                setPrefHeight(32);
            }};
            var reloadBtn = new FusionButton(I18n.get().patchManagerReloadBtn()) {{
                setPrefWidth(120);
                setPrefHeight(32);
            }};
            var openFolderBtn = new FusionButton(I18n.get().patchManagerOpenFolderBtn()) {{
                setPrefWidth(120);
                setPrefHeight(32);
            }};

            editBtn.setOnAction(e -> {
                var info = table.getSelectedItem();
                if (info == null) { // nothing selected
                    return;
                }
                new EditStage(info).show();
            });
            removeBtn.setOnAction(e -> {
                var info = table.getSelectedItem();
                if (info == null) { // nothing selected
                    return;
                }
                var confirm = new VConfirmDialog();
                confirm.setText(I18n.get().patchManagerConfirmRemove(info.name));
                var res = confirm.showAndWait();
                if (res.isPresent() && res.get() == VConfirmDialog.Result.NO) {
                    return;
                }
                var f = Path.of(patchDir.getAbsolutePath(), info.name + PatchInfoBuilder.CONFIG_SUFFIX).toFile();
                if (f.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    f.delete();
                }
                f = Path.of(patchDir.getAbsolutePath(), info.name + ".sig").toFile();
                if (f.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    f.delete();
                }
                f = Path.of(patchDir.getAbsolutePath(), info.name + ".pak").toFile();
                if (f.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    f.delete();
                }
                table.getItems().remove(info);
            });
            reloadBtn.setOnAction(e -> {
                try {
                    load();
                } catch (Exception ex) {
                    StackTraceAlert.show(ex);
                }
            });
            openFolderBtn.setOnAction(e -> {
                try {
                    Desktop.getDesktop().open(patchDir);
                } catch (IOException ignore) {
                }
            });

            var pane = getContentPane();
            Region tableRightNode;

            pane.getChildren().addAll(
                new VBox(
                    new VPadding(10),
                    new HBox(
                        new HPadding(10),
                        table.getNode(),
                        new HPadding(10),
                        new FusionPane(false, (tableRightNode = new VBox(
                            editBtn,
                            new VPadding(5),
                            removeBtn,
                            new VPadding(5),
                            reloadBtn,
                            new VPadding(5),
                            openFolderBtn
                        ))).getNode()
                    )
                )
            );

            pane.widthProperty().addListener((ob, old, now) -> {
                if (now == null) return;
                table.getNode().setPrefWidth(now.doubleValue() - 10 - 120 - 10 - 10 - FusionPane.PADDING_V * 2);
            });
            pane.heightProperty().addListener((ob, old, now) -> {
                if (now == null) return;
                var tableMinHeight = tableRightNode.getHeight() + FusionPane.PADDING_V * 2;
                var pref = now.doubleValue() - 10 - 10;
                if (pref < tableMinHeight) {
                    pref = tableMinHeight;
                }
                table.getNode().setPrefHeight(pref);
            });
        }

        private void load() throws Exception {
            var old = new ArrayList<>(table.getItems());
            table.getItems().clear();

            var files = patchDir.listFiles();
            if (files == null) {
                return; // empty
            }
            try {
                for (var f : files) {
                    if (!f.isFile()) {
                        continue;
                    }
                    if (!f.getName().endsWith(".pak")) {
                        continue;
                    }
                    var name = f.getName();
                    name = name.substring(0, name.length() - ".pak".length());
                    load(name);
                }
            } catch (Exception e) {
                table.getItems().clear();
                table.getItems().addAll(old);
                throw e;
            }
        }

        private void load(String name) throws Exception {
            var p = Path.of(patchDir.getAbsolutePath(), name + PatchInfoBuilder.CONFIG_SUFFIX);
            var f = p.toFile();
            if (!f.exists()) {
                load(name, new PatchInfoBuilder());
                return;
            }
            if (!f.isFile()) {
                Logger.warn(LogType.INVALID_EXTERNAL_DATA, f + " is not a valid file, config creation might fail!");
                load(name, new PatchInfoBuilder());
                return;
            }
            String content;
            try {
                content = Files.readString(p);
            } catch (IOException e) {
                Logger.error(LogType.FILE_ERROR, "failed reading config file: " + p, e);
                throw e;
            }
            PatchInfoBuilder b;
            try {
                b = JSON.deserialize(content, PatchInfoBuilder.rule);
            } catch (JsonParseException e) {
                SimpleAlert.show(I18n.get().patchManagerAlertInvalidConfigTitle(), I18n.get().patchManagerAlertInvalidConfigContent(name));
                Logger.error(LogType.INVALID_EXTERNAL_DATA, "failed reading data from " + p + ", using empty data instead", e);
                b = null;
            }
            load(name, b);
        }

        private void load(String name, PatchInfoBuilder patchInfoBuilder) {
            var i = new PatchInfo(name, patchInfoBuilder);
            table.getItems().add(i);
        }

        private static class EditStage extends VStage {
            EditStage(PatchInfo info) {
                var pane = getInitialScene().getContentPane();

                var nameLabel = new ThemeLabel(I18n.get().patchManagerNameCol()) {{
                    FontManager.get().setFont(Consts.NotoFont, this);
                    setPrefWidth(100);
                    setAlignment(Pos.CENTER_RIGHT);
                }};
                var descLabel = new ThemeLabel(I18n.get().worldBossTimerCommentCol()) {{
                    FontManager.get().setFont(Consts.NotoFont, this);
                    setPrefWidth(100);
                    setAlignment(Pos.CENTER_RIGHT);
                    setPadding(new Insets(5, 0, 0, 0));
                }};
                var nameValue = new ThemeLabel(info.name) {{
                    FontManager.get().setFont(Consts.NotoFont, this);
                    setAlignment(Pos.CENTER_LEFT);
                }};
                var descInput = new TextField() {{
                    FontManager.get().setFont(Consts.NotoFont, this);
                    setPrefWidth(300);
                    setText(info.description);
                }};

                var okBtn = new FusionButton(I18n.get().worldBossTimerOkBtn()) {{
                    FontManager.get().setFont(Consts.NotoFont, getTextNode());
                }};
                okBtn.setPrefWidth(120);
                okBtn.setPrefHeight(32);
                okBtn.setOnAction(e -> {
                    info.setDescription(descInput.getText().trim());
                    close();
                });

                pane.getChildren().add(new VBox(
                    new VPadding(10),
                    new HBox(
                        new HPadding(10),
                        new VBox(
                            new HBox(nameLabel, new HPadding(10), nameValue),
                            new VPadding(5),
                            new HBox(descLabel, new HPadding(10), new FusionW(descInput))
                        )
                    ),
                    new VPadding(30),
                    new HBox(new HPadding(165), okBtn)
                ));

                getStage().setWidth(450);
                getStage().setHeight(200);
                getInitialScene().enableAutoContentWidthHeight();
            }
        }
    }
}
