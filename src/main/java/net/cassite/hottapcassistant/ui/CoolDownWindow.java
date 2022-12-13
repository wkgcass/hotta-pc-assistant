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
import net.cassite.hottapcassistant.component.cooldown.WeaponSpecialInfo;
import net.cassite.hottapcassistant.component.cooldown.WithDesc;
import net.cassite.hottapcassistant.data.*;
import net.cassite.hottapcassistant.data.matrix.LinYeMatrix;
import net.cassite.hottapcassistant.data.relics.DiceRelics;
import net.cassite.hottapcassistant.data.relics.KaoEnTeRelics;
import net.cassite.hottapcassistant.data.simulacra.XingHuanSimulacra;
import net.cassite.hottapcassistant.data.weapon.*;
import net.cassite.hottapcassistant.entity.InputData;
import net.cassite.hottapcassistant.entity.Key;
import net.cassite.hottapcassistant.entity.KeyCode;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.*;

import java.util.*;

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

    // special info
    private WeaponSpecialInfo liuQuanCheXinCounter;
    private WeaponCoolDown yongDongCD;
    private WeaponSpecialInfo wanDaoHuiQiCounter;
    private WeaponCoolDown yingYueZhiJingBuffTimer;
    private WeaponCoolDown diceBuffTimer;
    private WeaponCoolDown kaoEnTeBuffTimer;
    private WeaponCoolDown bingFengZhiShiBuffTimer;
    private WeaponSpecialInfo siYeShiZiShotRemain;
    private WeaponSpecialInfo siYeShiZiDodgeRemain;
    private WeaponCoolDown shiZiZhuoShaoBuffTimer;
    private WeaponCoolDown opticalSpaceTimer;
    private WeaponCoolDown liZiZhuoShaoBuffTimer;
    private WeaponCoolDown burnSettleTimer;
    private WeaponCoolDown linYeMatrixBuffTimer;
    private WeaponCoolDown lingDuZhiZhenBeeTimer;
    private WeaponCoolDown xingHuanSimulacraTimer;

    private LinYeMatrix linYe2Matrix;

    private final Scale scale = new Scale(1, 1);
    private final int totalIndicatorCount;

    public CoolDownWindow(List<Weapon> weapons, Relics[] relics, Simulacra simulacra,
                          InputData weaponSkill, InputData[] melee, InputData[] evade,
                          InputData[] changeWeapons, InputData[] artifact,
                          InputData jump,
                          Runnable reset) {
        this.ctx = new WeaponContext(weapons, relics, simulacra);
        this.weaponSkill = weaponSkill;
        this.melee = melee;
        this.evade = evade;
        this.changeWeapons = changeWeapons;
        this.artifact = artifact;
        this.jump = jump;
        this.lastWeaponButtonDownTs = new long[weapons.size()];
        this.lastArtifactButtonDownTs = new long[artifact.length];

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

        var resetBtn = new ImageView(ImageManager.get().load("/images/icon/reloading.png"));
        resetBtn.setLayoutX(10 + (WeaponCoolDown.MAX_RADIUS - WeaponCoolDown.MIN_RADIUS) + 2);
        resetBtn.setLayoutY(10 + (WeaponCoolDown.MAX_RADIUS - WeaponCoolDown.MIN_RADIUS) + 2);
        resetBtn.setFitWidth(2 * (WeaponCoolDown.MIN_RADIUS - 2));
        resetBtn.setFitHeight(2 * (WeaponCoolDown.MIN_RADIUS - 2));
        resetBtn.setCursor(Cursor.HAND);
        resetBtn.setOnMouseClicked(e -> reset.run());
        group.getChildren().add(resetBtn);

        cds = new WeaponCoolDown[weapons.size()];
        for (var i = 0; i < weapons.size(); ++i) {
            cds[i] = new WeaponCoolDown(weapons.get(i).getImage(), weapons.get(i).getName());
            if (weapons.get(i) == ctx.current) {
                cds[i].setActive(true);
            }
        }
        var needBurnSettle = false;
        for (var w : weapons) {
            if (w instanceof LiuQuanCheXinWeapon lw) {
                liuQuanCheXinCounter = new WeaponSpecialInfo(lw.getImage(), I18n.get().buffName("liuQuanCheXinCounter"));
                liuQuanCheXinCounter.setOnMouseClicked(e -> lw.addCount(ctx));
                liuQuanCheXinCounter.setCursor(Cursor.HAND);
                yongDongCD = new WeaponCoolDown(Utils.getBuffImageFromClasspath("yong-dong"), I18n.get().buffName("yongDongCD"));
            } else if (w instanceof WanDaoWeapon wd) {
                if (ctx.resonanceInfo.sup()) {
                    wanDaoHuiQiCounter = new WeaponSpecialInfo(Utils.getBuffImageFromClasspath("hui-qi"), I18n.get().buffName("wanDaoHuiQiCounter"));
                    wanDaoHuiQiCounter.setOnMouseClicked(e -> wd.resetCount());
                    wanDaoHuiQiCounter.setCursor(Cursor.HAND);
                }
            } else if (w instanceof YingZhiWeapon) {
                yingYueZhiJingBuffTimer = new WeaponCoolDown(Utils.getBuffImageFromClasspath("ying-yue-zhi-jing"), I18n.get().buffName("yingYueZhiJingBuffTimer"));
            } else if (w instanceof BingFengZhiShiWeapon) {
                bingFengZhiShiBuffTimer = new WeaponCoolDown(w.getImage(), I18n.get().buffName("bingFengZhiShiBuffTimer"));
            } else if (w instanceof AbstractSiYeShiZiWeapon) {
                siYeShiZiShotRemain = new WeaponSpecialInfo(w.getImage(), I18n.get().buffName("siYeShiZiShotRemain"));
                siYeShiZiDodgeRemain = new WeaponSpecialInfo(Utils.getBuffImageFromClasspath("dodge"), I18n.get().buffName("siYeShiZiDodgeRemain"));
                opticalSpaceTimer = new WeaponCoolDown(Utils.getBuffImageFromClasspath("optical-space"), I18n.get().buffName("opticalSpaceTimer"));
                if (w instanceof BurnSiYeShiZiWeapon) {
                    shiZiZhuoShaoBuffTimer = new WeaponCoolDown(Utils.getBuffImageFromClasspath("shi-zi-zhuo-shao"), I18n.get().buffName("shiZiZhuoShaoBuffTimer"));
                }
            } else if (w instanceof ChiYanZuoLunWeapon) {
                liZiZhuoShaoBuffTimer = new WeaponCoolDown(Utils.getBuffImageFromClasspath("li-zi-zhuo-shao"), I18n.get().buffName("liZiZhuoShaoBuffTimer"));
            } else if (w instanceof SiPaKeWeapon) {
                needBurnSettle = true;
            } else if (w instanceof LingDuZhiZhenWeapon) {
                lingDuZhiZhenBeeTimer = new WeaponCoolDown(Utils.getBuffImageFromClasspath("bee"), I18n.get().buffName("lingDuZhiZhenBeeTimer"));
            }
        }
        if (needBurnSettle) {
            // check for burn source
            needBurnSettle = false;
            for (var w : weapons) {
                if (w instanceof BurnSiYeShiZiWeapon || w instanceof ChiYanZuoLunWeapon || w instanceof LingGuangWeapon) {
                    needBurnSettle = true;
                    break;
                }
            }
        }
        if (needBurnSettle) {
            burnSettleTimer = new WeaponCoolDown(Utils.getBuffImageFromClasspath("burn-settle"), I18n.get().buffName("burnSettleTimer"));
        } else {
            opticalSpaceTimer = null;
        }

        linYe2Matrix = null;
        for (var w : weapons) {
            Matrix linYe = null;
            for (var m : w.getMatrix()) {
                if (m instanceof LinYeMatrix) {
                    linYe = m;
                    break;
                }
            }
            if (linYe == null) {
                continue;
            }
            if (linYe.getEffectiveStars()[2] != -1) {
                linYe2Matrix = (LinYeMatrix) linYe;
                break;
            }
        }
        if (linYe2Matrix != null) {
            linYeMatrixBuffTimer = new WeaponCoolDown(linYe2Matrix.getImage(), I18n.get().buffName("linYe2MatrixBuffTimer"));
        }

        for (var r : relics) {
            if (r instanceof DiceRelics) {
                diceBuffTimer = new WeaponCoolDown(r.getImage(), I18n.get().buffName("diceBuffTimer"));
            } else if (r instanceof KaoEnTeRelics) {
                kaoEnTeBuffTimer = new WeaponCoolDown(r.getImage(), I18n.get().buffName("kaoEnTeBuffTimer"));
            }
        }

        if (simulacra instanceof XingHuanSimulacra && ctx.resonanceInfo.sup()) {
            xingHuanSimulacraTimer = new WeaponCoolDown(Utils.getBuffImageFromClasspath("xing-huan-simulacra"), I18n.get().buffName("xingHuanSimulacraTimer"));
        }

        var groups = new ArrayList<Group>(Arrays.asList(cds));
        if (liuQuanCheXinCounter != null) groups.add(liuQuanCheXinCounter);
        if (yongDongCD != null) groups.add(yongDongCD);
        if (wanDaoHuiQiCounter != null) groups.add(wanDaoHuiQiCounter);
        if (yingYueZhiJingBuffTimer != null) groups.add(yingYueZhiJingBuffTimer);
        if (bingFengZhiShiBuffTimer != null) groups.add(bingFengZhiShiBuffTimer);
        if (siYeShiZiShotRemain != null) groups.add(siYeShiZiShotRemain);
        if (siYeShiZiDodgeRemain != null) groups.add(siYeShiZiDodgeRemain);
        if (shiZiZhuoShaoBuffTimer != null) groups.add(shiZiZhuoShaoBuffTimer);
        if (liZiZhuoShaoBuffTimer != null) groups.add(liZiZhuoShaoBuffTimer);
        if (opticalSpaceTimer != null) groups.add(opticalSpaceTimer);
        if (burnSettleTimer != null) groups.add(burnSettleTimer);
        if (diceBuffTimer != null) groups.add(diceBuffTimer);
        if (kaoEnTeBuffTimer != null) groups.add(kaoEnTeBuffTimer);
        if (linYeMatrixBuffTimer != null) groups.add(linYeMatrixBuffTimer);
        if (lingDuZhiZhenBeeTimer != null) groups.add(lingDuZhiZhenBeeTimer);
        if (xingHuanSimulacraTimer != null) groups.add(xingHuanSimulacraTimer);

        totalIndicatorCount = groups.size();

        resizeWindow();
        scale.xProperty().addListener((ob, old, now) -> resizeWindow());

        var descLabel = new Text() {{
            FontManager.setFont(this, 24);
            setFill(Color.WHITE);
            setStrokeWidth(0.5);
            setStroke(Color.BLACK);
        }};
        descLabel.setLayoutY(10 + 2 * r + 2 + 25);
        for (var i = 0; i < groups.size(); ++i) {
            var n = groups.get(i);
            n.setLayoutX(10 + r + (2 * r + margin) * (i + 1));
            n.setLayoutY(10 + r);
            String desc;
            if (n instanceof WithDesc) {
                desc = ((WithDesc) n).desc();
            } else {
                desc = "";
            }
            n.setOnMouseEntered(e -> setTextForDescLabel(descLabel, desc));
            n.setOnMouseExited(e -> setTextForDescLabel(descLabel, ""));
            group.getChildren().add(n);
        }
        {
            var sep = new Line();
            sep.setStrokeWidth(2);
            sep.setStartX(0);
            sep.setStartY(r / 2);
            sep.setEndX(0);
            sep.setEndY(2 * r - r / 2);
            sep.setStroke(Color.GRAY);
            sep.setLayoutX(10 + 2 * r + margin - margin / 2);
            sep.setLayoutY(10);
            group.getChildren().add(sep);
        }
        if (groups.size() > 3) {
            var sep = new Line();
            sep.setStrokeWidth(2);
            sep.setStartX(0);
            sep.setStartY(r / 2);
            sep.setEndX(0);
            sep.setEndY(2 * r - r / 2);
            sep.setStroke(Color.GRAY);
            sep.setLayoutX(10 + (2 * r + margin) * 4 - margin / 2);
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

        ctx.start();
        timer.start();
    }

    private static final double margin = 5;
    private static final double r = WeaponCoolDown.MAX_RADIUS;

    private void resizeWindow() {
        setWidth((10 + (2 * r) * (totalIndicatorCount + 1) + margin * ((totalIndicatorCount + 1) - 1) + 10) * scale.getX());
        setHeight((10 + 2 * r + 40) * scale.getY());
    }

    private void setTextForDescLabel(Text descLabel, String s) {
        if (s.equals(descLabel.getText())) return;
        descLabel.setText(s);
        if (s.isBlank()) return;
        var bounds = Utils.calculateTextBounds(descLabel);
        double width = getWidth();
        width /= scale.getX();
        descLabel.setLayoutX(width / 2 - bounds.getWidth() / 2);
    }

    private final Set<KeyCode> keys = new HashSet<>();
    private final Set<MouseButton> btns = new HashSet<>();

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        var key = KeyCode.valueOf(e.getKeyCode());
        if (key == null) return;
        handlePressed(key, null);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        var key = KeyCode.valueOf(e.getKeyCode());
        if (key == null) return;
        if (keys.contains(key)) {
            handle(key, null);
            keys.remove(key);
        }
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent e) {
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
        ctx.switchWeapon(index, current - lastWeaponButtonDownTs[index] > 300);
        cds[index].setActive(true);
        for (var i = 0; i < cds.length; ++i) {
            if (i == index) continue;
            cds[i].setActive(false);
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
            if (w instanceof LiuQuanCheXinWeapon lw) {
                var counter = liuQuanCheXinCounter;
                if (counter != null) {
                    counter.setText(lw.getCount() + "");
                }
                var yd = yongDongCD;
                if (yd != null) {
                    var time = lw.getYongDongCD();
                    var total = lw.getTotalYongDongCD();
                    yd.setCoolDown(time);
                    yd.setAllCoolDown(time, total);
                }
            } else if (w instanceof WanDaoWeapon) {
                var counter = wanDaoHuiQiCounter;
                if (counter != null) {
                    counter.setText(((WanDaoWeapon) w).getCount() + "");
                }
            } else if (w instanceof YingZhiWeapon) {
                var yyzj = yingYueZhiJingBuffTimer;
                if (yyzj != null) {
                    var time = ((YingZhiWeapon) w).getFieldTime();
                    var total = ((YingZhiWeapon) w).getTotalFieldTime();
                    yyzj.setCoolDown(time);
                    yyzj.setAllCoolDown(time, total);
                }
            } else if (w instanceof BingFengZhiShiWeapon) {
                var bfzs = bingFengZhiShiBuffTimer;
                if (bfzs != null) {
                    var time = ((BingFengZhiShiWeapon) w).getBuffTime();
                    var total = ((BingFengZhiShiWeapon) w).getTotalBuffTime();
                    bfzs.setCoolDown(time);
                    bfzs.setAllCoolDown(time, total);
                }
            } else if (w instanceof AbstractSiYeShiZiWeapon sysz) {
                if (siYeShiZiShotRemain != null) {
                    siYeShiZiShotRemain.setText(sysz.getShotRemain() + "");
                }
                if (siYeShiZiDodgeRemain != null) {
                    siYeShiZiDodgeRemain.setText(sysz.getDodgeRemain() + "");
                }
                if (opticalSpaceTimer != null) {
                    var time = sysz.getOpticalSpaceTime();
                    var total = sysz.getTotalOpticalSpaceTime();
                    opticalSpaceTimer.setCoolDown(time);
                    opticalSpaceTimer.setAllCoolDown(time, total);
                }
                if (w instanceof BurnSiYeShiZiWeapon bsysz) {
                    var szzs = shiZiZhuoShaoBuffTimer;
                    if (szzs != null) {
                        var burn = bsysz.getBurnBuff();
                        var total = bsysz.getTotalBurnBuff();
                        szzs.setCoolDown(burn);
                        szzs.setAllCoolDown(burn, total);
                    }
                }
            } else if (w instanceof ChiYanZuoLunWeapon cyzl) {
                var lzzs = liZiZhuoShaoBuffTimer;
                if (lzzs != null) {
                    var burn = cyzl.getBurnBuff();
                    var total = cyzl.getTotalBurnBuff();
                    lzzs.setCoolDown(burn);
                    lzzs.setAllCoolDown(burn, total);
                }
            } else if (w instanceof LingDuZhiZhenWeapon ldzz) {
                var timer = lingDuZhiZhenBeeTimer;
                if (timer != null) {
                    var time = ldzz.getBeeTime();
                    var total = ldzz.getTotalBeeTime();
                    timer.setCoolDown(time);
                    timer.setAllCoolDown(time, total);
                }
            }
        }
        if (linYe2Matrix != null) {
            if (linYeMatrixBuffTimer != null) {
                var time = linYe2Matrix.getBuffTime();
                var total = linYe2Matrix.getTotalBuffTime();
                linYeMatrixBuffTimer.setCoolDown(time);
                linYeMatrixBuffTimer.setAllCoolDown(time, total);
            }
        }
        for (var r : ctx.relics) {
            if (r instanceof DiceRelics) {
                diceBuffTimer.setCoolDown(r.getTime());
                diceBuffTimer.setAllCoolDown(r.getAllTime());
            } else if (r instanceof KaoEnTeRelics) {
                kaoEnTeBuffTimer.setCoolDown(r.getTime());
                kaoEnTeBuffTimer.setAllCoolDown(r.getAllTime());
            }
        }
        if (ctx.simulacra instanceof XingHuanSimulacra xh) {
            var timer = xingHuanSimulacraTimer;
            if (timer != null) {
                var buffTime = xh.getBuffTime();
                var total = xh.getTotalBuffTime();
                timer.setCoolDown(buffTime);
                timer.setAllCoolDown(buffTime, total);
            }
        }
        if (burnSettleTimer != null) {
            var ctx = this.ctx.getBurnSettleContext();
            burnSettleTimer.setCoolDown(ctx.getCd());
            burnSettleTimer.setAllCoolDown(ctx.getCd(), ctx.getLastTotalCD());
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
}
