package net.cassite.hottapcassistant.ui;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import net.cassite.hottapcassistant.component.HPadding;
import net.cassite.hottapcassistant.component.VPadding;
import net.cassite.hottapcassistant.config.AssistantConfig;
import net.cassite.hottapcassistant.config.InputConfig;
import net.cassite.hottapcassistant.data.Matrix;
import net.cassite.hottapcassistant.data.Relics;
import net.cassite.hottapcassistant.data.Simulacra;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.simulacra.DummySimulacra;
import net.cassite.hottapcassistant.entity.*;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class CoolDownPane extends StackPane implements EnterCheck, Terminate {
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
                    weaponSelect.setPrefWidth(150);
                    image.setFitWidth(150);
                    image.setFitHeight(150);
                    starsSelect.setEditable(false);
                    starsSelect.setItems(FXCollections.observableList(Arrays.asList(0, 1, 2, 3, 4, 5, 6)));
                    starsSelect.getSelectionModel().select(6);
                    starsSelect.setPrefWidth(150);
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
                    matrixSelect.setPrefWidth(120);
                    image.setFitWidth(120);
                    image.setFitHeight(120);
                    starsSelect.setEditable(false);
                    starsSelect.setItems(FXCollections.observableList(Arrays.asList(0, 1, 2, 3)));
                    starsSelect.getSelectionModel().select(3);
                    starsSelect.setPrefWidth(120);
                }
            }
        }

        {
            var sep = new Separator();
            sep.setPadding(new Insets(10, 0, 10, 0));
            vbox.getChildren().add(sep);
        }

        // buttons
        {
            var hbox = new HBox();
            vbox.getChildren().add(hbox);

            var startBtn = new Button(I18n.get().startCoolDown()) {{
                FontManager.setFont(this);
            }};
            startBtn.setPrefWidth(120);
            startBtn.setOnAction(e -> start());

            var stopBtn = new Button(I18n.get().stopCoolDown()) {{
                FontManager.setFont(this);
            }};
            stopBtn.setPrefWidth(120);
            stopBtn.setDisable(true);
            stopBtn.setOnAction(e -> stop());

            isStarted.addListener((ob, old, now) -> {
                if (now == null) return;
                startBtn.setDisable(now);
                stopBtn.setDisable(!now);
            });

            var tipsBtn = new Button(I18n.get().cooldownTipsButton()) {{
                FontManager.setFont(this);
            }};
            tipsBtn.setPrefWidth(120);
            tipsBtn.setOnAction(e -> new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().cooldownTips()).show());

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
                relics0.setPrefWidth(120);
                relicsStars0.setEditable(false);
                relicsStars0.setItems(FXCollections.observableList(Arrays.asList(0, 1, 2, 3, 4, 5)));
                relicsStars0.getSelectionModel().select(3);
                relicsStars0.setPrefWidth(120);
            }

            ComboBox<SimulacraRef> simulacraRefComboBox;
            {
                simulacraRefComboBox = new ComboBox<>();
                simulacraRefComboBox.setPrefWidth(120);
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

            hbox.getChildren().addAll(startBtn, new HPadding(4), stopBtn,
                new HPadding(4), tipsBtn,
                new HPadding(20), new VBox() {{
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

        var set = new HashSet<Integer>();
        for (var weapon : weapons) {
            if (weapon.get() == null) {
                new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().weaponNotSelected()).showAndWait();
                return;
            }
            set.add(weapon.get().id);
        }
        if (set.size() != 3 || (set.contains(16) && set.contains(17))) {
            new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().duplicatedWeapon()).showAndWait();
            return;
        }
        set.clear();
        Relics[] relics = new Relics[]{null, null};
        for (int i = 0; i < this.relics.length; i++) {
            var r = this.relics[i];
            var rr = r.get();
            if (rr != null) {
                if (!set.add(rr.id) && rr.id != 0) {
                    new SimpleAlert(Alert.AlertType.INFORMATION, I18n.get().duplicatedRelics()).showAndWait();
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
            weapons.add(this.weapons[i].get().make(matrixs));
        }

        { // build config
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
            Logger.info("weapon properties:\n" + config.toJson().pretty());

            try {
                AssistantConfig.updateAssistant(ass -> ass.cooldown = config);
            } catch (IOException e) {
                Logger.error("updating assistant config for cooldown failed", e);
            }
        }

        var window = new CoolDownWindow(weapons, relics, simulacra, weaponSkill, melee, evade, changeWeapons, useArtifacts, jump,
            this::reset);
        this.window = window;
        setWindowPosition(window);
        window.show();
        Utils.iconifyWindow(getScene().getWindow());
    }

    private double windowPositionX;
    private double windowPositionY;
    private double windowScale = 1;

    private void setWindowPosition(CoolDownWindow window) {
        if (windowPositionX == 0 && windowPositionY == 0 && windowScale == 1) return;
        window.setX(windowPositionX);
        window.setY(windowPositionY);
        window.setScale(windowScale);
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
            window.close();
        }
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
            ass = AssistantConfig.readAssistant();
        } catch (IOException e) {
            new StackTraceAlert(e).show();
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
                    new StackTraceAlert(e).show();
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

    @Override
    public void terminate() {
        stop();
    }
}
