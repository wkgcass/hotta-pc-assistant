package net.cassite.hottapcassistant.ui;

import io.vproxy.vfx.entity.Rect;
import io.vproxy.vfx.entity.Point;
import io.vproxy.vfx.entity.input.InputData;
import io.vproxy.vfx.entity.input.Key;
import io.vproxy.vfx.entity.input.KeyCode;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.manager.task.TaskManager;
import io.vproxy.vfx.ui.alert.SimpleAlert;
import io.vproxy.vfx.ui.alert.StackTraceAlert;
import io.vproxy.vfx.ui.layout.HPadding;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.shapes.MovableRect;
import io.vproxy.vfx.util.FXUtils;
import io.vproxy.vfx.util.Logger;
import io.vproxy.vfx.util.MiscUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import net.cassite.hottapcassistant.config.AssistantConfig;
import net.cassite.hottapcassistant.config.InputConfig;
import net.cassite.hottapcassistant.data.Matrix;
import net.cassite.hottapcassistant.data.Relics;
import net.cassite.hottapcassistant.data.Simulacra;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.simulacra.DummySimulacra;
import net.cassite.hottapcassistant.discharge.DischargeCheckAlgorithm;
import net.cassite.hottapcassistant.discharge.DischargeCheckContext;
import net.cassite.hottapcassistant.discharge.SimpleDischargeCheckAlgorithm;
import net.cassite.hottapcassistant.entity.*;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Consts;
import net.cassite.hottapcassistant.util.GlobalValues;
import net.cassite.hottapcassistant.util.Utils;

import java.awt.*;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CoolDownPane extends StackPane implements EnterCheck, Terminate {
    private static final int WIDTH_HEIGHT = 110;

    private InputData weaponSkill;
    private InputData[] melee;
    private InputData[] evade;
    private final InputData[] changeWeapons = new InputData[3];
    private final InputData[] useArtifacts = new InputData[2];
    private InputData jump;
    @SuppressWarnings({"unchecked", "rawtypes"})
    private final SimpleObjectProperty<WeaponRef>[] weapons = new SimpleObjectProperty[]{
        new SimpleObjectProperty(null),
        new SimpleObjectProperty(null),
        new SimpleObjectProperty(null),
    };
    @SuppressWarnings({"unchecked", "rawtypes"})
    private final SimpleObjectProperty<MatrixRef>[][] matrixs = new SimpleObjectProperty[][]{
        {
            new SimpleObjectProperty(null),
            new SimpleObjectProperty(null),
            new SimpleObjectProperty(null),
            new SimpleObjectProperty(null),
        },
        {
            new SimpleObjectProperty(null),
            new SimpleObjectProperty(null),
            new SimpleObjectProperty(null),
            new SimpleObjectProperty(null),
        },
        {
            new SimpleObjectProperty(null),
            new SimpleObjectProperty(null),
            new SimpleObjectProperty(null),
            new SimpleObjectProperty(null),
        },
    };
    @SuppressWarnings({"unchecked", "rawtypes"})
    private final SimpleObjectProperty<RelicsRef>[] relics = new SimpleObjectProperty[]{
        new SimpleObjectProperty(),
        new SimpleObjectProperty(),
    };
    private final SimpleObjectProperty<SimulacraRef> simulacra = new SimpleObjectProperty<>(null);
    private final Set<String> row2Ids = new HashSet<>();
    private final ObservableList<String> configurationNames = FXCollections.observableList(new ArrayList<>());
    private final ObservableList<AssistantCoolDownConfiguration> configurations = FXCollections.observableList(new ArrayList<>());
    private final SimpleObjectProperty<AssistantCoolDownOptions> options = new SimpleObjectProperty<>(AssistantCoolDownOptions.empty());
    private final CoolDownOptions optionsStage = new CoolDownOptions();

    public CoolDownPane() {
        var vbox = new VBox();
        getChildren().add(vbox);

        // weapons
        {
            for (int vwi = 0; vwi < weapons.length; ++vwi) {
                final int wi = vwi;
                if (wi != 0) {
                    var sep = new Separator();
                    sep.setPadding(new Insets(5, 0, 2, 0));
                    vbox.getChildren().add(sep);
                }
                var hbox = new HBox();
                vbox.getChildren().add(hbox);

                {
                    var weaponVBox = new VBox();
                    var image = new ImageView();
                    var weaponSelect = new ComboBox<WeaponRef>();
                    var starsSelect = new ComboBox<Integer>();
                    weaponVBox.getChildren().addAll(image, weaponSelect, starsSelect);
                    hbox.getChildren().add(weaponVBox);

                    weapons[wi].addListener((ob, old, now) -> {
                        if (now == null) return;
                        var img = now.image;
                        if (img != null) image.setImage(img);
                        for (var w : weaponSelect.getItems()) {
                            if (w.id == now.id) {
                                if (w != now) {
                                    weaponSelect.setValue(w);
                                    starsSelect.setValue(now.getStars());
                                    weapons[wi].set(w);
                                }
                                break;
                            }
                        }
                    });
                    weaponSelect.setConverter(new StringConverter<>() {
                        @Override
                        public String toString(WeaponRef object) {
                            if (object == null) return "";
                            return object.name;
                        }

                        @Override
                        public WeaponRef fromString(String string) {
                            throw new UnsupportedOperationException();
                        }
                    });
                    weaponSelect.setEditable(false);
                    weaponSelect.setItems(FXCollections.observableList(WeaponRef.all()));
                    weaponSelect.getItems().forEach(e -> e.starsSupplier = starsSelect::getValue);
                    weaponSelect.setOnAction(e -> {
                        var selected = weaponSelect.getValue();
                        if (selected == null) return;
                        weapons[wi].set(selected);
                    });
                    weaponSelect.setPrefWidth(WIDTH_HEIGHT + 30);
                    image.setFitWidth(WIDTH_HEIGHT + 30);
                    image.setFitHeight(WIDTH_HEIGHT + 30);
                    starsSelect.setEditable(false);
                    starsSelect.setItems(FXCollections.observableList(Arrays.asList(0, 1, 2, 3, 4, 5, 6)));
                    starsSelect.getSelectionModel().select(6);
                    starsSelect.setPrefWidth(WIDTH_HEIGHT + 30);
                }

                {
                    var sep = new Separator(Orientation.VERTICAL);
                    sep.setPadding(new Insets(0, 10, 0, 10));
                    hbox.getChildren().add(sep);
                }

                for (int vmi = 0; vmi < matrixs[wi].length; ++vmi) {
                    if (vmi != 0) {
                        hbox.getChildren().add(new HPadding(5));
                    }
                    final int mi = vmi;

                    var matrixVBox = new VBox();
                    var image = new ImageView();
                    var matrixSelect = new ComboBox<MatrixRef>();
                    var starsSelect = new ComboBox<Integer>();
                    matrixVBox.getChildren().addAll(new VPadding(30), image, matrixSelect, starsSelect);
                    hbox.getChildren().add(matrixVBox);

                    matrixs[wi][mi].addListener((ob, old, now) -> {
                        if (now == null) return;
                        var img = now.image;
                        if (img != null) image.setImage(img);
                        for (var m : matrixSelect.getItems()) {
                            if (m.id == now.id) {
                                if (m != now) {
                                    matrixSelect.setValue(m);
                                    starsSelect.setValue(now.getStars());
                                    matrixs[wi][mi].set(m);
                                }
                                break;
                            }
                        }
                    });
                    matrixSelect.setConverter(new StringConverter<>() {
                        @Override
                        public String toString(MatrixRef object) {
                            if (object == null) return "";
                            return object.name;
                        }

                        @Override
                        public MatrixRef fromString(String string) {
                            throw new UnsupportedOperationException();
                        }
                    });
                    matrixSelect.setEditable(false);
                    matrixSelect.setItems(FXCollections.observableList(MatrixRef.all()));
                    matrixSelect.getItems().forEach(e -> e.starsSupplier = starsSelect::getValue);
                    matrixSelect.setOnAction(e -> {
                        var selected = matrixSelect.getValue();
                        if (selected == null) return;
                        matrixs[wi][mi].set(selected);
                    });
                    matrixSelect.setPrefWidth(WIDTH_HEIGHT);
                    image.setFitWidth(WIDTH_HEIGHT);
                    image.setFitHeight(WIDTH_HEIGHT);
                    starsSelect.setEditable(false);
                    starsSelect.setItems(FXCollections.observableList(Arrays.asList(0, 1, 2, 3)));
                    starsSelect.getSelectionModel().select(3);
                    starsSelect.setPrefWidth(WIDTH_HEIGHT);
                }
            }
        }

        {
            var sep = new Separator();
            sep.setPadding(new Insets(10, 0, 10, 0));
            vbox.getChildren().add(sep);
        }

        var buttonsAndRelicsSimulacraHBox = new HBox();
        vbox.getChildren().add(buttonsAndRelicsSimulacraHBox);

        var buttonsVBox = new VBox();
        buttonsAndRelicsSimulacraHBox.getChildren().add(buttonsVBox);
        buttonsVBox.setMaxWidth(700);

        {
            var hbox = new HBox();
            buttonsVBox.getChildren().add(hbox);

            var label = new Label(I18n.get().cooldownConfigurationLabel()) {{
                FontManager.get().setFont(this, 14);
            }};
            label.setPadding(new Insets(3, 0, 0, 0));

            configurations.addListener((ListChangeListener<AssistantCoolDownConfiguration>) c ->
                configurationNames.setAll(configurations.stream().map(e -> e.name).collect(Collectors.toList())));

            var equipConfig = new ComboBox<String>();
            equipConfig.setEditable(true);
            equipConfig.setItems(configurationNames);
            equipConfig.setOnAction(e -> {
                var selected = equipConfig.getValue();
                if (selected == null) return;
                var opt = configurations.stream().filter(ee -> selected.equals(ee.name)).findAny();
                opt.ifPresent(this::loadFromConfig);
            });
            equipConfig.setPrefWidth(WIDTH_HEIGHT);
            var saveButton = new Button(I18n.get().cooldownConfigurationSave()) {{
                FontManager.get().setFont(this, 13);
            }};
            saveButton.setOnAction(e -> {
                var name = equipConfig.getValue();
                if (name == null || name.isBlank()) return;
                if (configurationNames.contains(name)) {
                    SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().cooldownConfigurationDuplicate());
                    return;
                }
                newConfiguration(name);
            });
            var deleteButton = new Button(I18n.get().cooldownConfigurationDelete()) {{
                FontManager.get().setFont(this, 13);
            }};
            deleteButton.setOnAction(e -> {
                var name = equipConfig.getValue();
                if (name == null || name.isBlank()) return;
                if (!configurationNames.contains(name)) {
                    return;
                }
                configurations.removeIf(ee -> name.equals(ee.name));
                saveConfig();
                equipConfig.setValue(null);
            });
            hbox.getChildren().addAll(label, new HPadding(5), equipConfig, new HPadding(5), saveButton, new HPadding(5), deleteButton);
        }

        {
            var sep = new Separator();
            sep.setPadding(new Insets(10, 0, 10, 0));
            buttonsVBox.getChildren().add(sep);
        }

        // buttons
        {
            var hbox = new HBox();
            buttonsVBox.getChildren().add(hbox);

            var startBtn = new Button(I18n.get().startCoolDown()) {{
                FontManager.get().setFont(this);
            }};
            startBtn.setPrefWidth(WIDTH_HEIGHT);
            startBtn.setOnAction(e -> start());

            var stopBtn = new Button(I18n.get().stopCoolDown()) {{
                FontManager.get().setFont(this);
            }};
            stopBtn.setPrefWidth(WIDTH_HEIGHT);
            stopBtn.setDisable(true);
            stopBtn.setOnAction(e -> stop());

            isStarted.addListener((ob, old, now) -> {
                if (now == null) return;
                startBtn.setDisable(now);
                stopBtn.setDisable(!now);
            });

            hbox.getChildren().addAll(startBtn, new HPadding(4), stopBtn);
        }

        {
            buttonsVBox.getChildren().add(new VPadding(4));
            var hbox = new HBox();
            buttonsVBox.getChildren().add(hbox);

            var optionsBtn = new Button(I18n.get().cooldownOptionsBtn()) {{
                FontManager.get().setFont(this);
            }};
            optionsBtn.setPrefWidth(WIDTH_HEIGHT);
            optionsBtn.setOnAction(e -> {
                if (optionsStage.isShowing()) {
                    optionsStage.requestFocus();
                } else {
                    optionsStage.show();
                }
            });

            var tipsBtn = new Button(I18n.get().cooldownTipsButton()) {{
                FontManager.get().setFont(this);
            }};
            tipsBtn.setPrefWidth(WIDTH_HEIGHT);
            tipsBtn.setOnAction(e -> new CoolDownTips().show());

            hbox.getChildren().addAll(optionsBtn, new HPadding(4), tipsBtn);
        }

        {
            buttonsAndRelicsSimulacraHBox.getChildren().add(new HPadding(20));
        }

        {
            //noinspection unchecked,rawtypes
            ComboBox<RelicsRef>[] relics = new ComboBox[]{
                new ComboBox(),
                new ComboBox(),
            };
            //noinspection unchecked,rawtypes
            ComboBox<Integer>[] relicsStars = new ComboBox[]{
                new ComboBox(),
                new ComboBox(),
            };

            for (var i = 0; i < 2; ++i) {
                final var ii = i;
                var relics0 = relics[ii];
                var relicsStars0 = relicsStars[ii];
                relics0.setConverter(new StringConverter<>() {
                    @Override
                    public String toString(RelicsRef object) {
                        if (object == null)
                            return I18n.get().relicsChooserPlaceHolder(ii);
                        return object.name;
                    }

                    @Override
                    public RelicsRef fromString(String string) {
                        throw new UnsupportedOperationException();
                    }
                });
                relics0.setItems(FXCollections.observableList(RelicsRef.all()));
                relics0.getItems().forEach(e -> e.starsSupplier = relicsStars0::getValue);
                relics0.setOnAction(e -> this.relics[ii].set(relics0.getValue()));
                this.relics[ii].addListener((ob, old, now) -> {
                    if (now == null) return;
                    for (var r : relics0.getItems()) {
                        if (r.id == now.id && r != now) {
                            relics0.setValue(r);
                            relicsStars0.setValue(now.getStars());
                            this.relics[ii].setValue(r);
                        }
                    }
                });
                relics0.setPrefWidth(WIDTH_HEIGHT);
                relicsStars0.setEditable(false);
                relicsStars0.setItems(FXCollections.observableList(Arrays.asList(0, 1, 2, 3, 4, 5)));
                relicsStars0.getSelectionModel().select(3);
                relicsStars0.setPrefWidth(WIDTH_HEIGHT);
            }

            ComboBox<SimulacraRef> simulacraRefComboBox;
            {
                simulacraRefComboBox = new ComboBox<>();
                simulacraRefComboBox.setPrefWidth(WIDTH_HEIGHT);
                simulacraRefComboBox.setEditable(false);
                simulacraRefComboBox.setItems(FXCollections.observableList(SimulacraRef.all()));
                simulacraRefComboBox.getSelectionModel().select(0);
                simulacraRefComboBox.setOnAction(e -> simulacra.set(simulacraRefComboBox.getValue()));
                simulacraRefComboBox.setConverter(new StringConverter<>() {
                    @Override
                    public String toString(SimulacraRef object) {
                        if (object == null) return "";
                        return object.name;
                    }

                    @Override
                    public SimulacraRef fromString(String string) {
                        throw new UnsupportedOperationException();
                    }
                });
                simulacra.addListener((ob, old, now) -> {
                    if (now == null) return;
                    for (var s : simulacraRefComboBox.getItems()) {
                        if (s.id == now.id) {
                            if (s != now) {
                                simulacraRefComboBox.setValue(s);
                                simulacra.set(s);
                            }
                            break;
                        }
                    }
                });
            }

            var hbox = new HBox();
            buttonsAndRelicsSimulacraHBox.getChildren().add(hbox);
            hbox.getChildren().addAll(new VBox() {{
                getChildren().add(relics[0]);
                getChildren().add(relicsStars[0]);
            }}, new HPadding(4), new VBox() {{
                getChildren().add(relics[1]);
                getChildren().add(relicsStars[1]);
            }}, new HPadding(20), simulacraRefComboBox);
        }
    }

    private final SimpleBooleanProperty isStarted = new SimpleBooleanProperty(false);
    private CoolDownWindow window = null;

    private void start() {
        if (isStarted.get()) return;

        if (optionsStage.isShowing()) {
            optionsStage.close();
        }

        var set = new HashSet<Integer>();
        for (var weapon : weapons) {
            if (weapon.get() == null) {
                SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().weaponNotSelected());
                return;
            }
            set.add(weapon.get().id);
        }
        if (set.size() != 3 || (set.contains(16) && set.contains(17))) {
            SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().duplicatedWeapon());
            return;
        }
        set.clear();
        Relics[] relics = new Relics[]{null, null};
        for (int i = 0; i < this.relics.length; i++) {
            var r = this.relics[i];
            var rr = r.get();
            if (rr != null) {
                if (!set.add(rr.id) && rr.id != 0) {
                    SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().duplicatedRelics());
                    return;
                }
                relics[i] = rr.make();
            }
        }
        Simulacra simulacra;
        {
            var s = this.simulacra.get();
            if (s != null) {
                simulacra = s.make();
            } else {
                simulacra = new DummySimulacra();
            }
        }

        isStarted.set(true);

        var weapons = new ArrayList<Weapon>(3);
        for (var i = 0; i < 3; ++i) {
            var arr = matrixs[i];
            var map = new HashMap<Integer, List<MatrixRef>>();
            for (var e : arr) {
                var ref = e.get();
                if (ref == null) continue;
                List<MatrixRef> ls;
                if (map.containsKey(ref.id)) {
                    ls = map.get(ref.id);
                } else {
                    ls = new ArrayList<>();
                    map.put(ref.id, ls);
                }
                ls.add(ref);
            }
            var matrixs = new Matrix[map.size()];
            int index = 0;
            for (var ls : map.values()) {
                var m = MatrixRef.make(ls);
                matrixs[index++] = m;
            }
            var w = this.weapons[i].get().make(matrixs);
            w.init(options.get());
            weapons.add(w);
        }

        saveConfig();

        var window = new CoolDownWindow(weapons, relics, simulacra, weaponSkill, melee, evade, changeWeapons, useArtifacts, jump,
            row2Ids, options.get(),
            this::reset);
        this.window = window;
        setWindowPosition(window);
        window.show();
        FXUtils.iconifyWindow(getScene().getWindow());
    }

    private void saveConfig() {
        var config = buildConfig();
        try {
            AssistantConfig.updateAssistant(ass -> ass.cooldown = config);
        } catch (Exception e) {
            Logger.error("updating assistant config for cooldown failed", e);
        }
    }

    private void newConfiguration(String name) {
        var cd = buildConfig();
        var ret = new AssistantCoolDownConfiguration();
        ret.name = name;
        ret.weapons = cd.weapons;
        ret.relics = cd.relics;
        ret.simulacra = cd.simulacra;

        configurations.add(ret);
        saveConfig();
    }

    private AssistantCoolDown buildConfig() {
        var config = new AssistantCoolDown();
        config.weapons = new ArrayList<>(this.weapons.length);
        var thisWeapons = this.weapons;
        for (int i = 0, simpleObjectPropertiesLength = thisWeapons.length; i < simpleObjectPropertiesLength; i++) {
            var wp = thisWeapons[i];
            var w = wp.get();
            var wConfig = new AssistantCoolDownWeapon();
            wConfig.weaponId = w == null ? 0 : w.id;
            wConfig.stars = w == null ? 6 : w.getStars();
            wConfig.matrix = new ArrayList<>(this.matrixs[i].length);
            for (var mp : this.matrixs[i]) {
                var m = mp.get();
                var mConfig = new AssistantCoolDownWeaponMatrix();
                mConfig.matrixId = m == null ? 0 : m.id;
                mConfig.stars = m == null ? 3 : m.getStars();
                wConfig.matrix.add(mConfig);
            }
            config.weapons.add(wConfig);
        }
        config.relics = new ArrayList<>(this.relics.length);
        for (var rp : this.relics) {
            var r = rp.get();
            var rConfig = new AssistantCoolDownRelics();
            rConfig.relicsId = r == null ? 0 : r.id;
            rConfig.stars = r == null ? 3 : r.getStars();
            config.relics.add(rConfig);
        }
        config.simulacra = new AssistantCoolDownSimulacra();
        {
            var s = this.simulacra.get();
            config.simulacra.simulacraId = s == null ? 0 : s.id;
        }
        config.row2Ids = row2Ids;
        config.configurations = new ArrayList<>(configurations);
        config.options = this.options.get();
        Logger.info("weapon properties:\n" + config.toJson().stringify());
        return config;
    }

    private double windowPositionX;
    private double windowPositionY;
    private double windowScale = 1;

    private void setWindowPosition(CoolDownWindow window) {
        if (windowPositionX != 0 && windowPositionY != 0) {
            window.setX(windowPositionX);
            window.setY(windowPositionY);
        }
        if (windowScale != 1) {
            window.setScale(windowScale);
        }
    }

    private void stop() {
        if (!isStarted.get()) return;
        isStarted.set(false);

        var window = this.window;
        this.window = null;
        if (window != null) {
            windowPositionX = window.getX();
            windowPositionY = window.getY();
            windowScale = window.getScale();
            options.get().lastWindowScale = windowScale;
            handleBuffsAndSaveConfig(window);
            window.close();
        }
    }

    private void handleBuffsAndSaveConfig(CoolDownWindow window) {
        var buffs = window.getBuffs();
        var row2 = window.getRow2();
        var toRemove = new HashSet<String>();
        for (var s : row2Ids) {
            if (!row2.contains(s)) {
                if (buffs.contains(s)) {
                    toRemove.add(s);
                }
            }
        }
        row2Ids.removeAll(toRemove);
        row2Ids.addAll(row2);
        saveConfig();
    }

    private void reset() {
        stop();
        start();
    }

    @SuppressWarnings({"rawtypes"})
    private final ChangeListener inputConfigChangeListener = (ob, old, now) -> readConfig(false);

    @SuppressWarnings({"unchecked"})
    @Override
    public boolean enterCheck(boolean skipGamePathCheck) {
        if (!skipGamePathCheck) {
            if (!GlobalValues.checkGamePath()) return false;
        }
        GlobalValues.useVersion.removeListener(inputConfigChangeListener);
        GlobalValues.savedPath.removeListener(inputConfigChangeListener);
        GlobalValues.useVersion.addListener(inputConfigChangeListener);
        GlobalValues.savedPath.addListener(inputConfigChangeListener);
        return readConfig(skipGamePathCheck);
    }

    private boolean readConfig(boolean skipGamePath) {
        Assistant ass;
        try {
            ass = AssistantConfig.readAssistant(true);
        } catch (Exception e) {
            StackTraceAlert.show(I18n.get().readAssistantConfigFailed(), e);
            return false;
        }
        loadFromConfig(ass.cooldown);

        List<KeyBinding> input;
        if (GlobalValues.savedPath.get() != null) {
            var config = InputConfig.ofSaved(Path.of(GlobalValues.savedPath.get(), "Config", "WindowsNoEditor", "Input.ini").toString());
            try {
                input = config.read();
            } catch (Exception e) {
                if (skipGamePath) {
                    Logger.error("failed reading input config", e);
                    input = Collections.emptyList();
                } else {
                    StackTraceAlert.show(I18n.get().readInputConfigFailed(), e);
                    return false;
                }
            }
        } else {
            input = Collections.emptyList();
        }

        InputData weaponSkill = null;
        InputData melee = null;
        InputData meleeKey = null;
        InputData evade = null;
        InputData evadeKey = null;
        InputData changeWeapon0 = null;
        InputData changeWeapon1 = null;
        InputData changeWeapon2 = null;
        InputData artifact1 = null;
        InputData artifact2 = null;
        InputData jump = null;
        for (var kb : input) {
            if ("WeaponSkill".equals(kb.action)) {
                weaponSkill = kb;
            } else if ("ChangeWeapon0".equals(kb.action)) {
                changeWeapon0 = kb;
            } else if ("ChangeWeapon1".equals(kb.action)) {
                changeWeapon1 = kb;
            } else if ("ChangeWeapon2".equals(kb.action)) {
                changeWeapon2 = kb;
            } else if ("Artifact_1".equals(kb.action)) {
                artifact1 = kb;
            } else if ("Artifact_2".equals(kb.action)) {
                artifact2 = kb;
            } else if ("Melee".equals(kb.action)) {
                melee = kb;
            } else if ("Melee_Key".equals(kb.action)) {
                meleeKey = kb;
            } else if ("Evade".equals(kb.action)) {
                evade = kb;
            } else if ("Evade_Key".equals(kb.action)) {
                evadeKey = kb;
            } else if ("Jump".equals(kb.action)) {
                jump = kb;
            }
        }
        if (weaponSkill == null) {
            weaponSkill = new InputData(new Key(KeyCode.KEY_1));
        }
        if (changeWeapon0 == null) {
            changeWeapon0 = new InputData(new Key(KeyCode.Q));
        }
        if (changeWeapon1 == null) {
            changeWeapon1 = new InputData(new Key(KeyCode.E));
        }
        if (changeWeapon2 == null) {
            changeWeapon2 = new InputData(new Key(KeyCode.R));
        }
        if (artifact1 == null) {
            artifact1 = new InputData(new Key(KeyCode.KEY_2));
        }
        if (artifact2 == null) {
            artifact2 = new InputData(new Key(KeyCode.KEY_3));
        }
        if (melee == null) {
            melee = new InputData(new Key(MouseButton.PRIMARY));
        }
        if (evade == null) {
            evade = new InputData(new Key(MouseButton.SECONDARY));
        }
        if (evadeKey == null) {
            evadeKey = new InputData(new Key(KeyCode.SHIFT, true));
        }
        if (jump == null) {
            jump = new InputData(new Key(KeyCode.SPACE));
        }

        this.weaponSkill = weaponSkill;
        if (meleeKey == null) {
            this.melee = new InputData[]{melee};
        } else {
            this.melee = new InputData[]{melee, meleeKey};
        }
        this.evade = new InputData[]{evade, evadeKey};
        this.changeWeapons[0] = changeWeapon0;
        this.changeWeapons[1] = changeWeapon1;
        this.changeWeapons[2] = changeWeapon2;
        this.useArtifacts[0] = artifact1;
        this.useArtifacts[1] = artifact2;
        this.jump = jump;

        return true;
    }

    private void loadFromConfig(AssistantCoolDown cooldown) {
        if (cooldown == null) return;
        loadWeaponsFromConfig(cooldown.weapons);
        loadRelicsFromConfig(cooldown.relics);
        loadSimulacraFromConfig(cooldown.simulacra);
        loadRow2Ids(cooldown.row2Ids);
        loadConfigurations(cooldown.configurations);
        loadOptions(cooldown.options);
    }

    private void loadFromConfig(AssistantCoolDownConfiguration cooldown) {
        if (cooldown == null) return;
        loadWeaponsFromConfig(cooldown.weapons);
        loadRelicsFromConfig(cooldown.relics);
        loadSimulacraFromConfig(cooldown.simulacra);
    }

    private void loadWeaponsFromConfig(List<AssistantCoolDownWeapon> weapons) {
        if (weapons == null) return;
        for (int i = 0; i < weapons.size(); ++i) {
            var configW = weapons.get(i);
            var w = new WeaponRef(configW.weaponId, null);
            w.starsSupplier = () -> configW.stars;
            this.weapons[i].set(w);

            if (configW.matrix == null) continue;
            for (int j = 0; j < configW.matrix.size(); ++j) {
                var configM = configW.matrix.get(j);
                var m = new MatrixRef(configM.matrixId, null);
                m.starsSupplier = () -> configM.stars;
                matrixs[i][j].set(m);
            }
        }
    }

    private void loadRelicsFromConfig(List<AssistantCoolDownRelics> relics) {
        if (relics == null) return;
        for (int i = 0; i < relics.size(); ++i) {
            var rr = relics.get(i);
            var r = new RelicsRef(rr.relicsId, null);
            r.starsSupplier = () -> rr.stars;
            this.relics[i].set(r);
        }
    }

    private void loadSimulacraFromConfig(AssistantCoolDownSimulacra simulacra) {
        if (simulacra == null) return;
        var s = new SimulacraRef(simulacra.simulacraId, null);
        this.simulacra.set(s);
    }

    private void loadRow2Ids(Set<String> row2Ids) {
        if (row2Ids == null) return;
        this.row2Ids.clear();
        this.row2Ids.addAll(row2Ids);
    }

    private void loadConfigurations(List<AssistantCoolDownConfiguration> configurations) {
        if (configurations == null) return;
        this.configurations.clear();
        this.configurations.addAll(configurations);
    }

    private void loadOptions(AssistantCoolDownOptions options) {
        if (options == null) return;
        this.options.set(options);
        if (options.lastWindowScale >= 0.2 && options.lastWindowScale <= 3) {
            windowScale = options.lastWindowScale;
        }
    }

    @Override
    public void terminate() {
        stop();
    }

    private class CoolDownOptions extends Stage {
        CoolDownOptions() {
            var vbox = new VBox();
            vbox.setPadding(new Insets(10, 10, 10, 10));
            var scene = new Scene(vbox);
            setScene(scene);

            {
                var scanDischargeDesc = new Label(I18n.get().cooldownScanDischargeDesc()) {{
                    FontManager.get().setFont(Consts.NotoFont, this);
                }};
                vbox.getChildren().add(scanDischargeDesc);
                vbox.getChildren().add(new VPadding(4));
                var scanDischargeCheckbox = new CheckBox(I18n.get().cooldownScanDischargeCheckBox()) {{
                    FontManager.get().setFont(this);
                }};
                vbox.getChildren().add(scanDischargeCheckbox);
                vbox.getChildren().add(new VPadding(4));
                var scanDischargeDebugCheckbox = new CheckBox(I18n.get().cooldownScanDischargeDebugCheckBox()) {{
                    FontManager.get().setFont(this);
                }};
                vbox.getChildren().add(scanDischargeDebugCheckbox);
                vbox.getChildren().add(new VPadding(4));
                var scanDischargeUseNativeCaptureCheckbox = new CheckBox(I18n.get().cooldownScanDischargeUseNativeCaptureCheckBox()) {{
                    FontManager.get().setFont(this);
                }};
                vbox.getChildren().add(scanDischargeUseNativeCaptureCheckbox);
                vbox.getChildren().add(new VPadding(4));
                var scanDischargeUseRoughCaptureCheckbox = new CheckBox(I18n.get().cooldownScanDischargeUseRoughCaptureCheckBox()) {{
                    FontManager.get().setFont(this);
                }};
                vbox.getChildren().add(scanDischargeUseRoughCaptureCheckbox);
                vbox.getChildren().add(new VPadding(4));
                var scanDischargeResetConfigBtn = new Button(I18n.get().cooldownScanDischargeResetBtn()) {{
                    FontManager.get().setFont(this);
                }};
                vbox.getChildren().add(scanDischargeResetConfigBtn);
                vbox.getChildren().add(new Separator() {{
                    setPadding(new Insets(5, 0, 5, 0));
                }});
                vbox.getChildren().add(new Separator() {{
                    setPadding(new Insets(5, 0, 5, 0));
                }});
                var hideWhenMouseEnterCheckBox = new CheckBox(I18n.get().hideWhenMouseEnterCheckBox()) {{
                    FontManager.get().setFont(this);
                }};
                vbox.getChildren().add(hideWhenMouseEnterCheckBox);
                vbox.getChildren().add(new VPadding(4));
                var lockCDWindowPositionCheckBox = new CheckBox(I18n.get().lockCDWindowPositionCheckBox()) {{
                    FontManager.get().setFont(this);
                }};
                vbox.getChildren().add(lockCDWindowPositionCheckBox);
                vbox.getChildren().add(new VPadding(4));
                var onlyShowFirstLineBuffCheckBox = new CheckBox(I18n.get().onlyShowFirstLineBuffCheckBox()) {{
                    FontManager.get().setFont(this);
                }};
                vbox.getChildren().add(onlyShowFirstLineBuffCheckBox);
                vbox.getChildren().add(new Separator() {{
                    setPadding(new Insets(5, 0, 5, 0));
                }});
                var playAudioCheckBox = new CheckBox(I18n.get().cooldownPlayAudioCheckBox()) {{
                    FontManager.get().setFont(this);
                }};
                vbox.getChildren().add(playAudioCheckBox);
                vbox.getChildren().add(new Separator() {{
                    setPadding(new Insets(5, 0, 5, 0));
                }});
                var applyDischargeForYingZhiCheckBox = new CheckBox(I18n.get().cooldownApplyDischargeForYingZhiCheckBox()) {{
                    FontManager.get().setFont(this);
                }};
                vbox.getChildren().add(applyDischargeForYingZhiCheckBox);
                var autoFillPianGuangLingYuSubSkillCheckbox = new CheckBox(I18n.get().cooldownAutoFillPianGuangLingYuSubSkillCheckbox()) {{
                    FontManager.get().setFont(this);
                }};
                vbox.getChildren().add(autoFillPianGuangLingYuSubSkillCheckbox);

                options.addListener((ob, old, now) -> {
                    if (now == null) return;
                    if (now.scanDischargeEnabled()) {
                        scanDischargeCheckbox.setSelected(true);
                    }
                    scanDischargeDebugCheckbox.setSelected(now.scanDischargeDebug);
                    scanDischargeUseNativeCaptureCheckbox.setSelected(now.scanDischargeNativeCapture);
                    scanDischargeUseRoughCaptureCheckbox.setSelected(now.scanDischargeRoughCapture);
                    hideWhenMouseEnterCheckBox.setSelected(now.hideWhenMouseEnter);
                    lockCDWindowPositionCheckBox.setSelected(now.lockCDWindowPosition);
                    onlyShowFirstLineBuffCheckBox.setSelected(now.onlyShowFirstLineBuff);
                    playAudioCheckBox.setSelected(now.playAudio);
                    applyDischargeForYingZhiCheckBox.setSelected(now.applyDischargeForYingZhi);
                    autoFillPianGuangLingYuSubSkillCheckbox.setSelected(now.autoFillPianGuangLingYuSubSkill);
                });
                scanDischargeCheckbox.setOnAction(e -> {
                    var selected = scanDischargeCheckbox.isSelected();
                    var opt = options.get();
                    if (selected) {
                        if (opt.canEnableDischarge()) {
                            opt.scanDischarge = true;
                            saveConfig();
                            return;
                        }
                        chooseScanDischargeRectStep1(ok -> {
                            if (ok) {
                                opt.scanDischarge = true;
                                saveConfig();
                            } else {
                                scanDischargeCheckbox.setSelected(false);
                            }
                        });
                    } else {
                        opt.scanDischarge = false;
                        saveConfig();
                    }
                });
                scanDischargeDebugCheckbox.setOnAction(e -> {
                    var opt = options.get();
                    opt.scanDischargeDebug = scanDischargeDebugCheckbox.isSelected();
                    saveConfig();
                });
                scanDischargeUseNativeCaptureCheckbox.setOnAction(e -> {
                    var opt = options.get();
                    opt.scanDischargeNativeCapture = scanDischargeUseNativeCaptureCheckbox.isSelected();
                    if (scanDischargeUseNativeCaptureCheckbox.isSelected()) {
                        scanDischargeUseRoughCaptureCheckbox.setSelected(false);
                    }
                    saveConfig();
                });
                scanDischargeUseRoughCaptureCheckbox.setOnAction(e -> {
                    var opt = options.get();
                    opt.scanDischargeRoughCapture = scanDischargeUseRoughCaptureCheckbox.isSelected();
                    if (scanDischargeUseRoughCaptureCheckbox.isSelected()) {
                        scanDischargeUseNativeCaptureCheckbox.setSelected(false);
                    }
                    saveConfig();
                });
                scanDischargeResetConfigBtn.setOnAction(e -> {
                    var opt = options.get();
                    opt.scanDischargeRect = null;
                    opt.scanDischargeCapScale = 0;
                    opt.scanDischargeCriticalPoints = null;
                    opt.scanDischarge = false;
                    saveConfig();
                    scanDischargeCheckbox.setSelected(false);
                });
                hideWhenMouseEnterCheckBox.setOnAction(e -> {
                    var opt = options.get();
                    opt.hideWhenMouseEnter = hideWhenMouseEnterCheckBox.isSelected();
                    saveConfig();
                });
                lockCDWindowPositionCheckBox.setOnAction(e -> {
                    var opt = options.get();
                    opt.lockCDWindowPosition = lockCDWindowPositionCheckBox.isSelected();
                    saveConfig();
                });
                onlyShowFirstLineBuffCheckBox.setOnAction(e -> {
                    var opt = options.get();
                    opt.onlyShowFirstLineBuff = onlyShowFirstLineBuffCheckBox.isSelected();
                    saveConfig();
                });
                applyDischargeForYingZhiCheckBox.setOnAction(e -> {
                    var opt = options.get();
                    opt.applyDischargeForYingZhi = applyDischargeForYingZhiCheckBox.isSelected();
                    saveConfig();
                });
                playAudioCheckBox.setOnAction(e -> {
                    var opt = options.get();
                    opt.playAudio = playAudioCheckBox.isSelected();
                    saveConfig();
                });
                autoFillPianGuangLingYuSubSkillCheckbox.setOnAction(e -> {
                    var opt = options.get();
                    opt.autoFillPianGuangLingYuSubSkill = autoFillPianGuangLingYuSubSkillCheckbox.isSelected();
                    saveConfig();
                });
            }

            setTitle(I18n.get().cooldownOptionsTitle());
            centerOnScreen();
        }

        private void chooseScanDischargeRectStep1(Consumer<Boolean> cb) {
            SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().scanDischargeConfigureTips());
            TaskManager.get().execute(() -> {
                MiscUtils.threadSleep(3_000);
                Platform.runLater(() -> chooseScanDischargeRectStep2(cb));
            });
        }

        @SuppressWarnings("DuplicatedCode")
        private void chooseScanDischargeRectStep2(Consumer<Boolean> cb) {
            Screen screen = FXUtils.getScreenOf(getScene().getWindow());
            if (screen == null) {
                SimpleAlert.showAndWait(Alert.AlertType.WARNING, "cannot find any display");
                cb.accept(false);
                return;
            }
            final Screen fScreen = screen;
            var img = Utils.execRobotOnThread(r -> r.captureScreen(screen));
            var stage = new Stage();
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            stage.setFullScreen(true);

            var imagePane = new Pane();
            imagePane.setBackground(new Background(new BackgroundImage(img,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(1, 1, true, true, false, false
                ))));

            var scene = new Scene(imagePane);

            var scanDischargeRect = new MovableRect(I18n.get().positionOfDischargeTip());
            scanDischargeRect.setLayoutX(img.getWidth() * 2 / 3 / screen.getOutputScaleX());
            scanDischargeRect.setLayoutY(img.getHeight() * 2 / 3 / screen.getOutputScaleY());
            scanDischargeRect.setWidth(128);
            scanDischargeRect.setHeight(128);

            var desc = new Label(I18n.get().scanDischargeScreenDescription()) {{
                FontManager.get().setFont(this, 48);
                setTextFill(Color.RED);
            }};
            {
                var wh = FXUtils.calculateTextBounds(desc);
                desc.setLayoutX(0);
                desc.setLayoutY(img.getHeight() / screen.getOutputScaleY() - wh.getHeight() - 110);
            }

            imagePane.getChildren().addAll(desc, scanDischargeRect);

            imagePane.setOnKeyReleased(e -> {
                if (e.getCode() == javafx.scene.input.KeyCode.ENTER) {
                    if (calculatePointsAndStore(img, scanDischargeRect.makeRect(), fScreen)) {
                        cb.accept(true);
                    } else {
                        Platform.runLater(() -> SimpleAlert.show(Alert.AlertType.WARNING, I18n.get().failedCalculatingCriticalPoints()));
                        cb.accept(false);
                    }
                    stage.close();
                } else if ((e.getCode() == javafx.scene.input.KeyCode.W && e.isControlDown())) {
                    stage.close();
                    cb.accept(false);
                }
            });

            stage.setScene(scene);
            Platform.runLater(imagePane::requestFocus);
            stage.showAndWait();
        }

        private boolean calculatePointsAndStore(Image img, Rect rect, Screen screen) {
            try {
                var wImg = new WritableImage(
                    (int) (rect.w * screen.getOutputScaleX()),
                    (int) (rect.h * screen.getOutputScaleY()));
                wImg.getPixelWriter().setPixels(0, 0,
                    (int) (rect.w * screen.getOutputScaleX()),
                    (int) (rect.h * screen.getOutputScaleY()),
                    img.getPixelReader(),
                    (int) (rect.x * screen.getOutputScaleX()),
                    (int) (rect.y * screen.getOutputScaleY()));
                img = wImg;
            } catch (Exception e) {
                Logger.error("calculatePointsAndStore failure 1", e);
                return false;
            }

            var bImg = SwingFXUtils.fromFXImage(img, null);
            var opt = options.get();
            DischargeCheckContext ctx;
            if (opt.scanDischargeDebug) {
                var g = bImg.createGraphics();
                g.setPaint(new java.awt.Color(255, 0, 0));
                ctx = DischargeCheckContext.of(bImg, g);
            } else {
                ctx = DischargeCheckContext.of(bImg);
            }
            DischargeCheckAlgorithm.DischargeCheckResult result = null;
            if (ctx != null) {
                for (int extraEnsure = 2; extraEnsure >= 0; --extraEnsure) {
                    var args = new SimpleDischargeCheckAlgorithm.Args();
                    args.extraEnsure = extraEnsure;
                    var algo = new SimpleDischargeCheckAlgorithm(args);
                    algo.init(ctx);
                    result = algo.check();
                    if (result.p() >= 0.9) {
                        break;
                    }
                }
            }
            if (opt.scanDischargeDebug) {
                var g = bImg.createGraphics();
                g.setPaint(new java.awt.Color(255, 0, 0));
                g.setFont(new Font(null, Font.BOLD, 16));
                if (result == null) {
                    g.drawString("null", 10, 20);
                } else {
                    g.drawString(Utils.roughFloatValueFormat.format(result.p() * 100) + "%", 10, 20);
                }
                bImg.flush();
                Utils.copyImageToClipboard(bImg);
            }
            if (result == null) {
                return false;
            }
            if (result.p() < 0.90) {
                return false;
            }
            if (result.p() == 1) {
                return false;
            }

            var pointAdjustFound = -1;
            var points = new ArrayList<Point>();
            int widthAfterCut = ctx.getMaxX() - ctx.getMinX();
            pointAdjustLoop:
            for (var pointAdjust : Arrays.asList(widthAfterCut / 24, widthAfterCut / 48, widthAfterCut / 96, widthAfterCut / 144, widthAfterCut / 192)) {
                points.clear();

                int midX = ctx.getInitialX();
                int topY = ctx.getInitialY() + pointAdjust;
                int leftX = ctx.getMinX() + pointAdjust;
                int rightX = ctx.getMaxX() - pointAdjust;
                int botY = ctx.getMaxY() - pointAdjust;

                var p0 = new Point(midX, topY);
                points.add(p0);
                double rightYDelta = (rightX - midX + 1) / Math.sqrt(3) * 0.98;
                var p2 = new Point(rightX, topY + rightYDelta);
                points.add(Point.midOf(p0, p2));
                points.add(p2);
                var p4 = new Point(rightX, botY - rightYDelta);
                points.add(Point.midOf(p2, p4));
                points.add(p4);
                var p6 = new Point(midX, botY);
                points.add(Point.midOf(p4, p6));
                points.add(p6);
                double leftYDelta = (midX - leftX + 1) / Math.sqrt(3) * 0.98;
                var p8 = new Point(leftX, botY - leftYDelta);
                points.add(Point.midOf(p6, p8));
                points.add(p8);
                var p10 = new Point(leftX, topY + leftYDelta);
                points.add(Point.midOf(p8, p10));
                points.add(p10);
                points.add(Point.midOf(p0, p10));
                try { // find the last critical point
                    int n = 1;
                    while (true) {
                        var argb = img.getPixelReader().getArgb(midX - 2 * n, topY + n);
                        if (!DischargeCheckContext.isChargeColor(argb)) {
                            break;
                        }
                        ++n;
                    }
                    points.add(new Point(midX - 2 * n, topY + n));
                } catch (Exception e) {
                    Logger.error("calculatePointsAndStore failure 2", e);
                    continue;
                }
                for (int i = 0; i < points.size() - 1; ++i) {
                    var p = points.get(i);
                    var rgb = img.getPixelReader().getArgb((int) p.x, (int) p.y);
                    if (!DischargeCheckContext.isChargeColor(rgb)) {
                        continue pointAdjustLoop;
                    }
                }
                pointAdjustFound = pointAdjust;
                break;
            }

            if (opt.scanDischargeDebug) {
                bImg = SwingFXUtils.fromFXImage(img, null);
                var g = bImg.createGraphics();
                int pointRadius = (int) (img.getWidth() / 15);
                for (int i = points.size() - 2; i >= 0; --i) {
                    var p = points.get(i);
                    var rgb = bImg.getRGB((int) p.x, (int) p.y);
                    g.setPaint(new java.awt.Color(rgb));
                    g.fillOval((int) (p.x - pointRadius), (int) (p.y - pointRadius), pointRadius * 2, pointRadius * 2);
                }
                g.setPaint(new java.awt.Color(255, 0, 0));
                g.setFont(new Font(null, Font.BOLD, 16));
                if (pointAdjustFound != -1) {
                    g.drawString("adj:" + pointAdjustFound, 10, 20);
                } else {
                    g.drawString("adj:null", 10, 20);
                }
                bImg.flush();
                final var fBImg = bImg;
                Platform.runLater(() -> Utils.copyImageToClipboard(fBImg));
            }

            if (pointAdjustFound == -1) {
                return false;
            }

            // cut rect and move the points
            int cutLeft = (int) Math.round(ctx.getMinX() / screen.getOutputScaleX());
            int cutTop = (int) Math.round(ctx.getInitialY() / screen.getOutputScaleY());
            int cutRight = (int) Math.round(rect.w - ctx.getMaxX() / screen.getOutputScaleX());
            int cutBot = (int) Math.round(rect.h - ctx.getMaxY() / screen.getOutputScaleY());
            opt.scanDischargeRect = new Rect(
                rect.x + cutLeft,
                rect.y + cutTop,
                rect.w - cutLeft - cutRight + 2,
                rect.h - cutTop - cutBot + 2);
            opt.scanDischargeCapScale = screen.getOutputScaleX();
            for (var p : points) {
                p.x -= (int) (cutLeft * screen.getOutputScaleX());
                if (p.x < 0) {
                    p.x = 0;
                }
                p.y -= (int) (cutTop * screen.getOutputScaleY());
                if (p.y < 0) {
                    p.y = 0;
                }
            }
            opt.scanDischargeCriticalPoints = points;
            return true;
        }
    }
}
