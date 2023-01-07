package net.cassite.hottapcassistant.tool;

import javafx.animation.AnimationTimer;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.cassite.hottapcassistant.component.HPadding;
import net.cassite.hottapcassistant.component.VPadding;
import net.cassite.hottapcassistant.config.AssistantConfig;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.*;
import vjson.JSON;
import vjson.deserializer.rule.*;
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
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.temporal.ChronoField.*;

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
    protected Stage buildStage() throws Exception {
        return new S();
    }

    @Override
    protected void terminate0() {
        var stage = (S) this.stage;
        if (stage != null) {
            stage.etaTimer.stop();
        }
    }

    private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .append(ISO_LOCAL_DATE)
        .appendLiteral(' ')
        .append(new DateTimeFormatterBuilder()
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .toFormatter())
        .toFormatter();

    private static class S extends Stage {
        static final Path recordFilePath = Path.of(AssistantConfig.assistantDirPath.toString(), "WorldBossTimer.vjson.txt");
        static final File recordFile = recordFilePath.toFile();

        private final TableView<BossInfo> table = new TableView<>();
        private final TableView<AccountInfo> accounts = new TableView<>();
        private final AnimationTimer etaTimer;

        S() throws Exception {
            ScrollBar hScrollBar = (ScrollBar) table.lookup(".scroll-bar:horizontal");
            if (hScrollBar != null) {
                hScrollBar.setVisible(false);
            }

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

            var addBtn = new Button(I18n.get().worldBossTimerAddBtn()) {{
                FontManager.setFont(this);
                setPrefWidth(120);
            }};
            var editBtn = new Button(I18n.get().worldBossTimerEditBtn()) {{
                FontManager.setFont(this);
                setPrefWidth(120);
            }};
            var spawnBtn = new Button(I18n.get().worldBossTimerSpawnBtn()) {{
                FontManager.setFont(this);
                setPrefWidth(120);
            }};
            var delBtn = new Button(I18n.get().worldBossTimerDelBtn()) {{
                FontManager.setFont(this);
                setPrefWidth(120);
            }};
            var clearBtn = new Button(I18n.get().worldBossTimerClearBtn()) {{
                FontManager.setFont(this);
                setPrefWidth(120);
            }};
            var copyNextBossInfoBtn = new Button(I18n.get().worldBossTimerCopyBossInfoBtn()) {{
                FontManager.setFont(this);
                setPrefWidth(120);
                setPrefHeight(60);
            }};
            var exportBtn = new Button(I18n.get().worldBossTimerExportBtn()) {{
                FontManager.setFont(this);
                setPrefWidth(120);
            }};
            var importBtn = new Button(I18n.get().worldBossTimerImportBtn()) {{
                FontManager.setFont(this);
                setPrefWidth(120);
            }};

            addBtn.setOnAction(e -> new AddStage(table).showAndWait());
            editBtn.setOnAction(e -> {
                var selected = table.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    return;
                }
                new AddStage(table, selected).showAndWait();
            });
            spawnBtn.setOnAction(e -> {
                var selected = table.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    return;
                }
                table.getItems().remove(selected);
                selected.lastKnownKillTs = System.currentTimeMillis();
                Utils.runDelay(100, () -> table.getItems().add(selected));
            });
            delBtn.setOnAction(e -> {
                var selected = table.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    return;
                }
                table.getItems().remove(selected);
            });
            clearBtn.setOnAction(e -> table.getItems().clear());
            copyNextBossInfoBtn.setOnAction(e -> copyNextBossInfo());
            exportBtn.setOnAction(e -> {
                var s = genConfig();
                var content = new ClipboardContent();
                content.putString(s.stringify());
                Clipboard.getSystemClipboard().setContent(content);
            });
            importBtn.setOnAction(e -> {
                String s = (String) Clipboard.getSystemClipboard().getContent(DataFormat.PLAIN_TEXT);
                if (s == null) {
                    new SimpleAlert(Alert.AlertType.WARNING, I18n.get().worldBossTimerNoDataToImport()).showAndWait();
                    return;
                }
                Config config;
                try {
                    config = JSON.deserialize(s, Config.rule);
                } catch (Exception ee) {
                    new SimpleAlert(Alert.AlertType.WARNING, I18n.get().worldBossTimerInvalidImportingData()).showAndWait();
                    return;
                }
                init(config);
            });

            var accountAddBtn = new Button(I18n.get().worldBossTimerAddBtn()) {{
                FontManager.setFont(this);
                setPrefWidth(120);
            }};
            var accountEditBtn = new Button(I18n.get().worldBossTimerEditBtn()) {{
                FontManager.setFont(this);
                setPrefWidth(120);
            }};
            var accountSwitchLineBtn = new Button(I18n.get().worldBossTimerSwitchLineBtn()) {{
                FontManager.setFont(this);
                setPrefWidth(120);
            }};
            var accountDelBtn = new Button(I18n.get().worldBossTimerDelBtn()) {{
                FontManager.setFont(this);
                setPrefWidth(120);
            }};
            var accountClearBtn = new Button(I18n.get().worldBossTimerClearBtn()) {{
                FontManager.setFont(this);
                setPrefWidth(120);
            }};

            accountAddBtn.setOnAction(e -> new AddAccountStage(accounts).showAndWait());
            accountEditBtn.setOnAction(e -> {
                var selected = accounts.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    return;
                }
                new AddAccountStage(accounts, selected).showAndWait();
            });
            accountSwitchLineBtn.setOnAction(e -> {
                var selected = accounts.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    return;
                }
                accounts.getItems().remove(selected);
                selected.lastSwitchLineTs = System.currentTimeMillis();
                Utils.runDelay(100, () -> accounts.getItems().add(selected));
            });
            accountDelBtn.setOnAction(e -> {
                var selected = accounts.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    return;
                }
                accounts.getItems().remove(selected);
            });
            accountClearBtn.setOnAction(e -> accounts.getItems().clear());

            var pane = new Pane();
            var scene = new Scene(pane);
            setScene(scene);

            pane.getChildren().addAll(
                new VBox(
                    new VPadding(10),
                    new HBox(
                        new HPadding(10), exportBtn, new HPadding(10), importBtn
                    ),
                    new Separator() {{
                        setPadding(new Insets(10, 0, 10, 10));
                    }},
                    new HBox(
                        new HPadding(10),
                        table,
                        new HPadding(10),
                        new VBox(
                            addBtn,
                            new VPadding(5),
                            editBtn,
                            new VPadding(5),
                            spawnBtn,
                            new VPadding(5),
                            delBtn,
                            new VPadding(5),
                            clearBtn,
                            new VPadding(60),
                            copyNextBossInfoBtn
                        )
                    ),
                    new Separator() {{
                        setPadding(new Insets(10, 0, 10, 10));
                    }},
                    new HBox(
                        new HPadding(10),
                        accounts,
                        new HPadding(10),
                        new VBox(
                            accountAddBtn,
                            new VPadding(5),
                            accountEditBtn,
                            new VPadding(5),
                            accountSwitchLineBtn,
                            new VPadding(5),
                            accountDelBtn,
                            new VPadding(5),
                            accountClearBtn
                        )
                    )
                )
            );

            pane.widthProperty().addListener((ob, old, now) -> {
                if (now == null) return;
                table.setPrefWidth(now.doubleValue() - 10 - 120 - 10 - 10);
                accounts.setPrefWidth(now.doubleValue() - 10 - 120 - 10 - 10);
            });
            pane.heightProperty().addListener((ob, old, now) -> {
                if (now == null) return;
                table.setPrefHeight(now.doubleValue() - 10 - 10 - 280);
                accounts.setPrefHeight(200);
            });

            init();
            table.getItems().addListener((ListChangeListener<BossInfo>) c -> save());
            accounts.getItems().addListener((ListChangeListener<AccountInfo>) c -> save());

            setWidth(1024);
            setHeight(960);
            centerOnScreen();
        }

        private void initTableStructure() {
            var lineColumn = new TableColumn<BossInfo, Integer>(I18n.get().worldBossTimerLineCol());
            var nameColumn = new TableColumn<BossInfo, String>(I18n.get().worldBossTimerNameCol());
            var lastKillColumn = new TableColumn<BossInfo, String>(I18n.get().worldBossTimerLastKillCol());
            var etaColumn = new TableColumn<BossInfo, Long>(I18n.get().worldBossTimerETACol());
            var commentColumn = new TableColumn<BossInfo, String>(I18n.get().worldBossTimerCommentCol());

            lineColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().line));
            lineColumn.setMinWidth(50);
            nameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().name));
            nameColumn.setMinWidth(120);
            lastKillColumn.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(
                    formatter.format(ZonedDateTime.ofInstant(
                        Instant.ofEpochMilli(param.getValue().lastKnownKillTs),
                        ZoneId.systemDefault()))));
            lastKillColumn.setMinWidth(200);
            etaColumn.setCellFactory(param -> {
                var cell = new TableCell<BossInfo, Long>();
                cell.itemProperty().addListener((o, old, now) -> {
                    if (cell.getTableRow() == null || cell.getTableRow().getItem() == null) {
                        cell.setGraphic(null);
                        return;
                    }
                    cell.setGraphic(cell.getTableRow().getItem().timerLabel);
                });
                return cell;
            });
            etaColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(
                Instant.ofEpochMilli(param.getValue().lastKnownKillTs)
                    .plusSeconds(param.getValue().spawnMinutes * 60L)
                    .toEpochMilli()));
            etaColumn.setMinWidth(200);
            commentColumn.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().comment == null ? "" : param.getValue().comment));
            commentColumn.setMinWidth(250);

            //noinspection unchecked
            table.getColumns().addAll(lineColumn, nameColumn, lastKillColumn, etaColumn, commentColumn);
        }

        private void initAccountsStructure() {
            var lastLineColumn = new TableColumn<AccountInfo, Integer>(I18n.get().worldBossTimerLastLineCol());
            var nameColumn = new TableColumn<AccountInfo, String>(I18n.get().worldBossTimerAccountNameCol());
            var lastSwitchLineTsColumn = new TableColumn<AccountInfo, String>(I18n.get().worldBossTimerLastSwitchLineTsCol());
            var etaColumn = new TableColumn<AccountInfo, Long>(I18n.get().worldBossTimerAccountETACol());
            var commentColumn = new TableColumn<AccountInfo, String>(I18n.get().worldBossTimerCommentCol());

            lastLineColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().lastLine));
            lastLineColumn.setMinWidth(50);
            nameColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().name));
            nameColumn.setMinWidth(120);
            lastSwitchLineTsColumn.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(
                    formatter.format(ZonedDateTime.ofInstant(
                        Instant.ofEpochMilli(param.getValue().lastSwitchLineTs),
                        ZoneId.systemDefault()))));
            lastSwitchLineTsColumn.setMinWidth(200);
            etaColumn.setCellFactory(param -> {
                var cell = new TableCell<AccountInfo, Long>();
                cell.itemProperty().addListener((o, old, now) -> {
                    if (cell.getTableRow() == null || cell.getTableRow().getItem() == null) {
                        cell.setGraphic(null);
                        return;
                    }
                    cell.setGraphic(cell.getTableRow().getItem().timerLabel);
                });
                return cell;
            });
            etaColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(
                Instant.ofEpochMilli(param.getValue().lastSwitchLineTs)
                    .plusSeconds(param.getValue().switchLineCDMinutes * 60L)
                    .toEpochMilli()));
            etaColumn.setMinWidth(200);
            commentColumn.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().comment == null ? "" : param.getValue().comment));
            commentColumn.setMinWidth(250);

            //noinspection unchecked
            accounts.getColumns().addAll(lastLineColumn, nameColumn, lastSwitchLineTsColumn, etaColumn, commentColumn);
        }

        private void copyNextBossInfo() {
            var selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                return;
            }
            var remainingMillis = selected.lastKnownKillTs + selected.spawnMinutes * 60L * 1000 - System.currentTimeMillis();
            var s = I18n.get().worldBossTimerNextBossInfo(selected, remainingMillis);
            var content = new ClipboardContent();
            content.putString(s);
            Clipboard.getSystemClipboard().setContent(content);
        }

        private JSON.Object genConfig() {
            var config = new Config();
            config.list = new ArrayList<>(table.getItems());
            config.accounts = new ArrayList<>(accounts.getItems());
            return config.toJson();
        }

        private void save() {
            var config = genConfig();
            var str = config.pretty();
            try {
                Utils.writeFile(recordFilePath, str);
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
            initList(c.list);
            initAccounts(c.accounts);
        }

        private void initList(List<BossInfo> list) {
            if (list == null) return;
            table.setItems(FXCollections.observableList(list));
        }

        private void initAccounts(List<AccountInfo> list) {
            if (list == null) return;
            accounts.setItems(FXCollections.observableList(list));
        }
    }

    private static class Config {
        List<BossInfo> list;
        List<AccountInfo> accounts;

        static final Rule<Config> rule = new ObjectRule<>(Config::new)
            .put("list", (o, it) -> o.list = it,
                new ArrayRule<List<BossInfo>, BossInfo>(ArrayList::new, List::add, BossInfo.rule))
            .put("accounts", (o, it) -> o.accounts = it,
                new ArrayRule<List<AccountInfo>, AccountInfo>(ArrayList::new, List::add, AccountInfo.rule));

        JSON.Object toJson() {
            var ob = new ObjectBuilder();
            if (list != null) {
                ob.putArray("list", arr -> list.forEach(e -> arr.addInst(e.toJson())));
            }
            if (accounts != null) {
                ob.putArray("accounts", arr -> accounts.forEach(e -> arr.addInst(e.toJson())));
            }
            return ob.build();
        }
    }

    private static class AddStage extends Stage {
        private static String lastBossName = null;

        AddStage(TableView<BossInfo> table) {
            this(table, null, 0, null, 0, 0, null);
        }

        AddStage(TableView<BossInfo> table,
                 BossInfo oldInfo,
                 int line, String name, long lastKill, int spawnMinutes, String comment) {
            var current = LocalDateTime.now();

            var pane = new Pane();
            var scene = new Scene(pane);
            setScene(scene);

            var lineLabel = new Label(I18n.get().worldBossTimerLineCol()) {{
                FontManager.setNoto(this);
                setPrefWidth(160);
                setAlignment(Pos.CENTER_RIGHT);
                setPadding(new Insets(5, 0, 0, 0));
            }};
            var nameLabel = new Label(I18n.get().worldBossTimerNameCol()) {{
                FontManager.setNoto(this);
                setPrefWidth(160);
                setAlignment(Pos.CENTER_RIGHT);
                setPadding(new Insets(5, 0, 0, 0));
            }};
            var lastKillLabel = new Label(I18n.get().worldBossTimerLastKillCol()) {{
                FontManager.setNoto(this);
                setPrefWidth(160);
                setAlignment(Pos.CENTER_RIGHT);
                setPadding(new Insets(5, 0, 0, 0));
            }};
            var spawnMinutesLabel = new Label(I18n.get().worldBossTimerSpawnMinutesCol()) {{
                FontManager.setNoto(this);
                setPrefWidth(160);
                setAlignment(Pos.CENTER_RIGHT);
                setPadding(new Insets(5, 0, 0, 0));
            }};
            var commentLabel = new Label(I18n.get().worldBossTimerCommentCol()) {{
                FontManager.setNoto(this);
                setPrefWidth(160);
                setAlignment(Pos.CENTER_RIGHT);
                setPadding(new Insets(5, 0, 0, 0));
            }};
            var lineInput = new TextField() {{
                FontManager.setNoto(this);
                setPrefWidth(250);
                if (oldInfo != null && line != 0) {
                    setText("" + line);
                }
            }};
            var nameInput = new TextField() {{
                FontManager.setNoto(this);
                setPrefWidth(250);
                if (oldInfo != null && name != null) {
                    setText(name);
                } else if (lastBossName != null) {
                    setText(lastBossName);
                }
            }};
            var lastKillInput = new TextField() {{
                FontManager.setNoto(this);
                setPrefWidth(250);
                if (oldInfo != null && lastKill != 0) {
                    setText(ZonedDateTime.ofInstant(Instant.ofEpochMilli(lastKill), ZoneId.systemDefault()).format(formatter));
                } else {
                    setText(current.format(formatter));
                }
            }};
            var spawnMinutesInput = new TextField() {{
                FontManager.setNoto(this);
                setPrefWidth(250);
                if (oldInfo != null && spawnMinutes != 0) {
                    setText("" + spawnMinutes);
                } else {
                    setText("60");
                }
            }};
            var commentInput = new TextField() {{
                FontManager.setNoto(this);
                setPrefWidth(250);
                if (oldInfo != null && comment != null) {
                    setText(comment);
                } else {
                    setText("");
                }
            }};

            var okBtn = new Button(I18n.get().worldBossTimerOkBtn()) {{
                FontManager.setNoto(this);
            }};
            okBtn.setPrefWidth(120);
            okBtn.setOnAction(e -> {
                if (lineInput.getText().isBlank()) {
                    new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerMissingLine()).showAndWait();
                    return;
                }
                if (nameInput.getText().isBlank()) {
                    new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerMissingName()).showAndWait();
                    return;
                }
                if (lastKillInput.getText().isBlank()) {
                    new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerMissingLastKill()).showAndWait();
                    return;
                }
                if (spawnMinutesInput.getText().isBlank()) {
                    new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerMissingSpawnMinutes()).showAndWait();
                    return;
                }
                int vLine;
                try {
                    vLine = Integer.parseInt(lineInput.getText().trim());
                } catch (NumberFormatException ex) {
                    new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerInvalidLine()).showAndWait();
                    return;
                }
                LocalDateTime vLastKill;
                try {
                    vLastKill = LocalDateTime.parse(lastKillInput.getText().trim(), formatter);
                } catch (DateTimeParseException ex) {
                    new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerInvalidLastKill()).showAndWait();
                    return;
                }
                int vSpawnMinutes;
                try {
                    vSpawnMinutes = Integer.parseInt(spawnMinutesInput.getText().trim());
                } catch (NumberFormatException ex) {
                    new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerInvalidSpawnMinutes()).showAndWait();
                    return;
                }

                if (vLine < 1) {
                    new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerInvalidLine()).showAndWait();
                    return;
                }
                if (vSpawnMinutes < 0) {
                    new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerInvalidSpawnMinutes()).showAndWait();
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
                Utils.runDelay(100, () -> table.getItems().add(info));

                close();
            });

            pane.getChildren().add(new VBox(
                new VPadding(10),
                new HBox(
                    new HPadding(10),
                    new VBox(
                        new HBox(lineLabel, new HPadding(10), lineInput),
                        new VPadding(5),
                        new HBox(nameLabel, new HPadding(10), nameInput),
                        new VPadding(5),
                        new HBox(lastKillLabel, new HPadding(10), lastKillInput),
                        new VPadding(5),
                        new HBox(spawnMinutesLabel, new HPadding(10), spawnMinutesInput),
                        new VPadding(5),
                        new HBox(commentLabel, new HPadding(10), commentInput)
                    )
                ),
                new VPadding(10),
                new HBox(new HPadding(165), okBtn)
            ));

            setWidth(450);
            setHeight(290);
            setResizable(false);
            centerOnScreen();
        }

        public AddStage(TableView<BossInfo> table, BossInfo info) {
            this(table, info, info.line, info.name, info.lastKnownKillTs, info.spawnMinutes, info.comment);
        }
    }

    private static class AddAccountStage extends Stage {
        AddAccountStage(TableView<AccountInfo> table) {
            this(table, null, 0, null, 0, 0, null);
        }

        AddAccountStage(TableView<AccountInfo> table,
                        AccountInfo oldInfo,
                        int line, String name, long lastSwitchTs, int cd, String comment) {
            var current = LocalDateTime.now();

            var pane = new Pane();
            var scene = new Scene(pane);
            setScene(scene);

            var lineLabel = new Label(I18n.get().worldBossTimerLineCol()) {{
                FontManager.setNoto(this);
                setPrefWidth(160);
                setAlignment(Pos.CENTER_RIGHT);
                setPadding(new Insets(5, 0, 0, 0));
            }};
            var nameLabel = new Label(I18n.get().worldBossTimerAccountNameCol()) {{
                FontManager.setNoto(this);
                setPrefWidth(160);
                setAlignment(Pos.CENTER_RIGHT);
                setPadding(new Insets(5, 0, 0, 0));
            }};
            var lastSwitchLineTsLabel = new Label(I18n.get().worldBossTimerLastSwitchLineTsCol()) {{
                FontManager.setNoto(this);
                setPrefWidth(160);
                setAlignment(Pos.CENTER_RIGHT);
                setPadding(new Insets(5, 0, 0, 0));
            }};
            var switchLineCDLabel = new Label(I18n.get().worldBossTimerSwitchLineCDMinutes()) {{
                FontManager.setNoto(this);
                setPrefWidth(160);
                setAlignment(Pos.CENTER_RIGHT);
                setPadding(new Insets(5, 0, 0, 0));
            }};
            var commentLabel = new Label(I18n.get().worldBossTimerCommentCol()) {{
                FontManager.setNoto(this);
                setPrefWidth(160);
                setAlignment(Pos.CENTER_RIGHT);
                setPadding(new Insets(5, 0, 0, 0));
            }};
            var lineInput = new TextField() {{
                FontManager.setNoto(this);
                setPrefWidth(250);
                if (oldInfo != null && line != 0) {
                    setText("" + line);
                }
            }};
            var nameInput = new TextField() {{
                FontManager.setNoto(this);
                setPrefWidth(250);
                if (oldInfo != null && name != null) {
                    setText(name);
                }
            }};
            var lastSwitchLineCDInput = new TextField() {{
                FontManager.setNoto(this);
                setPrefWidth(250);
                if (oldInfo != null && lastSwitchTs != 0) {
                    setText(ZonedDateTime.ofInstant(Instant.ofEpochMilli(lastSwitchTs), ZoneId.systemDefault()).format(formatter));
                } else {
                    setText(current.format(formatter));
                }
            }};
            var cdInput = new TextField() {{
                FontManager.setNoto(this);
                setPrefWidth(250);
                if (oldInfo != null && cd != 0) {
                    setText("" + cd);
                } else {
                    setText("30");
                }
            }};
            var commentInput = new TextField() {{
                FontManager.setNoto(this);
                setPrefWidth(250);
                if (oldInfo != null && comment != null) {
                    setText(comment);
                } else {
                    setText("");
                }
            }};

            var okBtn = new Button(I18n.get().worldBossTimerOkBtn()) {{
                FontManager.setNoto(this);
            }};
            okBtn.setPrefWidth(120);
            okBtn.setOnAction(e -> {
                if (lineInput.getText().isBlank()) {
                    new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerMissingLine()).showAndWait();
                    return;
                }
                if (nameInput.getText().isBlank()) {
                    new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerMissingName()).showAndWait();
                    return;
                }
                if (lastSwitchLineCDInput.getText().isBlank()) {
                    new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerMissingLastSwitchLineTs()).showAndWait();
                    return;
                }
                if (cdInput.getText().isBlank()) {
                    new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerMissingSwitchLineCD()).showAndWait();
                    return;
                }
                int vLine;
                try {
                    vLine = Integer.parseInt(lineInput.getText().trim());
                } catch (NumberFormatException ex) {
                    new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerInvalidLine()).showAndWait();
                    return;
                }
                LocalDateTime vLastSwitchLineCD;
                try {
                    vLastSwitchLineCD = LocalDateTime.parse(lastSwitchLineCDInput.getText().trim(), formatter);
                } catch (DateTimeParseException ex) {
                    new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerInvalidLastSwitchLineTs()).showAndWait();
                    return;
                }
                int vCD;
                try {
                    vCD = Integer.parseInt(cdInput.getText().trim());
                } catch (NumberFormatException ex) {
                    new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerInvalidSwitchLineCD()).showAndWait();
                    return;
                }

                if (vLine < 1) {
                    new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerInvalidLine()).showAndWait();
                    return;
                }
                if (vCD < 0) {
                    new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().worldBossTimerInvalidSwitchLineCD()).showAndWait();
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
                Utils.runDelay(100, () -> table.getItems().add(info));

                close();
            });

            pane.getChildren().add(new VBox(
                new VPadding(10),
                new HBox(
                    new HPadding(10),
                    new VBox(
                        new HBox(lineLabel, new HPadding(10), lineInput),
                        new VPadding(5),
                        new HBox(nameLabel, new HPadding(10), nameInput),
                        new VPadding(5),
                        new HBox(lastSwitchLineTsLabel, new HPadding(10), lastSwitchLineCDInput),
                        new VPadding(5),
                        new HBox(switchLineCDLabel, new HPadding(10), cdInput),
                        new VPadding(5),
                        new HBox(commentLabel, new HPadding(10), commentInput)
                    )
                ),
                new VPadding(10),
                new HBox(new HPadding(165), okBtn)
            ));

            setWidth(450);
            setHeight(290);
            setResizable(false);
            centerOnScreen();
        }

        public AddAccountStage(TableView<AccountInfo> table, AccountInfo info) {
            this(table, info, info.lastLine, info.name, info.lastSwitchLineTs, info.switchLineCDMinutes, info.comment);
        }
    }

    public static class BossInfo {
        public int line;
        public String name;
        public long lastKnownKillTs;
        public int spawnMinutes;
        public String comment;

        public final TimerLabel timerLabel = new TimerLabel(Color.BLACK, Color.ORANGE, Color.RED);

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

        public final TimerLabel timerLabel = new TimerLabel(Color.BLACK, Color.ORANGE, Color.GREEN);

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
