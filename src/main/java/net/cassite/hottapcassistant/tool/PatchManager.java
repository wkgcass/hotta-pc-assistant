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
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
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
import java.util.List;
import java.util.*;
import java.util.function.Function;

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
            List<String> loadAfter;
            List<String> dependsOn;

            PatchInfo(String name, PatchInfoBuilder b) {
                this.enabled = b.enabled;
                this.name = name;
                this.isCNCompatible = b.isCNCompatible;
                this.isGlobalCompatible = b.isGlobalCompatible;
                this.description = b.description;
                this.loadAfter = b.loadAfter;
                this.dependsOn = b.dependsOn;

                if (this.description == null) {
                    this.description = "";
                }
                if (this.loadAfter == null) {
                    this.loadAfter = new ArrayList<>();
                }
                if (this.dependsOn == null) {
                    this.dependsOn = new ArrayList<>();
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

            public void persist() {
                var json = new ObjectBuilder()
                    .put("enabled", enabled)
                    .put("isCNCompatible", isCNCompatible)
                    .put("isGlobalCompatible", isGlobalCompatible)
                    .put("description", description)
                    .putArray("loadAfter", a -> loadAfter.forEach(a::add))
                    .putArray("dependsOn", a -> dependsOn.forEach(a::add))
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

                if (rowInformer != null) {
                    rowInformer.informRowUpdate();
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
            var loadAfterCol = new VTableColumn<PatchInfo, Integer>("L", i -> i.loadAfter.size());
            var dependsOnCol = new VTableColumn<PatchInfo, Integer>("D", i -> i.dependsOn.size());
            //noinspection unchecked
            table.getColumns().addAll(enabledCol, nameCol, cnCol, globalCol, descCol, loadAfterCol, dependsOnCol);
            enabledCol.setMaxWidth(80);
            nameCol.setMinWidth(100);
            nameCol.setComparator(String::compareTo);
            cnCol.setMaxWidth(80);
            globalCol.setMaxWidth(80);
            descCol.setMinWidth(400);
            loadAfterCol.setMaxWidth(40);
            dependsOnCol.setMaxWidth(40);

            loadAfterCol.setAlignment(Pos.CENTER);
            dependsOnCol.setAlignment(Pos.CENTER);

            enabledCol.setAlignment(Pos.CENTER);
            enabledCol.setNodeBuilder(i -> {
                var checkBox = new CheckBox();
                FXUtils.disableFocusColor(checkBox);
                checkBox.setSelected(i.enabled);
                checkBox.setOnAction(e -> i.setEnabled(checkBox.isSelected()));
                if (checkBox.isSelected()) {
                    // enable dependencies
                    for (var d : i.dependsOn)
                        for (var ii : table.getItems())
                            if (d.equals(ii.name))
                                ii.setEnabled(true);
                } else {
                    // disable depended
                    for (var ii : table.getItems())
                        for (var d : ii.dependsOn)
                            if (d.equals(i.name))
                                ii.setEnabled(false);
                }
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
                var depended = new HashSet<String>();
                for (var ii : table.getItems())
                    for (var d : ii.dependsOn)
                        if (d.equals(info.name))
                            depended.add(ii.name);
                if (!depended.isEmpty()) {
                    SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().patchManagerAlertHasDependedCannotDelete(info.name, depended));
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

        private class EditStage extends VStage {
            EditStage(PatchInfo info) {
                var pane = getInitialScene().getContentPane();

                var nameLabel = new ThemeLabel(I18n.get().patchManagerNameCol()) {{
                    FontManager.get().setFont(Consts.NotoFont, this);
                    setPrefWidth(100);
                    setAlignment(Pos.CENTER_RIGHT);
                }};
                var descLabel = new ThemeLabel(I18n.get().patchManagerDescCol()) {{
                    FontManager.get().setFont(Consts.NotoFont, this);
                    setPrefWidth(100);
                    setAlignment(Pos.CENTER_RIGHT);
                    setPadding(new Insets(5, 0, 0, 0));
                }};
                var loadAfterLabel = new ThemeLabel(I18n.get().patchManagerLoadAfterCol()) {{
                    FontManager.get().setFont(Consts.NotoFont, this);
                    setPrefWidth(100);
                    setAlignment(Pos.CENTER_RIGHT);
                }};
                var dependsOnLabel = new ThemeLabel(I18n.get().patchManagerDependsOnCol()) {{
                    FontManager.get().setFont(Consts.NotoFont, this);
                    setPrefWidth(100);
                    setAlignment(Pos.CENTER_RIGHT);
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
                var loadAfterInput = new TextArea() {{
                    FontManager.get().setFont(Consts.JetbrainsMonoFont, this);
                    setPrefWidth(300);
                    setPrefHeight(60);
                    setText(String.join("\n", info.loadAfter));
                }};
                var dependsOnInput = new TextArea() {{
                    FontManager.get().setFont(Consts.JetbrainsMonoFont, this);
                    setPrefWidth(300);
                    setPrefHeight(60);
                    setText(String.join("\n", info.dependsOn));
                }};

                var okBtn = new FusionButton(I18n.get().patchManagerOkBtn()) {{
                    FontManager.get().setFont(Consts.NotoFont, getTextNode());
                }};
                okBtn.setPrefWidth(120);
                okBtn.setPrefHeight(32);
                okBtn.setOnAction(e -> {
                    var loadAfter = textListValue(loadAfterInput);
                    var dependsOn = textListValue(dependsOnInput);

                    if (checkExistence(I18n.get().patchManagerLoadAfterCol(), loadAfter)) {
                        return;
                    }
                    if (checkExistence(I18n.get().patchManagerDependsOnCol(), dependsOn)) {
                        return;
                    }
                    if (checkCircularDep(I18n.get().patchManagerLoadAfterCol(), info, loadAfter, i -> i.loadAfter)) {
                        return;
                    }
                    if (checkCircularDep(I18n.get().patchManagerDependsOnCol(), info, dependsOn, i -> i.dependsOn)) {
                        return;
                    }

                    info.description = descInput.getText().trim();
                    info.loadAfter = loadAfter;
                    info.dependsOn = dependsOn;
                    info.persist();

                    if (info.enabled)
                        for (var d : dependsOn)
                            for (var item : table.getItems())
                                if (item.name.equals(d))
                                    item.setEnabled(true);

                    close();
                });

                pane.getChildren().add(new VBox(
                    new VPadding(10),
                    new HBox(
                        new HPadding(10),
                        new VBox(
                            new HBox(nameLabel, new HPadding(10), nameValue),
                            new HBox(descLabel, new HPadding(10), new FusionW(descInput)),
                            new HBox(loadAfterLabel, new HPadding(10), new FusionW(loadAfterInput)),
                            new HBox(dependsOnLabel, new HPadding(10), new FusionW(dependsOnInput))
                        ) {{
                            setSpacing(5);
                        }}
                    ),
                    new VPadding(30),
                    new HBox(new HPadding(165), okBtn)
                ));

                getStage().setWidth(450);
                getStage().setHeight(330);
                getInitialScene().enableAutoContentWidthHeight();
            }

            private boolean checkExistence(String colName, List<String> parentNames) {
                var current = table.getItems();
                if (current == null) {
                    current = Collections.emptyList();
                }
                for (var parent : parentNames) {
                    if (current.stream().noneMatch(i -> i.name.equals(parent))) {
                        SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().patchManagerDepNotExist(colName, parent));
                        return true;
                    }
                }
                return false;
            }

            private boolean checkCircularDep(String colName, PatchInfo info, List<String> parentNames, Function<PatchInfo, List<String>> getPatches) {
                var ls = new ArrayList<String>();
                ls.add(info.name);
                return checkCircularDep0(colName, info, parentNames, ls, getPatches);
            }

            private boolean checkCircularDep0(String colName, PatchInfo checkItem,
                                              List<String> parentNames,
                                              List<String> currentPath,
                                              Function<PatchInfo, List<String>> getParents) {
                var current = table.getItems();
                if (current == null) {
                    current = Collections.emptyList();
                }
                final var fcurrent = current;

                var parents = parentNames.stream()
                    .map(s ->
                        fcurrent.stream().filter(i -> i.name.equals(s)).findAny().orElse(null)
                    )
                    .filter(Objects::nonNull)
                    .toList();

                for (var parent : parents) {
                    var newPath = new ArrayList<>(currentPath);
                    newPath.add(parent.name);
                    if (parent.name.equals(checkItem.name)) {
                        SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().patchManagerDepCircular(colName, newPath));
                        return true;
                    }
                    var res = checkCircularDep0(colName, checkItem, getParents.apply(parent), newPath, getParents);
                    if (res) {
                        return true;
                    }
                }
                return false;
            }

            private static List<String> textListValue(TextArea dependsOnInput) {
                var ret = new ArrayList<String>();
                var split = dependsOnInput.getText().split("\n");
                for (var s : split) {
                    if (s.isBlank())
                        continue;
                    ret.add(s.trim());
                }
                return ret;
            }
        }
    }
}
