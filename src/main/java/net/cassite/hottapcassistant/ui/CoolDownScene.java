package net.cassite.hottapcassistant.ui;

import io.vproxy.vfx.entity.input.InputData;
import io.vproxy.vfx.entity.input.Key;
import io.vproxy.vfx.entity.input.KeyCode;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.ui.alert.SimpleAlert;
import io.vproxy.vfx.ui.alert.StackTraceAlert;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.layout.HPadding;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.pane.FusionPane;
import io.vproxy.vfx.ui.scene.VSceneGroup;
import io.vproxy.vfx.ui.scene.VSceneHideMethod;
import io.vproxy.vfx.ui.scene.VSceneShowMethod;
import io.vproxy.vfx.ui.wrapper.FusionW;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import io.vproxy.vfx.util.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.cassite.hottapcassistant.config.AssistantConfig;
import net.cassite.hottapcassistant.config.InputConfig;
import net.cassite.hottapcassistant.data.Matrix;
import net.cassite.hottapcassistant.data.Relics;
import net.cassite.hottapcassistant.data.Simulacra;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.simulacra.DummySimulacra;
import net.cassite.hottapcassistant.entity.*;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.ui.cooldown.*;
import net.cassite.hottapcassistant.util.GlobalValues;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked", "rawtypes"})
public class CoolDownScene extends MainScene implements EnterCheck, Terminate {
    private final VSceneGroup sceneGroup;
    private InputData weaponSkill;
    private InputData additionalSkill;
    private InputData[] melee;
    private InputData[] evade;
    private final InputData[] changeWeapons = new InputData[3];
    private final InputData[] useArtifacts = new InputData[2];
    private InputData jump;

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
    private final ObservableList<AssistantCoolDownYueXingChuanSanLiuSkill> yueXingChuanSanLiuSkills = FXCollections.observableList(new ArrayList<>());
    private final Set<String> row2Ids = new HashSet<>();
    private final ObservableList<String> configurationNames = FXCollections.observableList(new ArrayList<>());
    private final ObservableList<AssistantCoolDownConfiguration> configurations = FXCollections.observableList(new ArrayList<>());
    private final SimpleObjectProperty<AssistantCoolDownOptions> options = new SimpleObjectProperty<>(AssistantCoolDownOptions.empty());
    private final CoolDownOptionsScene optionsScene;
    private final CoolDownTips coolDownTipsScene = new CoolDownTips();

    public CoolDownScene(VSceneGroup sceneGroup) {
        enableAutoContentWidth();

        this.sceneGroup = sceneGroup;
        this.optionsScene = new CoolDownOptionsScene(options, yueXingChuanSanLiuSkills, this);
        sceneGroup.addScene(optionsScene, VSceneHideMethod.TO_RIGHT);
        sceneGroup.addScene(coolDownTipsScene, VSceneHideMethod.TO_RIGHT);

        var vbox = new VBox();
        FXUtils.observeWidthCenter(getContentPane(), vbox);
        getContentPane().getChildren().add(vbox);

        var matrixScene0 = new CooldownMatrixScene(matrixs[0]);
        var matrixScene1 = new CooldownMatrixScene(matrixs[1]);
        var matrixScene2 = new CooldownMatrixScene(matrixs[2]);
        var matrixScenes = Arrays.asList(
            matrixScene0, matrixScene1, matrixScene2
        );
        var matrixSceneGroup = new VSceneGroup(matrixScene0);
        matrixSceneGroup.addScene(matrixScene1);
        matrixSceneGroup.addScene(matrixScene2);

        matrixSceneGroup.getNode().setPrefWidth(
            CooldownMatrixChooser.WIDTH_HEIGHT * 4 + 3 * CooldownMatrixScene.SPACING + FusionPane.PADDING_H * 2 * 4 + 10);
        matrixSceneGroup.getNode().setPrefHeight(FusionPane.PADDING_V * 2 + CooldownMatrixChooser.WIDTH_HEIGHT + 100);

        List<CooldownWeaponChooser> choosers = new ArrayList<>();
        var weaponChooser0 = new CooldownWeaponChooser(matrixSceneGroup, matrixScenes, choosers, 0, weapons[0]);
        var weaponChooser1 = new CooldownWeaponChooser(matrixSceneGroup, matrixScenes, choosers, 1, weapons[1]);
        var weaponChooser2 = new CooldownWeaponChooser(matrixSceneGroup, matrixScenes, choosers, 2, weapons[2]);
        choosers.addAll(Arrays.asList(weaponChooser0, weaponChooser1, weaponChooser2));
        var weaponsHBox = new HBox(weaponChooser0.getNode(), weaponChooser1.getNode(), weaponChooser2.getNode());
        weaponsHBox.setSpacing(20);

        var controlButtonAndRelicsAndSimulacraHBox = new HBox();
        controlButtonAndRelicsAndSimulacraHBox.setSpacing(10);

        final int BUTTON_WIDTH = 120;
        var buttonsVBox = new VBox();
        buttonsVBox.setMaxWidth(700);

        {
            var hbox = new HBox();
            buttonsVBox.getChildren().add(hbox);

            var label = new ThemeLabel(I18n.get().cooldownConfigurationLabel()) {{
                FontManager.get().setFont(this, settings -> settings.setSize(14));
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
            equipConfig.setPrefWidth(190);
            hbox.getChildren().addAll(
                label,
                new HPadding(5),
                new FusionW(equipConfig) {{
                    enableLabelBackground();
                    getLabel().setPadding(new Insets(0, 0, 0, 10));
                }},
                new HPadding(5));

            var saveButton = new FusionButton(I18n.get().cooldownConfigurationSave()) {{
                FontManager.get().setFont(getTextNode(), settings -> settings.setSize(13));
                setPrefWidth(37);
                setPrefHeight(26);
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
            var deleteButton = new FusionButton(I18n.get().cooldownConfigurationDelete()) {{
                FontManager.get().setFont(getTextNode(), settings -> settings.setSize(13));
                setPrefWidth(37);
                setPrefHeight(26);
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

            buttonsVBox.getChildren().add(new VPadding(10));
            buttonsVBox.getChildren().add(new HBox(saveButton, new HPadding(5), deleteButton) {{
                setAlignment(Pos.CENTER_RIGHT);
            }});
        }

        {
            buttonsVBox.getChildren().add(new VPadding(25));
        }

        // buttons
        {
            var hbox = new HBox();
            buttonsVBox.getChildren().add(hbox);

            var startBtn = new FusionButton(I18n.get().startCoolDown()) {{
                FontManager.get().setFont(getTextNode());
            }};
            startBtn.setPrefWidth(BUTTON_WIDTH);
            startBtn.setPrefHeight(30);
            startBtn.setOnAction(e -> start());

            var stopBtn = new FusionButton(I18n.get().stopCoolDown()) {{
                FontManager.get().setFont(getTextNode());
            }};
            stopBtn.setPrefWidth(BUTTON_WIDTH);
            stopBtn.setPrefHeight(30);
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

            var optionsBtn = new FusionButton(I18n.get().cooldownOptionsBtn()) {{
                FontManager.get().setFont(getTextNode());
            }};
            optionsBtn.setPrefWidth(BUTTON_WIDTH);
            optionsBtn.setPrefHeight(30);
            optionsBtn.setOnAction(e -> {
                GlobalValues.setBackFunction(() ->
                    sceneGroup.hide(optionsScene, VSceneHideMethod.TO_RIGHT));
                sceneGroup.show(optionsScene, VSceneShowMethod.FROM_RIGHT);
            });

            var tipsBtn = new FusionButton(I18n.get().cooldownTipsButton()) {{
                FontManager.get().setFont(getTextNode());
            }};
            tipsBtn.setPrefWidth(BUTTON_WIDTH);
            tipsBtn.setPrefHeight(30);
            tipsBtn.setOnAction(e -> {
                GlobalValues.setBackFunction(() ->
                    sceneGroup.hide(coolDownTipsScene, VSceneHideMethod.TO_RIGHT));
                sceneGroup.show(coolDownTipsScene, VSceneShowMethod.FROM_RIGHT);
            });

            hbox.getChildren().addAll(optionsBtn, new HPadding(4), tipsBtn);
        }

        var relicsChooser0 = new CooldownRelicsChooser(relics[0], 0);
        var relicsChooser1 = new CooldownRelicsChooser(relics[1], 1);
        var simulacraChooser = new CooldownSimulacraChooser(simulacra);

        controlButtonAndRelicsAndSimulacraHBox.getChildren().addAll(
            new FusionPane(false, buttonsVBox).getNode(),
            relicsChooser0.getNode(),
            relicsChooser1.getNode(),
            simulacraChooser.getNode()
        );

        vbox.getChildren().addAll(
            new VPadding(35),
            weaponsHBox,
            new VPadding(5),
            matrixSceneGroup.getNode(),
            new VPadding(30),
            controlButtonAndRelicsAndSimulacraHBox
        );
    }

    @Override
    public String title() {
        return I18n.get().toolNameCoolDown();
    }

    private final SimpleBooleanProperty isStarted = new SimpleBooleanProperty(false);
    private CoolDownWindow window = null;

    private void start() {
        if (isStarted.get()) return;

        if (sceneGroup.isShowing(optionsScene)) {
            sceneGroup.hide(optionsScene, VSceneHideMethod.TO_RIGHT);
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
            weapons.add(w);
        }

        saveConfig();

        var window = new CoolDownWindow(weapons, relics, simulacra, weaponSkill, additionalSkill, melee, evade, changeWeapons, useArtifacts, jump,
            row2Ids, new AssistantCoolDownOptions(options.get()), new ArrayList<>(yueXingChuanSanLiuSkills),
            this::reset);
        this.window = window;
        setWindowPosition(window);
        window.show();
        FXUtils.iconifyWindow(getNode().getScene().getWindow());
    }

    public void saveConfig() {
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
        ret.yueXingChuanSanLiuSkills = cd.yueXingChuanSanLiuSkills;

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
        config.yueXingChuanSanLiuSkills = new ArrayList<>();
        {
            config.yueXingChuanSanLiuSkills.addAll(yueXingChuanSanLiuSkills);
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
        InputData additionalSkill = null;
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
            } else if ("SkillAdditional".equals(kb.action)) {
                additionalSkill = kb;
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
        if (additionalSkill == null) {
            additionalSkill = new InputData(new Key(KeyCode.X));
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
        this.additionalSkill = additionalSkill;
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
        loadYueXingChuanSanLiuSkills(cooldown.yueXingChuanSanLiuSkills);
        loadRow2Ids(cooldown.row2Ids);
        loadConfigurations(cooldown.configurations);
        loadOptions(cooldown.options);
    }

    private void loadFromConfig(AssistantCoolDownConfiguration cooldown) {
        if (cooldown == null) return;
        loadWeaponsFromConfig(cooldown.weapons);
        loadRelicsFromConfig(cooldown.relics);
        loadSimulacraFromConfig(cooldown.simulacra);
        loadYueXingChuanSanLiuSkills(cooldown.yueXingChuanSanLiuSkills);
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

    private void loadYueXingChuanSanLiuSkills(List<AssistantCoolDownYueXingChuanSanLiuSkill> skills) {
        if (skills == null) return;
        this.yueXingChuanSanLiuSkills.clear();
        this.yueXingChuanSanLiuSkills.addAll(skills);
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
}
