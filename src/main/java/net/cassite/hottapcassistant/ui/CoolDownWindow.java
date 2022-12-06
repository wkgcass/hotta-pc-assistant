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
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.cassite.hottapcassistant.component.cooldown.WeaponCoolDown;
import net.cassite.hottapcassistant.component.cooldown.WeaponSpecialInfo;
import net.cassite.hottapcassistant.data.Relics;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponContext;
import net.cassite.hottapcassistant.data.relics.DiceRelics;
import net.cassite.hottapcassistant.data.relics.KaoEnTeRelics;
import net.cassite.hottapcassistant.data.weapon.*;
import net.cassite.hottapcassistant.entity.InputData;
import net.cassite.hottapcassistant.entity.Key;
import net.cassite.hottapcassistant.entity.KeyCode;
import net.cassite.hottapcassistant.util.DragHandler;
import net.cassite.hottapcassistant.util.GlobalScreenUtils;
import net.cassite.hottapcassistant.util.Utils;

import java.util.*;

public class CoolDownWindow extends Stage implements NativeKeyListener, NativeMouseListener {
    private static final InputData SpecialAttack = new InputData(new Key(KeyCode.BACKQUOTE));
    private final WeaponContext ctx;
    private final Relics[] relics;
    private final InputData weaponSkill;
    private final InputData[] melee;
    private final InputData[] evade;
    private final InputData[] changeWeapons;
    private final InputData[] artifact;
    private final AnimationTimer timer;
    private final WeaponCoolDown[] cds;

    // special info
    private WeaponSpecialInfo liuQuanCheXinCounter;
    private WeaponCoolDown yingYueZhiJingBuffTimer;
    private WeaponCoolDown diceBuffTimer;
    private WeaponCoolDown kaoEnTeBuffTimer;
    private WeaponCoolDown bingFengZhiShiBuffTimer;
    private WeaponSpecialInfo siYeShiZiShotRemain;
    private WeaponCoolDown shiZiZhuoShaoBuffTimer;
    private WeaponCoolDown liZiZhuoShaoBuffTimer;

    public CoolDownWindow(List<Weapon> weapons, Relics[] relics,
                          InputData weaponSkill, InputData[] melee, InputData[] evade,
                          InputData[] changeWeapons, InputData[] artifact) {
        this.ctx = new WeaponContext(weapons);
        this.relics = relics;
        this.weaponSkill = weaponSkill;
        this.melee = melee;
        this.evade = evade;
        this.changeWeapons = changeWeapons;
        this.artifact = artifact;

        GlobalScreenUtils.enable(this);
        GlobalScreen.addNativeKeyListener(this);
        GlobalScreen.addNativeMouseListener(this);

        var group = new Group();
        var scene = new Scene(group);
        scene.setFill(Color.TRANSPARENT);
        setScene(scene);
        initStyle(StageStyle.TRANSPARENT);
        setAlwaysOnTop(true);
        setResizable(false);
        centerOnScreen();
        setTitle("CoolDown Indicator");

        cds = new WeaponCoolDown[weapons.size()];
        for (var i = 0; i < weapons.size(); ++i) {
            cds[i] = new WeaponCoolDown(weapons.get(i).getImage());
            if (weapons.get(i) == ctx.current) {
                cds[i].setActive(true);
            }
        }
        for (var w : weapons) {
            if (w instanceof LiuQuanCheXinWeapon lw) {
                liuQuanCheXinCounter = new WeaponSpecialInfo(lw.getImage());
                liuQuanCheXinCounter.setOnMouseClicked(e -> lw.addCount(ctx));
                liuQuanCheXinCounter.setCursor(Cursor.HAND);
            } else if (w instanceof YingZhiWeapon) {
                yingYueZhiJingBuffTimer = new WeaponCoolDown(Utils.getBuffImageFromClasspath("ying-yue-zhi-jing"));
            } else if (w instanceof BingFengZhiShiWeapon) {
                bingFengZhiShiBuffTimer = new WeaponCoolDown(w.getImage());
            } else if (w instanceof AbstractSiYeShiZiWeapon) {
                siYeShiZiShotRemain = new WeaponSpecialInfo(w.getImage());
                if (w instanceof BurnSiYeShiZiWeapon) {
                    shiZiZhuoShaoBuffTimer = new WeaponCoolDown(Utils.getBuffImageFromClasspath("shi-zi-zhuo-shao"));
                }
            } else if (w instanceof ChiYanZuoLunWeapon) {
                liZiZhuoShaoBuffTimer = new WeaponCoolDown(Utils.getBuffImageFromClasspath("li-zi-zhuo-shao"));
            }
        }

        for (var r : relics) {
            if (r instanceof DiceRelics) {
                diceBuffTimer = new WeaponCoolDown(r.getImage());
            } else if (r instanceof KaoEnTeRelics) {
                kaoEnTeBuffTimer = new WeaponCoolDown(r.getImage());
            }
        }

        var groups = new ArrayList<Group>(Arrays.asList(cds));
        if (liuQuanCheXinCounter != null) groups.add(liuQuanCheXinCounter);
        if (yingYueZhiJingBuffTimer != null) groups.add(yingYueZhiJingBuffTimer);
        if (bingFengZhiShiBuffTimer != null) groups.add(bingFengZhiShiBuffTimer);
        if (siYeShiZiShotRemain != null) groups.add(siYeShiZiShotRemain);
        if (shiZiZhuoShaoBuffTimer != null) groups.add(shiZiZhuoShaoBuffTimer);
        if (liZiZhuoShaoBuffTimer != null) groups.add(liZiZhuoShaoBuffTimer);
        if (diceBuffTimer != null) groups.add(diceBuffTimer);
        if (kaoEnTeBuffTimer != null) groups.add(kaoEnTeBuffTimer);

        final double margin = 5;
        double r = WeaponCoolDown.MAX_RADIUS;
        setWidth(10 + (2 * r) * groups.size() + margin * (groups.size() - 1) + 10);
        setHeight(10 + 2 * r + 10);
        for (var i = 0; i < groups.size(); ++i) {
            groups.get(i).setLayoutX(10 + r + (2 * r + margin) * i);
            groups.get(i).setLayoutY(10 + r);
            group.getChildren().add(groups.get(i));
        }
        if (groups.size() > 3) {
            var sep = new Line();
            sep.setStrokeWidth(2);
            sep.setStartX(0);
            sep.setStartY(r / 2);
            sep.setEndX(0);
            sep.setEndY(2 * r - r / 2);
            sep.setStroke(Color.GRAY);
            sep.setLayoutX(10 + (2 * r + margin) * 3 - margin / 2);
            sep.setLayoutY(10);
            group.getChildren().add(sep);
        }

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
        for (var rr : relics) {
            rr.start();
        }
        timer.start();
    }

    private final Set<KeyCode> keys = new HashSet<>();
    private final Set<MouseButton> btns = new HashSet<>();

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        var key = KeyCode.valueOf(e.getKeyCode());
        if (key == null) return;
        if (keys.add(key)) {
            for (var input : melee) {
                if (input.matches(keys, btns, key, null)) {
                    lastAttackButtonDownTs = System.currentTimeMillis();
                    break;
                }
            }
        }
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
        if (btns.add(b)) {
            for (var input : melee) {
                if (input.matches(keys, btns, null, b)) {
                    lastAttackButtonDownTs = System.currentTimeMillis();
                    break;
                }
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
        ctx.switchWeapon(index);
        cds[index].setActive(true);
        for (var i = 0; i < cds.length; ++i) {
            if (i == index) continue;
            cds[i].setActive(false);
        }
    }

    private void useArtifact(int index) {
        if (relics[index] == null) return;
        relics[index].use(ctx);
    }

    private long lastAttackButtonDownTs;
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
    }

    private void updateUI() {
        for (var i = 0; i < ctx.weapons.size(); ++i) {
            var w = ctx.weapons.get(i);
            cds[i].setCoolDown(w.getCoolDown());
            cds[i].setAllCoolDown(w.getAllCoolDown());
            if (w instanceof LiuQuanCheXinWeapon) {
                var counter = liuQuanCheXinCounter;
                if (counter != null) {
                    counter.setText(((LiuQuanCheXinWeapon) w).getCount() + "");
                }
            } else if (w instanceof YingZhiWeapon) {
                var yyzj = yingYueZhiJingBuffTimer;
                if (yyzj != null) {
                    var time = ((YingZhiWeapon) w).getFieldTime();
                    var total = ((YingZhiWeapon) w).getTotalFieldTime();
                    yyzj.setCoolDown(time);
                    if (time == 0) yyzj.setAllCoolDown(null);
                    else yyzj.setAllCoolDown(new double[]{time / (double) total});
                }
            } else if (w instanceof BingFengZhiShiWeapon) {
                var bfzs = bingFengZhiShiBuffTimer;
                if (bfzs != null) {
                    var time = ((BingFengZhiShiWeapon) w).getBuffTime();
                    var total = ((BingFengZhiShiWeapon) w).getTotalBuffTime();
                    bfzs.setCoolDown(time);
                    if (time == 0) bfzs.setAllCoolDown(null);
                    else bfzs.setAllCoolDown(new double[]{time / (double) total});
                }
            } else if (w instanceof AbstractSiYeShiZiWeapon sysz) {
                if (siYeShiZiShotRemain != null) {
                    siYeShiZiShotRemain.setText(sysz.getShotRemain() + "");
                }
                if (w instanceof BurnSiYeShiZiWeapon bsysz) {
                    var szzs = shiZiZhuoShaoBuffTimer;
                    if (szzs != null) {
                        var burn = bsysz.getBurnBuff();
                        var total = bsysz.getTotalBurnBuff();
                        szzs.setCoolDown(burn);
                        if (burn == 0) szzs.setAllCoolDown(null);
                        else szzs.setAllCoolDown(new double[]{burn / (double) total});
                    }
                }
            } else if (w instanceof ChiYanZuoLunWeapon cyzl) {
                var lzzs = liZiZhuoShaoBuffTimer;
                if (lzzs != null) {
                    var burn = cyzl.getBurnBuff();
                    var total = cyzl.getTotalBurnBuff();
                    lzzs.setCoolDown(burn);
                    if (burn == 0) lzzs.setAllCoolDown(null);
                    else lzzs.setAllCoolDown(new double[]{burn / (double) total});
                }
            }
        }
        for (var r : relics) {
            if (r instanceof DiceRelics) {
                diceBuffTimer.setCoolDown(r.getTime());
                diceBuffTimer.setAllCoolDown(r.getAllTime());
            } else if (r instanceof KaoEnTeRelics) {
                kaoEnTeBuffTimer.setCoolDown(r.getTime());
                kaoEnTeBuffTimer.setAllCoolDown(r.getAllTime());
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
        for (var rr : relics) {
            rr.stop();
        }
        GlobalScreen.removeNativeKeyListener(this);
        GlobalScreen.removeNativeMouseListener(this);
        GlobalScreenUtils.disable(this);
    }
}
