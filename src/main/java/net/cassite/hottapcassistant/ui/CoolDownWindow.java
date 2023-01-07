package net.cassite.hottapcassistant.ui;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import javafx.animation.AnimationTimer;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.cassite.hottapcassistant.component.cooldown.WeaponCoolDown;
import net.cassite.hottapcassistant.component.cooldown.WithDesc;
import net.cassite.hottapcassistant.component.cooldown.WithId;
import net.cassite.hottapcassistant.data.Relics;
import net.cassite.hottapcassistant.data.Simulacra;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.discharge.DischargeDetector;
import net.cassite.hottapcassistant.entity.AssistantCoolDownOptions;
import net.cassite.hottapcassistant.entity.InputData;
import net.cassite.hottapcassistant.entity.Key;
import net.cassite.hottapcassistant.entity.KeyCode;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.*;

import java.util.*;
import java.util.stream.Collectors;

public class CoolDownWindow extends Stage implements NativeKeyListener, NativeMouseListener {
    private static final InputData SpecialAttack = new InputData(new Key(KeyCode.BACKQUOTE));
    private final WeaponContext ctx;
    private final InputData weaponSkill;
    private final InputData[] melee;
    private final InputData[] evade;
    private final InputData[] changeWeapons;
    private final InputData[] artifact;
    private final InputData jump;
    private final AnimationTimer timer;
    private final WeaponCoolDown[] cds;
    private final Text descLabel;
    private final List<Group> buffs = new ArrayList<>();
    private final List<Group> row2 = new ArrayList<>();
    private final DischargeDetector dischargeDetector;

    private final Scale scale = new Scale(1, 1);
    private final int totalIndicatorCount;

    private final WeaponCoolDown chargePercentage;
    private final ImageView pauseResumeBtn;

    public CoolDownWindow(List<Weapon> weapons, Relics[] relics, Simulacra simulacra,
                          InputData weaponSkill, InputData[] melee, InputData[] evade,
                          InputData[] changeWeapons, InputData[] artifact,
                          InputData jump,
                          Set<String> row2Ids, AssistantCoolDownOptions options,
                          Runnable resetCallback) {
        this.ctx = new WeaponContext(weapons, relics, simulacra, options != null && options.playAudio);
        this.weaponSkill = weaponSkill;
        this.melee = melee;
        this.evade = evade;
        this.changeWeapons = changeWeapons;
        this.artifact = artifact;
        this.jump = jump;
        this.lastWeaponButtonDownTs = new long[weapons.size()];
        this.lastArtifactButtonDownTs = new long[artifact.length];
        if (options == null || !options.scanDischargeEnabled()) {
            dischargeDetector = null;
        } else {
            dischargeDetector = new DischargeDetector(
                options.scanDischargeRect,
                options.scanDischargeCriticalPoints,
                options.scanDischargeDebug
            );
        }

        GlobalScreenUtils.enable(this);
        GlobalScreen.addNativeKeyListener(this);
        GlobalScreen.addNativeMouseListener(this);

        var group = new Group();
        group.getTransforms().add(scale);
        group.setOnScroll(e -> {
            double zoomFactor = 0.01;
            if (e.getDeltaY() < 0) {
                zoomFactor = -zoomFactor;
            }
            double oldRatio = scale.getX();
            double ratio = oldRatio + zoomFactor;
            if (ratio < 0.2) {
                ratio = 0.2;
            } else if (ratio > 3) {
                ratio = 3;
            }
            scale.setX(ratio);
            scale.setY(ratio);
        });

        var scene = new Scene(group);
        scene.setFill(Color.TRANSPARENT);
        setScene(scene);
        initStyle(StageStyle.TRANSPARENT);
        setAlwaysOnTop(true);
        setResizable(false);
        centerOnScreen();
        setTitle("CoolDown Indicator");

        pauseResumeBtn = new ImageView();
        initPauseResumeBtn(true);
        group.getChildren().add(pauseResumeBtn);

        var resetBtn = new ImageView(ImageManager.get().load("/images/icon/reloading.png"));
        resetBtn.setLayoutX(10 + (2 * r + margin) + (WeaponCoolDown.MAX_RADIUS - WeaponCoolDown.MIN_RADIUS) + 2);
        resetBtn.setLayoutY(10 + (WeaponCoolDown.MAX_RADIUS - WeaponCoolDown.MIN_RADIUS) + 2);
        resetBtn.setFitWidth(2 * (WeaponCoolDown.MIN_RADIUS - 2));
        resetBtn.setFitHeight(2 * (WeaponCoolDown.MIN_RADIUS - 2));
        resetBtn.setCursor(Cursor.HAND);
        resetBtn.setOnMouseClicked(e -> resetCallback.run());
        resetBtn.setOnMouseEntered(e -> setTextForDescLabel(I18n.get().cooldownResetDesc()));
        resetBtn.setOnMouseExited(e -> setTextForDescLabel(""));
        group.getChildren().add(resetBtn);

        cds = new WeaponCoolDown[weapons.size()];
        for (var i = 0; i < weapons.size(); ++i) {
            cds[i] = new WeaponCoolDown(weapons.get(i).getImage(), weapons.get(i).getId(), weapons.get(i).getName());
            if (weapons.get(i) == ctx.current) {
                cds[i].setActive(true);
            }
        }

        for (var w : weapons) {
            buffs.addAll(addBuffPositionHandler(w.extraIndicators()));
            buffs.addAll(addBuffPositionHandler(w.extraInfo()));
            var matrix = w.getMatrix();
            for (var m : matrix) {
                buffs.addAll(addBuffPositionHandler(m.extraIndicators()));
                buffs.addAll(addBuffPositionHandler(m.extraInfo()));
            }
        }
        for (var r : relics) {
            if (r == null) continue;
            buffs.addAll(addBuffPositionHandler(r.extraIndicators()));
            buffs.addAll(addBuffPositionHandler(r.extraInfo()));
        }
        buffs.addAll(addBuffPositionHandler(simulacra.extraIndicators()));
        buffs.addAll(addBuffPositionHandler(simulacra.extraInfo()));
        buffs.addAll(addBuffPositionHandler(ctx.extraIndicators()));
        buffs.addAll(addBuffPositionHandler(ctx.extraInfo()));

        if (dischargeDetector != null) {
            chargePercentage = new WeaponCoolDown(Utils.getBuffImageFromClasspath("charge"), "chargePercentage", I18n.get().buffName("chargePercentage")) {{
                setRotateAngle(180);
            }};
            chargePercentage.setOnMouseClicked(e -> {
                if (row2.contains(chargePercentage)) {
                    moveUp(chargePercentage);
                } else {
                    moveDown(chargePercentage);
                }
            });
            buffs.add(chargePercentage);
        } else {
            chargePercentage = null;
        }

        totalIndicatorCount = 3 + buffs.size();

        resizeWindow();
        scale.xProperty().addListener((ob, old, now) -> resizeWindow());

        descLabel = new Text() {{
            FontManager.setFont(this, 24);
            setFill(Color.WHITE);
            setStrokeWidth(0.5);
            setStroke(Color.BLACK);
        }};
        descLabel.setLayoutY(10 + 2 * r + 2 + 25);
        for (var i = 0; i < cds.length; ++i) {
            var n = cds[i];
            n.setLayoutX(10 + r + (2 * r + margin) * (i + 2));
            n.setLayoutY(10 + r);
            String desc;
            desc = n.desc();
            n.setOnMouseEntered(e -> setTextForDescLabel(desc));
            n.setOnMouseExited(e -> setTextForDescLabel(""));
            group.getChildren().add(n);
        }
        for (var g : buffs) {
            if (!(g instanceof WithId)) continue;
            var id = ((WithId) g).id();
            if (row2Ids.contains(id)) {
                row2.add(g);
            }
        }
        setBuffPosition();
        for (var g : buffs) {
            var desc = (g instanceof WithDesc) ? ((WithDesc) g).desc() : "";
            g.setOnMouseEntered(e -> setTextForDescLabel(desc));
            g.setOnMouseExited(e -> setTextForDescLabel(""));
            group.getChildren().add(g);
        }
        {
            var sep = new Line();
            sep.setStrokeWidth(2);
            sep.setStartX(0);
            sep.setStartY(r / 2);
            sep.setEndX(0);
            sep.setEndY(2 * r - r / 2);
            sep.setStroke(Color.GRAY);
            sep.setLayoutX(10 + (2 * r + margin) * 2 - margin / 2 - 1);
            sep.setLayoutY(10);
            group.getChildren().add(sep);
        }
        if (!buffs.isEmpty()) {
            var sep = new Line();
            sep.setStrokeWidth(2);
            sep.setStartX(0);
            sep.setStartY(r / 2);
            sep.setEndX(0);
            sep.setEndY(2 * r - r / 2);
            sep.setStroke(Color.GRAY);
            sep.setLayoutX(10 + (2 * r + margin) * 5 - margin / 2);
            sep.setLayoutY(10);
            group.getChildren().add(sep);
        }
        group.getChildren().add(descLabel);

        var dragHandler = new DragHandler(xy -> {
            setX(xy[0]);
            setY(xy[1]);
        }, () -> new double[]{getX(), getY()});
        group.setOnMousePressed(dragHandler);
        group.setOnMouseDragged(dragHandler);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateUI();
            }
        };

        if (dischargeDetector != null) {
            dischargeDetector.start();
        }
        ctx.start();
        timer.start();
    }

    private void initPauseResumeBtn(boolean isPause) {
        pauseResumeBtn.setCursor(Cursor.HAND);
        if (isPause) {
            pauseResumeBtn.setImage(ImageManager.get().load("/images/icon/pause.png"));
            pauseResumeBtn.setLayoutX(10 + 5 + (WeaponCoolDown.MAX_RADIUS - WeaponCoolDown.MIN_RADIUS));
            pauseResumeBtn.setLayoutY(10 + (WeaponCoolDown.MAX_RADIUS - WeaponCoolDown.MIN_RADIUS));
            pauseResumeBtn.setFitWidth(2 * (WeaponCoolDown.MIN_RADIUS) + 2);
            pauseResumeBtn.setFitHeight(2 * (WeaponCoolDown.MIN_RADIUS) + 2);
            pauseResumeBtn.setOnMouseClicked(e -> pause());
            pauseResumeBtn.setOnMouseEntered(e -> setTextForDescLabel(I18n.get().cooldownPauseDesc()));
        } else {
            pauseResumeBtn.setImage(ImageManager.get().load("/images/icon/play-button.png"));
            pauseResumeBtn.setLayoutX(10 + 5 + (WeaponCoolDown.MAX_RADIUS - WeaponCoolDown.MIN_RADIUS) + 2);
            pauseResumeBtn.setLayoutY(10 + 1 + (WeaponCoolDown.MAX_RADIUS - WeaponCoolDown.MIN_RADIUS) + 2);
            pauseResumeBtn.setFitWidth(2 * (WeaponCoolDown.MIN_RADIUS - 2));
            pauseResumeBtn.setFitHeight(2 * (WeaponCoolDown.MIN_RADIUS - 2));
            pauseResumeBtn.setOnMouseClicked(e -> resume());
            pauseResumeBtn.setOnMouseEntered(e -> setTextForDescLabel(I18n.get().cooldownResumeDesc()));
        }
        pauseResumeBtn.setOnMouseExited(e -> setTextForDescLabel(""));
    }

    private volatile boolean isPaused = false;

    private void pause() {
        isPaused = true;
        initPauseResumeBtn(false);
        setTextForDescLabel(I18n.get().cooldownResumeDesc());
        keys.clear();
        btns.clear();
        if (dischargeDetector != null) {
            dischargeDetector.pause();
        }
    }

    private void resume() {
        isPaused = false;
        initPauseResumeBtn(true);
        setTextForDescLabel(I18n.get().cooldownPauseDesc());
        if (dischargeDetector != null) {
            dischargeDetector.resume();
        }
    }

    private Collection<? extends Group> addBuffPositionHandler(List<? extends Group> ls) {
        for (var g : ls) {
            var cb = g.getOnMouseClicked();
            if (cb == null) {
                cb = e -> {
                };
            }
            final var fcb = cb;
            g.setOnMouseClicked(e -> {
                if (e.getButton() != MouseButton.SECONDARY) {
                    fcb.handle(e);
                    return;
                }
                if (row2.contains(g)) {
                    moveUp(g);
                } else {
                    moveDown(g);
                }
            });
        }
        return ls;
    }

    private void moveDown(Group g) {
        if (row2.contains(g)) {
            return;
        }
        row2.add(g);
        row2.sort((a, b) -> {
            var ai = buffs.indexOf(a);
            var bi = buffs.indexOf(b);
            return ai - bi;
        });
        setBuffPosition();
    }

    private void moveUp(Group g) {
        if (!row2.contains(g)) {
            return;
        }
        row2.remove(g);
        setBuffPosition();
    }

    private void setBuffPosition() {
        var row1 = new ArrayList<>(buffs);
        row1.removeAll(row2);
        for (var n : buffs) {
            if (row2.contains(n)) {
                int i = row2.indexOf(n);
                n.setLayoutX(10 + r + (2 * r + margin) * (i + 5));
                n.setLayoutY(10 + r + (2 * r + marginV));
            } else {
                int i = row1.indexOf(n);
                n.setLayoutX(10 + r + (2 * r + margin) * (i + 5));
                n.setLayoutY(10 + r);
            }
        }
    }

    private static final double margin = 5;
    private static final double marginV = 2;
    private static final double r = WeaponCoolDown.MAX_RADIUS;

    private void resizeWindow() {
        setWidth((10 + (2 * r) * (totalIndicatorCount + 2) + margin * ((totalIndicatorCount + 2) - 1) + 10) * scale.getX());
        final var rows = 2;
        //noinspection PointlessArithmeticExpression
        setHeight((10 + (2 * r) * rows + 10 * (rows - 1) + 10) * scale.getY());
    }

    private static final double weaponMiddleX = 10 + r + (2 * r + margin) * 3 - margin / 2;

    private void setTextForDescLabel(String s) {
        if (s.equals(descLabel.getText())) return;
        descLabel.setText(s);
        if (s.isBlank()) return;
        var bounds = Utils.calculateTextBounds(descLabel);
        descLabel.setLayoutX(weaponMiddleX - bounds.getWidth() / 2);
    }

    private final Set<KeyCode> keys = new HashSet<>();
    private final Set<MouseButton> btns = new HashSet<>();

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (isPaused) return;

        var key = KeyCode.valueOf(e.getKeyCode());
        if (key == null) return;
        handlePressed(key, null);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        if (isPaused) return;

        var key = KeyCode.valueOf(e.getKeyCode());
        if (key == null) return;
        if (keys.contains(key)) {
            handle(key, null);
            keys.remove(key);
        }
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent e) {
        if (isPaused) return;

        var btn = e.getButton();
        MouseButton b = null;
        if (btn == NativeMouseEvent.BUTTON1) {
            b = MouseButton.PRIMARY;
        } else if (btn == NativeMouseEvent.BUTTON2) {
            b = MouseButton.SECONDARY;
        }
        if (b == null) return;
        handlePressed(null, b);
    }

    private void handlePressed(KeyCode key, MouseButton btn) {
        boolean handle = false;
        if (key != null) {
            handle = keys.add(key);
        } else if (btn != null) {
            handle = btns.add(btn);
        }
        if (!handle) {
            return;
        }
        for (var input : melee) {
            if (input.matches(keys, btns, key, btn)) {
                lastAttackButtonDownTs = System.currentTimeMillis();
                break;
            }
        }
        for (var i = 0; i < changeWeapons.length; ++i) {
            InputData input = changeWeapons[i];
            if (input.matches(keys, btns, key, btn)) {
                lastWeaponButtonDownTs[i] = System.currentTimeMillis();
                break;
            }
        }
        for (int i = 0; i < artifact.length; i++) {
            InputData input = artifact[i];
            if (input.matches(keys, btns, key, btn)) {
                lastArtifactButtonDownTs[i] = System.currentTimeMillis();
                break;
            }
        }
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {
        if (isPaused) return;

        var btn = e.getButton();
        MouseButton b = null;
        if (btn == NativeMouseEvent.BUTTON1) {
            b = MouseButton.PRIMARY;
        } else if (btn == NativeMouseEvent.BUTTON2) {
            b = MouseButton.SECONDARY;
        }
        if (b == null) return;
        if (btns.contains(b)) {
            handle(null, b);
            btns.remove(b);
        }
    }

    private void handle(KeyCode key, MouseButton btn) {
        if (weaponSkill.matches(keys, btns, key, btn)) {
            ctx.useSkill();
        } else if (SpecialAttack.matches(keys, btns, key, btn)) {
            ctx.specialAttack();
        } else if (jump.matches(keys, btns, key, btn)) {
            ctx.jump();
        } else {
            for (var input : melee) {
                if (input.matches(keys, btns, key, btn)) {
                    attack();
                    break;
                }
            }
            for (var input : evade) {
                if (input.matches(keys, btns, key, btn)) {
                    dodge();
                    break;
                }
            }
            for (var i = 0; i < changeWeapons.length; ++i) {
                var cw = changeWeapons[i];
                if (cw.matches(keys, btns, key, btn)) {
                    changeWeapon(i);
                    return;
                }
            }
            for (var i = 0; i < artifact.length; ++i) {
                var a = artifact[i];
                if (a.matches(keys, btns, key, btn)) {
                    useArtifact(i);
                    return;
                }
            }
        }
    }

    private void changeWeapon(int index) {
        long current = System.currentTimeMillis();
        boolean discharge = false;
        if (dischargeDetector != null) {
            discharge = dischargeDetector.isFullCharge();
        }
        if (!discharge) {
            discharge = current - lastWeaponButtonDownTs[index] > 180;
        }
        boolean ok = ctx.switchWeapon(index, discharge);
        if (!ok) {
            return;
        }
        cds[index].setActive(true);
        for (var i = 0; i < cds.length; ++i) {
            if (i == index) continue;
            cds[i].setActive(false);
        }
        if (discharge && dischargeDetector != null) {
            dischargeDetector.discharge();
        }
    }

    private void useArtifact(int index) {
        long current = System.currentTimeMillis();
        ctx.useRelics(index, current - lastArtifactButtonDownTs[index] > 300);
    }

    private long lastAttackButtonDownTs;
    private final long[] lastWeaponButtonDownTs;
    private final long[] lastArtifactButtonDownTs;
    private long lastDodgeTs;

    private void attack() {
        long current = System.currentTimeMillis();
        if (current - lastDodgeTs < 1_000) {
            ctx.dodgeAttack();
        } else if (current - lastAttackButtonDownTs > 300) {
            ctx.aimAttack();
        } else {
            ctx.attack();
        }
        lastDodgeTs = 0;
        lastAttackButtonDownTs = 0;
    }

    private void dodge() {
        lastDodgeTs = System.currentTimeMillis();
        ctx.dodge();
    }

    private void updateUI() {
        for (var i = 0; i < ctx.weapons.size(); ++i) {
            var w = ctx.weapons.get(i);
            cds[i].setCoolDown(w.getCoolDown());
            cds[i].setAllCoolDown(w.getAllCoolDown());

            {
                var cd = ctx.getSwitchWeaponCoolDown(i);
                var total = ctx.getTotalSwitchWeaponCoolDown();
                var p = (cd / (double) total);
                if (p == 0) {
                    cds[i].setLayoutY(10 + r);
                } else {
                    cds[i].setLayoutY(10 + r + 10 + 20 * p);
                }
            }
        }
        ctx.updateExtraData();
        if (chargePercentage != null) {
            if (dischargeDetector != null) {
                if (dischargeDetector.isFullCharge()) {
                    chargePercentage.setAllCoolDown(new double[]{1, dischargeDetector.getPercentage()});
                } else {
                    chargePercentage.setAllCoolDown(new double[]{dischargeDetector.getPercentage()});
                }
            }
        }
    }

    @Override
    public void close() {
        stop();
        super.close();
    }

    private void stop() {
        timer.stop();
        ctx.stop();
        if (dischargeDetector != null) {
            dischargeDetector.stop();
        }
        GlobalScreen.removeNativeKeyListener(this);
        GlobalScreen.removeNativeMouseListener(this);
        GlobalScreenUtils.disable(this);
    }

    public void setScale(double scale) {
        this.scale.setX(scale);
        this.scale.setY(scale);
    }

    public double getScale() {
        return scale.getX();
    }

    public List<String> getBuffs() {
        return buffs.stream().map(g -> {
            if (g instanceof WithId id) {
                return id.id();
            } else {
                return "";
            }
        }).filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }

    public List<String> getRow2() {
        return row2.stream().map(g -> {
            if (g instanceof WithId id) {
                return id.id();
            } else {
                return "";
            }
        }).filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }
}
