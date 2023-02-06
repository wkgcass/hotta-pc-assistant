package net.cassite.hottapcassistant.tool;

import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.manager.image.ImageManager;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.alert.SimpleAlert;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.layout.HPadding;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.pane.FusionPane;
import io.vproxy.vfx.ui.scene.VScene;
import io.vproxy.vfx.ui.stage.VStage;
import io.vproxy.vfx.ui.table.VTableColumn;
import io.vproxy.vfx.ui.table.VTableView;
import io.vproxy.vfx.ui.wrapper.FusionW;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import io.vproxy.vfx.util.IOUtils;
import io.vproxy.vfx.util.Logger;
import io.vproxy.vfx.util.MiscUtils;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import net.cassite.hottapcassistant.config.AssistantConfig;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Consts;
import vjson.JSON;
import vjson.deserializer.rule.*;
import vjson.ex.ParserException;
import vjson.pl.InterpreterBuilder;
import vjson.util.Manager;
import vjson.util.ObjectBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WorldBossTimer extends AbstractTool implements Tool {
    @Override
    protected String buildName() {
        return I18n.get().toolName("world-boss-timer");
    }

    @Override
    protected Image buildIcon() {
        return ImageManager.get().load("/images/icon/world-boss-timer-icon.png");
    }

    @Override
    protected VScene buildScene() throws Exception {
        return new S();
    }

    @Override
    protected void terminate0() {
        var stage = (S) this.scene;
        if (stage != null) {
            stage.etaTimer.stop();
        }
    }

    private static final DateTimeFormatter formatter = MiscUtils.YYYYMMddHHiissDateTimeFormatter;

    private static class S extends ToolScene {
        static final Path recordFilePath = Path.of(AssistantConfig.assistantDirPath.toString(), "WorldBossTimer.vjson.txt");
        static final File recordFile = recordFilePath.toFile();

        private final VTableView<BossInfo> table = new VTableView<>();
        private final VTableView<AccountInfo> accounts = new VTableView<>();
        private final AnimationTimer etaTimer;

        private final CheckBox includeBossTimerCheckBox;
        private final CheckBox includeAccountTimerCheckBox;
        private final CheckBox includeMsgTemplateCheckBox;
        private final CheckBox mergeImportCheckBox;

        private final TextArea nextBossInfoTemplate;

        S() throws Exception {
            enableAutoContentWidthHeight();

            initTableStructure();
            initAccountsStructure();
            etaTimer = new AnimationTimer() {
                private long last = 0;

                @Override
                public void handle(long now) {
                    if (last == 0) {
                        last = now;
                        return;
                    }
                    if (now < last) {
                        last = now;
                        return;
                    }
                    long delta = (now - last) / 1_000_000;
                    if (delta < 100) {
                        return;
                    }
                    last = now;
                    long current = System.currentTimeMillis();
                    for (var i : table.getItems()) {
                        i.timerLabel.setMillis(i.spawnMinutes * 60_000L - (current - i.lastKnownKillTs));
                    }
                    for (var i : accounts.getItems()) {
                        i.timerLabel.setMillis(i.switchLineCDMinutes * 60_000L - (current - i.lastSwitchLineTs));
                    }
                }
            };
            etaTimer.start();

            var addBtn = new FusionButton(I18n.get().worldBossTimerAddBtn()) {{
                setPrefWidth(120);
                setPrefHeight(32);
            }};
            var editBtn = new FusionButton(I18n.get().worldBossTimerEditBtn()) {{
                setPrefWidth(120);
                setPrefHeight(32);
            }};
            var spawnBtn = new FusionButton(I18n.get().worldBossTimerSpawnBtn()) {{
                setPrefWidth(120);
                setPrefHeight(32);
                getTextNode().setTextFill(new Color(0x8d / 255d, 0xbd / 255d, 0x74 / 255d, 1));
            }};
            var delBtn = new FusionButton(I18n.get().worldBossTimerDelBtn()) {{
                setPrefWidth(120);
                setPrefHeight(32);
            }};
            var clearBtn = new FusionButton(I18n.get().worldBossTimerClearBtn()) {{
                setPrefWidth(120);
                setPrefHeight(32);
            }};
            var copyNextBossInfoBtn = new FusionButton(I18n.get().worldBossTimerCopyBossInfoBtn()) {{
                setPrefWidth(120);
                setPrefHeight(60);
            }};
            nextBossInfoTemplate = new TextArea(I18n.get().worldBossTimerNextBossInfoDefaultTemplate()) {{
                FontManager.get().setFont(Consts.NotoFont, this);
                setPrefWidth(120);
                setPrefHeight(100);
                setWrapText(false);
            }};
            var exportBtn = new FusionButton(I18n.get().worldBossTimerExportBtn()) {{
                setPrefWidth(120);
                setPrefHeight(32);
            }};
            var importBtn = new FusionButton(I18n.get().worldBossTimerImportBtn()) {{
                setPrefWidth(120);
                setPrefHeight(32);
            }};
            includeBossTimerCheckBox = new CheckBox(I18n.get().worldBossTimerIncludeBossTimerCheckBox()) {{
                FontManager.get().setFont(this, settings -> settings.setSize(14));
                setTextFill(Theme.current().normalTextColor());
                FXUtils.disableFocusColor(this);
                setSelected(true);
            }};
            includeAccountTimerCheckBox = new CheckBox(I18n.get().worldBossTimerIncludeAccountTimerCheckBox()) {{
                FontManager.get().setFont(this, settings -> settings.setSize(14));
                setTextFill(Theme.current().normalTextColor());
                FXUtils.disableFocusColor(this);
            }};
            includeMsgTemplateCheckBox = new CheckBox(I18n.get().worldBossTimerIncludeMsgTemplateCheckBox()) {{
                FontManager.get().setFont(this, settings -> settings.setSize(14));
                setTextFill(Theme.current().normalTextColor());
                FXUtils.disableFocusColor(this);
            }};
            mergeImportCheckBox = new CheckBox(I18n.get().worldBossTimerMergeImportCheckBox()) {{
                FontManager.get().setFont(this, settings -> settings.setSize(14));
                setTextFill(Theme.current().normalTextColor());
                FXUtils.disableFocusColor(this);
            }};

            addBtn.setOnAction(e -> new AddStage(table, this::save).showAndWait());
            editBtn.setOnAction(e -> {
                var selected = table.getSelectedItem();
                if (selected == null) {
                    return;
                }
                new AddStage(table, selected, this::save).showAndWait();
            });
            spawnBtn.setOnAction(e -> {
                var selected = table.getSelectedItem();
                if (selected == null) {
                    return;
                }
                table.getItems().remove(selected);
                selected.lastKnownKillTs = System.currentTimeMillis();
                table.getItems().add(selected);
                save();
            });
            delBtn.setOnAction(e -> {
                var selected = table.getSelectedItem();
                if (selected == null) {
                    return;
                }
                table.getItems().remove(selected);
                save();
            });
            clearBtn.setOnAction(e -> {
                table.getItems().clear();
                save();
            });
            copyNextBossInfoBtn.setOnAction(e -> copyNextBossInfo());
            exportBtn.setOnAction(e -> {
                var s = genConfig(
                    includeBossTimerCheckBox.isSelected(),
                    includeAccountTimerCheckBox.isSelected(),
                    includeMsgTemplateCheckBox.isSelected()
                );
                var content = new ClipboardContent();
                content.putString(s.stringify());
                Clipboard.getSystemClipboard().setContent(content);
            });
            importBtn.setOnAction(e -> {
                String s = (String) Clipboard.getSystemClipboard().getContent(DataFormat.PLAIN_TEXT);
                if (s == null) {
                    SimpleAlert.showAndWait(Alert.AlertType.WARNING, I18n.get().worldBossTimerNoDataToImport());
                    return;
                }
                Config config;
                try {
                    config = JSON.deserialize(s, Config.rule);
                } catch (Exception ee) {
                    SimpleAlert.showAndWait(Alert.AlertType.WARNING, I18n.get().worldBossTimerInvalidImportingData());
                    return;
                }
                init(config,
                    includeBossTimerCheckBox.isSelected(),
                    includeAccountTimerCheckBox.isSelected(),
                    includeMsgTemplateCheckBox.isSelected(),
                    mergeImportCheckBox.isSelected()
                );
                save();
            });

            var accountAddBtn = new FusionButton(I18n.get().worldBossTimerAddBtn()) {{
                setPrefWidth(120);
                setPrefHeight(32);
            }};
            var accountEditBtn = new FusionButton(I18n.get().worldBossTimerEditBtn()) {{
                setPrefWidth(120);
                setPrefHeight(32);
            }};
            var accountSwitchLineBtn = new FusionButton(I18n.get().worldBossTimerSwitchLineBtn()) {{
                setPrefWidth(120);
                setPrefHeight(32);
            }};
            var accountDelBtn = new FusionButton(I18n.get().worldBossTimerDelBtn()) {{
                setPrefWidth(120);
                setPrefHeight(32);
            }};
            var accountClearBtn = new FusionButton(I18n.get().worldBossTimerClearBtn()) {{
                setPrefWidth(120);
                setPrefHeight(32);
            }};

            accountAddBtn.setOnAction(e -> new AddAccountStage(accounts, this::save).showAndWait());
            accountEditBtn.setOnAction(e -> {
                var selected = accounts.getSelectedItem();
                if (selected == null) {
                    return;
                }
                new AddAccountStage(accounts, selected, this::save).showAndWait();
            });
            accountSwitchLineBtn.setOnAction(e -> {
                var selected = accounts.getSelectedItem();
                if (selected == null) {
                    return;
                }
                accounts.getItems().remove(selected);
                selected.lastSwitchLineTs = System.currentTimeMillis();
                accounts.getItems().add(selected);
                save();
            });
            accountDelBtn.setOnAction(e -> {
                var selected = accounts.getSelectedItem();
                if (selected == null) {
                    return;
                }
                accounts.getItems().remove(selected);
                save();
            });
            accountClearBtn.setOnAction(e -> {
                accounts.getItems().clear();
                save();
            });

            var pane = getContentPane();

            pane.getChildren().addAll(
                new VBox(
                    new VPadding(10),
                    new HBox(
                        new HPadding(10), exportBtn, new HPadding(10), importBtn,
                        new HPadding(20), includeBossTimerCheckBox,
                        new HPadding(25), includeAccountTimerCheckBox,
                        new HPadding(25), includeMsgTemplateCheckBox,
                        new HPadding(25), mergeImportCheckBox
                    ) {{
                        setAlignment(Pos.CENTER_LEFT);
                    }},
                    new VPadding(20),
                    new HBox(
                        new HPadding(10),
                        table.getNode(),
                        new HPadding(10),
                        new FusionPane(false, new VBox(
                            addBtn,
                            new VPadding(5),
                            editBtn,
                            new VPadding(5),
                            delBtn,
                            new VPadding(5),
                            clearBtn,
                            new VPadding(60),
                            spawnBtn,
                            new VPadding(5),
                            copyNextBossInfoBtn,
                            new VPadding(5),
                            new FusionW(nextBossInfoTemplate)
                        )).getNode()
                    ),
                    new VPadding(20),
                    new HBox(
                        new HPadding(10),
                        accounts.getNode(),
                        new HPadding(10),
                        new FusionPane(false, new VBox(
                            accountAddBtn,
                            new VPadding(5),
                            accountEditBtn,
                            new VPadding(5),
                            accountSwitchLineBtn,
                            new VPadding(5),
                            accountDelBtn,
                            new VPadding(5),
                            accountClearBtn
                        )).getNode()
                    )
                )
            );

            pane.widthProperty().addListener((ob, old, now) -> {
                if (now == null) return;
                table.getNode().setPrefWidth(now.doubleValue() - 10 - 120 - 10 - 10 - FusionPane.PADDING_V * 2);
                accounts.getNode().setPrefWidth(now.doubleValue() - 10 - 120 - 10 - 10 - FusionPane.PADDING_V * 2);
            });
            pane.heightProperty().addListener((ob, old, now) -> {
                if (now == null) return;
                table.getNode().setPrefHeight(now.doubleValue() - 10 - 10 - 280);
                accounts.getNode().setPrefHeight(200);
            });

            init();

            FXUtils.observeWidthCenter(getContentPane(), pane);
        }

        private void initTableStructure() {
            var lineColumn = new VTableColumn<BossInfo, Integer>(I18n.get().worldBossTimerLineCol(), i -> i.line);
            var nameColumn = new VTableColumn<BossInfo, String>(I18n.get().worldBossTimerNameCol(), i -> i.name);
            var lastKillColumn = new VTableColumn<BossInfo, Long>(I18n.get().worldBossTimerLastKillCol(), i -> i.lastKnownKillTs);
            var etaColumn = new VTableColumn<BossInfo, BossInfo>(I18n.get().worldBossTimerETACol(), i -> i);
            var commentColumn = new VTableColumn<BossInfo, String>(I18n.get().worldBossTimerCommentCol(), i -> i.comment);

            lineColumn.setMinWidth(50);
            lineColumn.setComparator(Integer::compareTo);
            nameColumn.setMinWidth(120);
            nameColumn.setComparator(String::compareTo);
            lastKillColumn.setMinWidth(200);
            lastKillColumn.setTextBuilder(l -> formatter.format(ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(l),
                ZoneId.systemDefault())));
            lastKillColumn.setComparator(Long::compareTo);
            lastKillColumn.setAlignment(Pos.CENTER);
            etaColumn.setNodeBuilder(i -> i.timerLabel);
            etaColumn.setMinWidth(200);
            etaColumn.setComparator((a, b) -> {
                var aa = a.lastKnownKillTs + a.spawnMinutes * 60_000L;
                var bb = b.lastKnownKillTs + b.spawnMinutes * 60_000L;
                return Long.compare(aa, bb);
            });
            etaColumn.setAlignment(Pos.CENTER);
            commentColumn.setMinWidth(250);

            //noinspection unchecked
            table.getColumns().addAll(lineColumn, nameColumn, lastKillColumn, etaColumn, commentColumn);
        }

        private void initAccountsStructure() {
            var lastLineColumn = new VTableColumn<AccountInfo, Integer>(I18n.get().worldBossTimerLastLineCol(), i -> i.lastLine);
            var nameColumn = new VTableColumn<AccountInfo, String>(I18n.get().worldBossTimerAccountNameCol(), i -> i.name);
            var lastSwitchLineTsColumn = new VTableColumn<AccountInfo, Long>(I18n.get().worldBossTimerLastSwitchLineTsCol(), i -> i.lastSwitchLineTs);
            var etaColumn = new VTableColumn<AccountInfo, AccountInfo>(I18n.get().worldBossTimerAccountETACol(), i -> i);
            var commentColumn = new VTableColumn<AccountInfo, String>(I18n.get().worldBossTimerCommentCol(), i -> i.comment);

            lastLineColumn.setMinWidth(50);
            lastLineColumn.setComparator(Integer::compareTo);
            nameColumn.setMinWidth(120);
            nameColumn.setComparator(String::compareTo);
            lastSwitchLineTsColumn.setMinWidth(200);
            lastSwitchLineTsColumn.setTextBuilder(l -> formatter.format(ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(l),
                ZoneId.systemDefault())));
            lastSwitchLineTsColumn.setComparator(Long::compareTo);
            lastSwitchLineTsColumn.setAlignment(Pos.CENTER);
            etaColumn.setNodeBuilder(i -> i.timerLabel);
            etaColumn.setMinWidth(200);
            etaColumn.setComparator((a, b) -> {
                var aa = a.lastSwitchLineTs + a.switchLineCDMinutes * 60_000L;
                var bb = b.lastSwitchLineTs + b.switchLineCDMinutes * 60_000L;
                return Long.compare(aa, bb);
            });
            etaColumn.setAlignment(Pos.CENTER);
            commentColumn.setMinWidth(250);

            //noinspection unchecked
            accounts.getColumns().addAll(lastLineColumn, nameColumn, lastSwitchLineTsColumn, etaColumn, commentColumn);
        }

        private String lastUsedTemplate = null;

        private void copyNextBossInfo() {
            var selected = table.getSelectedItem();
            if (selected == null) {
                return;
            }
            var template = nextBossInfoTemplate.getText();
            var remainingMillis = selected.lastKnownKillTs + selected.spawnMinutes * 60L * 1000 - System.currentTimeMillis();
            var spawnTime =
                ZonedDateTime.ofInstant(
                    Instant.ofEpochMilli(selected.lastKnownKillTs + selected.spawnMinutes * 60L * 1000),
                    ZoneId.systemDefault());
            var hh = "" + spawnTime.getHour();
            if (hh.length() < 2) {
                hh = "0" + hh;
            }
            var mm = "" + spawnTime.getMinute();
            if (mm.length() < 2) {
                mm = "0" + mm;
            }
            var ss = "" + spawnTime.getSecond();
            if (ss.length() < 2) {
                ss = "0" + ss;
            }
            String mainScript = "{" +
                                "var msg = ('')\n" +
                                "var name = ('" + selected.name + "')\n" +
                                "var hh = ('" + hh + "')\n" +
                                "var mm = ('" + mm + "')\n" +
                                "var ss = ('" + ss + "')\n" +
                                "var line = " + selected.line + "\n" +
                                "var remainingMillis = " + remainingMillis + "\n" +
                                "#include \"template\"\n" +
                                "}\n";
            Manager<String> manager = (name) -> {
                if (name.equals("main")) return () -> mainScript;
                else if (name.equals("template")) return () -> template;
                return null;
            };
            String msg = null;
            try {
                var interpreter = new InterpreterBuilder().compile(manager, "main");
                var explorer = interpreter.getExplorer();
                var mem = interpreter.execute();
                msg = (String) explorer.getVariable("msg", mem);
            } catch (ParserException e) {
                SimpleAlert.show(Alert.AlertType.ERROR, I18n.get().worldBossTimerInvalidTemplate() + ": " + e.getMessage());
            }
            if (msg != null) {
                var content = new ClipboardContent();
                content.putString(msg);
                Clipboard.getSystemClipboard().setContent(content);

                if (lastUsedTemplate == null || !lastUsedTemplate.equals(template)) {
                    save();
                }
                lastUsedTemplate = template;
            }
        }

        private JSON.Object genConfig() {
            return genConfig(true, true, true);
        }

        private JSON.Object genConfig(boolean includeBoss, boolean includeAccount, boolean includeTemplate) {
            var config = new Config();
            if (includeBoss) {
                config.list = new ArrayList<>(table.getItems());
            }
            if (includeAccount) {
                config.accounts = new ArrayList<>(accounts.getItems());
            }
            if (includeTemplate) {
                config.template = nextBossInfoTemplate.getText();
            }
            return config.toJson();
        }

        private void save() {
            var config = genConfig();
            var str = config.pretty();
            try {
                IOUtils.writeFile(recordFilePath, str);
            } catch (IOException e) {
                Logger.error("failed saving config file to " + recordFilePath, e);
            }
        }

        private void init() throws Exception {
            Config c = null;
            if (recordFile.isFile()) {
                var str = Files.readString(recordFilePath);
                if (!str.isBlank()) {
                    try {
                        c = JSON.deserialize(str, Config.rule);
                    } catch (Exception e) {
                        Logger.error("failed deserializing config from " + recordFilePath, e);
                        // silently delete the file and proceed
                        try {
                            Files.delete(recordFilePath);
                        } catch (Exception ee) {
                            Logger.error("failed deleting config file " + recordFilePath, ee);
                        }
                    }
                }
            }
            if (c != null) {
                init(c);
            }
        }

        private void init(Config c) {
            init(c, true, true, true, false);
        }

        private void init(Config c, boolean includeBoss, boolean includeAccount, boolean includeTemplate, boolean merge) {
            if (includeBoss) {
                initList(c.list, merge);
            }
            if (includeAccount) {
                initAccounts(c.accounts, merge);
            }
            if (includeTemplate) {
                initTemplate(c.template);
            }
        }

        private void initList(List<BossInfo> list, boolean merge) {
            if (list == null) return;
            if (merge) {
                if (table.getItems() != null) {
                    for (var info : list) {
                        table.getItems().removeIf(i -> i.line == info.line && i.name.equals(info.name));
                    }
                    table.getItems().addAll(list);
                    return;
                }
            }
            table.setItems(list);
        }

        private void initAccounts(List<AccountInfo> list, boolean merge) {
            if (list == null) return;
            if (merge) {
                if (accounts.getItems() != null) {
                    for (var info : list) {
                        table.getItems().removeIf(i -> i.name.equals(info.name));
                    }
                    accounts.getItems().addAll(list);
                    return;

                }
            }
            accounts.setItems(list);
        }

        private void initTemplate(String template) {
            if (template != null && !template.isBlank()) {
                nextBossInfoTemplate.setText(template);
            }
        }
    }

    private static class Config {
        List<BossInfo> list;
        List<AccountInfo> accounts;
        String template;

        static final Rule<Config> rule = new ObjectRule<>(Config::new)
            .put("list", (o, it) -> o.list = it,
                new ArrayRule<List<BossInfo>, BossInfo>(ArrayList::new, List::add, BossInfo.rule))
            .put("accounts", (o, it) -> o.accounts = it,
                new ArrayRule<List<AccountInfo>, AccountInfo>(ArrayList::new, List::add, AccountInfo.rule))
            .put("template", (o, it) -> o.template = it, StringRule.get());

        JSON.Object toJson() {
            var ob = new ObjectBuilder();
            if (list != null) {
                ob.putArray("list", arr -> list.forEach(e -> arr.addInst(e.toJson())));
            }
            if (accounts != null) {
                ob.putArray("accounts", arr -> accounts.forEach(e -> arr.addInst(e.toJson())));
            }
            if (template != null) {
                ob.put("template", template);
            }
            return ob.build();
        }
    }

    private static class AddStage extends VStage {
        private static String lastBossName = null;

        AddStage(VTableView<BossInfo> table, Runnable saveCB) {
            this(table, null, 0, null, 0, 0, null, saveCB);
        }

        AddStage(VTableView<BossInfo> table,
                 BossInfo oldInfo,
                 int line, String name, long lastKill, int spawnMinutes, String comment,
                 Runnable saveCB) {
            var current = LocalDateTime.now();

            var pane = getInitialScene().getContentPane();

            var lineLabel = new ThemeLabel(I18n.get().worldBossTimerLineCol()) {{
                FontManager.get().setFont(Consts.NotoFont, this);
                setPrefWidth(160);
                setAlignment(Pos.CENTER_RIGHT);
                setPadding(new Insets(5, 0, 0, 0));
            }};
            var nameLabel = new ThemeLabel(I18n.get().worldBossTimerNameCol()) {{
                FontManager.get().setFont(Consts.NotoFont, this);
                setPrefWidth(160);
                setAlignment(Pos.CENTER_RIGHT);
                setPadding(new Insets(5, 0, 0, 0));
            }};
            var lastKillLabel = new ThemeLabel(I18n.get().worldBossTimerLastKillCol()) {{
                FontManager.get().setFont(Consts.NotoFont, this);
                setPrefWidth(160);
                setAlignment(Pos.CENTER_RIGHT);
                setPadding(new Insets(5, 0, 0, 0));
            }};
            var spawnMinutesLabel = new ThemeLabel(I18n.get().worldBossTimerSpawnMinutesCol()) {{
                FontManager.get().setFont(Consts.NotoFont, this);
                setPrefWidth(160);
                setAlignment(Pos.CENTER_RIGHT);
                setPadding(new Insets(5, 0, 0, 0));
            }};
            var commentLabel = new ThemeLabel(I18n.get().worldBossTimerCommentCol()) {{
                FontManager.get().setFont(Consts.NotoFont, this);
                setPrefWidth(160);
                setAlignment(Pos.CENTER_RIGHT);
                setPadding(new Insets(5, 0, 0, 0));
            }};
            var lineInput = new TextField() {{
                FontManager.get().setFont(Consts.NotoFont, this);
                setPrefWidth(250);
                if (oldInfo != null && line != 0) {
                    setText("" + line);
                }
            }};
            var nameInput = new TextField() {{
                FontManager.get().setFont(Consts.NotoFont, this);
                setPrefWidth(250);
                if (oldInfo != null && name != null) {
                    setText(name);
                } else if (lastBossName != null) {
                    setText(lastBossName);
                }
            }};
            var lastKillInput = new TextField() {{
                FontManager.get().setFont(Consts.NotoFont, this);
                setPrefWidth(250);
                if (oldInfo != null && lastKill != 0) {
                    setText(ZonedDateTime.ofInstant(Instant.ofEpochMilli(lastKill), ZoneId.systemDefault()).format(formatter));
                } else {
                    setText(current.format(formatter));
                }
            }};
            var spawnMinutesInput = new TextField() {{
                FontManager.get().setFont(Consts.NotoFont, this);
                setPrefWidth(250);
                if (oldInfo != null && spawnMinutes != 0) {
                    setText("" + spawnMinutes);
                } else {
                    setText("60");
                }
            }};
            var commentInput = new TextField() {{
                FontManager.get().setFont(Consts.NotoFont, this);
                setPrefWidth(250);
                if (oldInfo != null && comment != null) {
                    setText(comment);
                } else {
                    setText("");
                }
            }};

            var okBtn = new FusionButton(I18n.get().worldBossTimerOkBtn()) {{
                FontManager.get().setFont(Consts.NotoFont, getTextNode());
            }};
            okBtn.setPrefWidth(120);
            okBtn.setPrefHeight(32);
            okBtn.setOnAction(e -> {
                if (lineInput.getText().isBlank()) {
                    SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerMissingLine());
                    return;
                }
                if (nameInput.getText().isBlank()) {
                    SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerMissingName());
                    return;
                }
                if (lastKillInput.getText().isBlank()) {
                    SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerMissingLastKill());
                    return;
                }
                if (spawnMinutesInput.getText().isBlank()) {
                    SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerMissingSpawnMinutes());
                    return;
                }
                int vLine;
                try {
                    vLine = Integer.parseInt(lineInput.getText().trim());
                } catch (NumberFormatException ex) {
                    SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerInvalidLine());
                    return;
                }
                LocalDateTime vLastKill;
                try {
                    vLastKill = LocalDateTime.parse(lastKillInput.getText().trim(), formatter);
                } catch (DateTimeParseException ex) {
                    SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerInvalidLastKill());
                    return;
                }
                int vSpawnMinutes;
                try {
                    vSpawnMinutes = Integer.parseInt(spawnMinutesInput.getText().trim());
                } catch (NumberFormatException ex) {
                    SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerInvalidSpawnMinutes());
                    return;
                }

                if (vLine < 1) {
                    SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerInvalidLine());
                    return;
                }
                if (vSpawnMinutes < 0) {
                    SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerInvalidSpawnMinutes());
                    return;
                }

                if (oldInfo != null) {
                    table.getItems().remove(oldInfo);
                }

                var info = new BossInfo();
                info.line = vLine;
                info.name = nameInput.getText();
                info.lastKnownKillTs = ZonedDateTime.of(vLastKill, ZoneId.systemDefault()).toEpochSecond() * 1000;
                info.spawnMinutes = vSpawnMinutes;
                info.comment = Optional.of(commentInput.getText()).orElse("");

                lastBossName = info.name;

                var opt = table.getItems().stream().filter(i -> i.line == info.line && i.name.equals(info.name)).findAny();
                //noinspection OptionalIsPresent
                if (opt.isPresent()) {
                    table.getItems().remove(opt.get());
                }
                table.getItems().add(info);
                saveCB.run();

                close();
            });

            pane.getChildren().add(new VBox(
                new VPadding(10),
                new HBox(
                    new HPadding(10),
                    new VBox(
                        new HBox(lineLabel, new HPadding(10), new FusionW(lineInput)),
                        new VPadding(5),
                        new HBox(nameLabel, new HPadding(10), new FusionW(nameInput)),
                        new VPadding(5),
                        new HBox(lastKillLabel, new HPadding(10), new FusionW(lastKillInput)),
                        new VPadding(5),
                        new HBox(spawnMinutesLabel, new HPadding(10), new FusionW(spawnMinutesInput)),
                        new VPadding(5),
                        new HBox(commentLabel, new HPadding(10), new FusionW(commentInput))
                    )
                ),
                new VPadding(10),
                new HBox(new HPadding(165), okBtn)
            ));

            getStage().setWidth(450);
            getStage().setHeight(320);
            getInitialScene().enableAutoContentWidthHeight();
        }

        public AddStage(VTableView<BossInfo> table, BossInfo info, Runnable saveCB) {
            this(table, info, info.line, info.name, info.lastKnownKillTs, info.spawnMinutes, info.comment, saveCB);
        }
    }

    private static class AddAccountStage extends VStage {
        AddAccountStage(VTableView<AccountInfo> table, Runnable saveCB) {
            this(table, null, 0, null, 0, 0, null, saveCB);
        }

        AddAccountStage(VTableView<AccountInfo> table,
                        AccountInfo oldInfo,
                        int line, String name, long lastSwitchTs, int cd, String comment,
                        Runnable saveCB) {
            var current = LocalDateTime.now();

            var pane = getInitialScene().getContentPane();

            var lineLabel = new ThemeLabel(I18n.get().worldBossTimerLineCol()) {{
                FontManager.get().setFont(Consts.NotoFont, this);
                setPrefWidth(160);
                setAlignment(Pos.CENTER_RIGHT);
                setPadding(new Insets(5, 0, 0, 0));
            }};
            var nameLabel = new ThemeLabel(I18n.get().worldBossTimerAccountNameCol()) {{
                FontManager.get().setFont(Consts.NotoFont, this);
                setPrefWidth(160);
                setAlignment(Pos.CENTER_RIGHT);
                setPadding(new Insets(5, 0, 0, 0));
            }};
            var lastSwitchLineTsLabel = new ThemeLabel(I18n.get().worldBossTimerLastSwitchLineTsCol()) {{
                FontManager.get().setFont(Consts.NotoFont, this);
                setPrefWidth(160);
                setAlignment(Pos.CENTER_RIGHT);
                setPadding(new Insets(5, 0, 0, 0));
            }};
            var switchLineCDLabel = new ThemeLabel(I18n.get().worldBossTimerSwitchLineCDMinutes()) {{
                FontManager.get().setFont(Consts.NotoFont, this);
                setPrefWidth(160);
                setAlignment(Pos.CENTER_RIGHT);
                setPadding(new Insets(5, 0, 0, 0));
            }};
            var commentLabel = new ThemeLabel(I18n.get().worldBossTimerCommentCol()) {{
                FontManager.get().setFont(Consts.NotoFont, this);
                setPrefWidth(160);
                setAlignment(Pos.CENTER_RIGHT);
                setPadding(new Insets(5, 0, 0, 0));
            }};
            var lineInput = new TextField() {{
                FontManager.get().setFont(Consts.NotoFont, this);
                setPrefWidth(250);
                if (oldInfo != null && line != 0) {
                    setText("" + line);
                }
            }};
            var nameInput = new TextField() {{
                FontManager.get().setFont(Consts.NotoFont, this);
                setPrefWidth(250);
                if (oldInfo != null && name != null) {
                    setText(name);
                }
            }};
            var lastSwitchLineCDInput = new TextField() {{
                FontManager.get().setFont(Consts.NotoFont, this);
                setPrefWidth(250);
                if (oldInfo != null && lastSwitchTs != 0) {
                    setText(ZonedDateTime.ofInstant(Instant.ofEpochMilli(lastSwitchTs), ZoneId.systemDefault()).format(formatter));
                } else {
                    setText(current.format(formatter));
                }
            }};
            var cdInput = new TextField() {{
                FontManager.get().setFont(Consts.NotoFont, this);
                setPrefWidth(250);
                if (oldInfo != null && cd != 0) {
                    setText("" + cd);
                } else {
                    setText("30");
                }
            }};
            var commentInput = new TextField() {{
                FontManager.get().setFont(Consts.NotoFont, this);
                setPrefWidth(250);
                if (oldInfo != null && comment != null) {
                    setText(comment);
                } else {
                    setText("");
                }
            }};

            var okBtn = new FusionButton(I18n.get().worldBossTimerOkBtn()) {{
                FontManager.get().setFont(Consts.NotoFont, getTextNode());
            }};
            okBtn.setPrefWidth(120);
            okBtn.setPrefHeight(32);
            okBtn.setOnAction(e -> {
                if (lineInput.getText().isBlank()) {
                    SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerMissingLine());
                    return;
                }
                if (nameInput.getText().isBlank()) {
                    SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerMissingName());
                    return;
                }
                if (lastSwitchLineCDInput.getText().isBlank()) {
                    SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerMissingLastSwitchLineTs());
                    return;
                }
                if (cdInput.getText().isBlank()) {
                    SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerMissingSwitchLineCD());
                    return;
                }
                int vLine;
                try {
                    vLine = Integer.parseInt(lineInput.getText().trim());
                } catch (NumberFormatException ex) {
                    SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerInvalidLine());
                    return;
                }
                LocalDateTime vLastSwitchLineCD;
                try {
                    vLastSwitchLineCD = LocalDateTime.parse(lastSwitchLineCDInput.getText().trim(), formatter);
                } catch (DateTimeParseException ex) {
                    SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerInvalidLastSwitchLineTs());
                    return;
                }
                int vCD;
                try {
                    vCD = Integer.parseInt(cdInput.getText().trim());
                } catch (NumberFormatException ex) {
                    SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerInvalidSwitchLineCD());
                    return;
                }

                if (vLine < 1) {
                    SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerInvalidLine());
                    return;
                }
                if (vCD < 0) {
                    SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerInvalidSwitchLineCD());
                    return;
                }

                if (oldInfo != null) {
                    table.getItems().remove(oldInfo);
                }

                var info = new AccountInfo();
                info.lastLine = vLine;
                info.name = nameInput.getText();
                info.lastSwitchLineTs = ZonedDateTime.of(vLastSwitchLineCD, ZoneId.systemDefault()).toEpochSecond() * 1000;
                info.switchLineCDMinutes = vCD;
                info.comment = Optional.of(commentInput.getText()).orElse("");

                var opt = table.getItems().stream().filter(i -> i.name.equals(info.name)).findAny();
                //noinspection OptionalIsPresent
                if (opt.isPresent()) {
                    table.getItems().remove(opt.get());
                }
                table.getItems().add(info);
                saveCB.run();

                close();
            });

            pane.getChildren().add(new VBox(
                new VPadding(10),
                new HBox(
                    new HPadding(10),
                    new VBox(
                        new HBox(lineLabel, new HPadding(10), new FusionW(lineInput)),
                        new VPadding(5),
                        new HBox(nameLabel, new HPadding(10), new FusionW(nameInput)),
                        new VPadding(5),
                        new HBox(lastSwitchLineTsLabel, new HPadding(10), new FusionW(lastSwitchLineCDInput)),
                        new VPadding(5),
                        new HBox(switchLineCDLabel, new HPadding(10), new FusionW(cdInput)),
                        new VPadding(5),
                        new HBox(commentLabel, new HPadding(10), new FusionW(commentInput))
                    )
                ),
                new VPadding(10),
                new HBox(new HPadding(165), okBtn)
            ));

            getStage().setWidth(450);
            getStage().setHeight(320);
            getInitialScene().enableAutoContentWidthHeight();
        }

        public AddAccountStage(VTableView<AccountInfo> table, AccountInfo info, Runnable saveCB) {
            this(table, info, info.lastLine, info.name, info.lastSwitchLineTs, info.switchLineCDMinutes, info.comment, saveCB);
        }
    }

    public static class BossInfo {
        public int line;
        public String name;
        public long lastKnownKillTs;
        public int spawnMinutes;
        public String comment;

        public final TimerLabel timerLabel = new TimerLabel(
            Theme.current().normalTextColor(),
            new Color(0xe8 / 255d, 0x98 / 255d, 0x70 / 255d, 1),
            new Color(0xf3 / 255d, 0x85 / 255d, 0x85 / 255d, 1));

        static final Rule<BossInfo> rule = new ObjectRule<>(BossInfo::new)
            .put("line", (o, it) -> o.line = it, IntRule.get())
            .put("name", (o, it) -> o.name = it, StringRule.get())
            .put("lastKnownKillTs", (o, it) -> o.lastKnownKillTs = it, LongRule.get())
            .put("spawnMinutes", (o, it) -> o.spawnMinutes = it, IntRule.get())
            .put("comment", (o, it) -> o.comment = it, NullableStringRule.get());

        JSON.Object toJson() {
            return new ObjectBuilder()
                .put("line", line)
                .put("name", name)
                .put("lastKnownKillTs", lastKnownKillTs)
                .put("spawnMinutes", spawnMinutes)
                .put("comment", comment)
                .build();
        }
    }

    public static class AccountInfo {
        public String name;
        public long lastSwitchLineTs;
        public int lastLine;
        public int switchLineCDMinutes;
        public String comment;

        public final TimerLabel timerLabel = new TimerLabel(
            Theme.current().normalTextColor(),
            new Color(0xe8 / 255d, 0x98 / 255d, 0x70 / 255d, 1),
            new Color(0x8d / 255d, 0xbd / 255d, 0x74 / 255d, 1));

        static final Rule<AccountInfo> rule = new ObjectRule<>(AccountInfo::new)
            .put("name", (o, it) -> o.name = it, StringRule.get())
            .put("lastSwitchLineTs", (o, it) -> o.lastSwitchLineTs = it, LongRule.get())
            .put("lastLine", (o, it) -> o.lastLine = it, IntRule.get())
            .put("switchLineCDMinutes", (o, it) -> o.switchLineCDMinutes = it, IntRule.get())
            .put("comment", (o, it) -> o.comment = it, NullableStringRule.get());

        JSON.Object toJson() {
            return new ObjectBuilder()
                .put("name", name)
                .put("lastSwitchLineTs", lastSwitchLineTs)
                .put("lastLine", lastLine)
                .put("switchLineCDMinutes", switchLineCDMinutes)
                .put("comment", comment)
                .build();
        }
    }

    static class TimerLabel extends Label {
        private final Color normalColor;
        private final Color almostReachesColor;
        private final Color exceedColor;

        public TimerLabel(Color normalColor, Color almostReachesColor, Color exceedColor) {
            super("00:00:00");
            this.normalColor = normalColor;
            this.almostReachesColor = almostReachesColor;
            this.exceedColor = exceedColor;
            setTextFill(almostReachesColor);
        }

        public void setMillis(long millis) {
            boolean negative = millis < 0;
            millis = Math.abs(millis);

            long h = millis / 1_000 / 60 / 60;
            long m = (millis / 1_000 / 60) % 60;
            long s = (millis / 1_000) % 60;

            String hh = (h < 10 ? "0" : "") + h;
            String mm = (m < 10 ? "0" : "") + m;
            String ss = (s < 10 ? "0" : "") + s;

            setText((negative ? "-" : "") + hh + ":" + mm + ":" + ss);
            if (negative) {
                setTextFill(exceedColor);
            } else if (millis < 120_000) {
                setTextFill(almostReachesColor);
            } else {
                setTextFill(normalColor);
            }
        }
    }
}
