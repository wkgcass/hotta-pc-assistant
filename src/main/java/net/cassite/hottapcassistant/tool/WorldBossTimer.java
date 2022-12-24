package net.cassite.hottapcassistant.tool;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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

        S() throws Exception {
            ScrollBar hScrollBar = (ScrollBar) table.lookup(".scroll-bar:horizontal");
            if (hScrollBar != null) {
                hScrollBar.setVisible(false);
            }
            var lineColumn = new TableColumn<BossInfo, Integer>(I18n.get().worldBossTimerLineCol());
            var nameColumn = new TableColumn<BossInfo, String>(I18n.get().worldBossTimerNameCol());
            var lastKillColumn = new TableColumn<BossInfo, String>(I18n.get().worldBossTimerLastKillCol());
            var etaColumn = new TableColumn<BossInfo, String>(I18n.get().worldBossTimerETACol());

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
            etaColumn.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(
                    formatter.format(ZonedDateTime.ofInstant(
                        Instant.ofEpochMilli(param.getValue().lastKnownKillTs)
                            .plusSeconds(param.getValue().spawnMinutes * 60L),
                        ZoneId.systemDefault()))));
            etaColumn.setMinWidth(200);

            //noinspection unchecked
            table.getColumns().addAll(lineColumn, nameColumn, lastKillColumn, etaColumn);

            var addBtn = new Button(I18n.get().worldBossTimerAddBtn()) {{
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

            addBtn.setOnAction(e -> new AddStage(table).showAndWait());
            delBtn.setOnAction(e -> {
                var selected = table.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    return;
                }
                table.getItems().remove(selected);
            });
            clearBtn.setOnAction(e -> table.getItems().clear());

            var pane = new Pane();
            var scene = new Scene(pane);
            setScene(scene);

            pane.getChildren().addAll(
                new VBox(
                    new VPadding(10),
                    new HBox(
                        new HPadding(10),
                        table,
                        new HPadding(10),
                        new VBox(
                            addBtn,
                            new VPadding(5),
                            delBtn,
                            new VPadding(5),
                            clearBtn
                        )
                    )
                )
            );

            pane.widthProperty().addListener((ob, old, now) -> {
                if (now == null) return;
                table.setPrefWidth(now.doubleValue() - 10 - 120 - 10 - 10);
            });
            pane.heightProperty().addListener((ob, old, now) -> {
                if (now == null) return;
                table.setPrefHeight(now.doubleValue() - 10 - 10);
            });

            init();
            table.getItems().addListener((ListChangeListener<BossInfo>) c -> save());

            setWidth(800);
            setHeight(600);
            centerOnScreen();
        }

        private void save() {
            var ls = table.getItems();
            var config = new Config();
            config.list = new ArrayList<>(ls);
            var str = config.toJson().pretty();
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
        }

        private void initList(List<BossInfo> list) {
            if (list == null) return;
            table.setItems(FXCollections.observableList(list));
        }
    }

    private static class Config {
        List<BossInfo> list;

        static final Rule<Config> rule = new ObjectRule<>(Config::new)
            .put("list", (o, it) -> o.list = it,
                new ArrayRule<List<BossInfo>, BossInfo>(ArrayList::new, List::add, BossInfo.rule));

        JSON.Object toJson() {
            var ob = new ObjectBuilder();
            if (list != null) {
                ob.putArray("list", arr -> list.forEach(e -> arr.addInst(e.toJson())));
            }
            return ob.build();
        }
    }

    private static class AddStage extends Stage {
        AddStage(TableView<BossInfo> table) {
            var current = LocalDateTime.now();

            var pane = new Pane();
            var scene = new Scene(pane);
            setScene(scene);

            var lineLabel = new Label(I18n.get().worldBossTimerLineCol()) {{
                FontManager.setNoto(this);
                setPrefWidth(160);
                setAlignment(Pos.CENTER_RIGHT);
            }};
            var nameLabel = new Label(I18n.get().worldBossTimerNameCol()) {{
                FontManager.setNoto(this);
                setPrefWidth(160);
                setAlignment(Pos.CENTER_RIGHT);
            }};
            var lastKillLabel = new Label(I18n.get().worldBossTimerLastKillCol()) {{
                FontManager.setNoto(this);
                setPrefWidth(160);
                setAlignment(Pos.CENTER_RIGHT);
            }};
            var spawnMinutesLabel = new Label(I18n.get().worldBossTimerSpawnMinutesCol()) {{
                FontManager.setNoto(this);
                setPrefWidth(160);
                setAlignment(Pos.CENTER_RIGHT);
            }};
            var lineInput = new TextField() {{
                FontManager.setNoto(this);
                setPrefWidth(250);
            }};
            var nameInput = new TextField() {{
                FontManager.setNoto(this);
                setPrefWidth(250);
            }};
            var lastKillInput = new TextField() {{
                FontManager.setNoto(this);
                setPrefWidth(250);
                setText(current.format(formatter));
            }};
            var spawnMinutesInput = new TextField() {{
                FontManager.setNoto(this);
                setPrefWidth(250);
                setText("60");
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

                var info = new BossInfo();
                info.line = vLine;
                info.name = nameInput.getText();
                info.lastKnownKillTs = ZonedDateTime.of(vLastKill, ZoneId.systemDefault()).toEpochSecond() * 1000;
                info.spawnMinutes = vSpawnMinutes;

                var opt = table.getItems().stream().filter(i -> i.line == info.line && i.name.equals(info.name)).findAny();
                //noinspection OptionalIsPresent
                if (opt.isPresent()) {
                    table.getItems().remove(opt.get());
                }
                table.getItems().add(info);

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
                        new HBox(spawnMinutesLabel, new HPadding(10), spawnMinutesInput)
                    )
                ),
                new VPadding(10),
                new HBox(new HPadding(175), okBtn)
            ));

            setWidth(450);
            setHeight(240);
            setResizable(false);
            centerOnScreen();
        }
    }

    private static class BossInfo {
        int line;
        String name;
        long lastKnownKillTs;
        int spawnMinutes;

        static final Rule<BossInfo> rule = new ObjectRule<>(BossInfo::new)
            .put("line", (o, it) -> o.line = it, IntRule.get())
            .put("name", (o, it) -> o.name = it, StringRule.get())
            .put("lastKnownKillTs", (o, it) -> o.lastKnownKillTs = it, LongRule.get())
            .put("spawnMinutes", (o, it) -> o.spawnMinutes = it, IntRule.get());

        JSON.Object toJson() {
            return new ObjectBuilder()
                .put("line", line)
                .put("name", name)
                .put("lastKnownKillTs", lastKnownKillTs)
                .put("spawnMinutes", spawnMinutes)
                .build();
        }
    }
}
