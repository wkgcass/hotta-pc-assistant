package net.cassite.hottapcassistant.tool;

import io.vproxy.vfx.manager.audio.AudioManager;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.layout.HPadding;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.scene.VScene;
import io.vproxy.vfx.ui.slider.VSlider;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import javafx.animation.AnimationTimer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Utils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class LansBrainWash extends AbstractTool implements Tool {
    @Override
    protected String buildName() {
        return I18n.get().toolName("lan's-brain-wash");
    }

    @Override
    protected Image buildIcon() {
        return Utils.getBuffImageFromClasspath("lan-simulacra");
    }

    @Override
    protected VScene buildScene() {
        return new S();
    }

    @Override
    protected void terminate0() {
        var scene = (S) this.scene;
        if (scene != null) {
            scene.stop();
        }
    }

    private static class S extends ToolScene {
        private final SimpleBooleanProperty started = new SimpleBooleanProperty(false);
        private AudioClip[] audio = null;
        private final IntegerProperty freq = new SimpleIntegerProperty(30);
        private final IntegerProperty randTime = new SimpleIntegerProperty(1000) {
            @Override
            public String toString() {
                String v = "" + get();
                if (v.length() < 2) {
                    v = "000" + v;
                } else if (v.length() < 3) {
                    v = "00" + v;
                } else if (v.length() < 4) {
                    v = "0" + v;
                }
                return v.charAt(0) + "." + v.substring(1);
            }
        };
        private Play play = null;

        public S() {
            enableAutoContentWidthHeight();

            var pane = new Pane();
            pane.setPrefWidth(550);
            pane.setPrefHeight(300);
            getContentPane().getChildren().add(pane);
            FXUtils.observeWidthHeightCenter(getContentPane(), pane);

            var freqSlider = new VSlider();
            freqSlider.setLength(500);
            freqSlider.setPercentage(freq.get() / 120d);

            var randTimeSlider = new VSlider();
            randTimeSlider.setLength(500);
            randTimeSlider.setPercentage(randTime.get() / 3000d);

            var startBtn = new FusionButton(I18n.get().brainWashLanStartBtn()) {{
                setPrefWidth(48);
                setPrefHeight(32);
            }};
            startBtn.setOnAction(e -> start());
            var stopBtn = new FusionButton(I18n.get().brainWashLanStopBtn()) {{
                setDisable(true);
                setPrefWidth(48);
                setPrefHeight(32);
            }};
            stopBtn.setOnAction(e -> stop());

            freqSlider.percentageProperty().addListener((ob, old, now) -> {
                if (now == null) return;
                if (now.doubleValue() == 0) {
                    startBtn.setDisable(true);
                } else {
                    startBtn.setDisable(started.get());
                }
                freq.set((int) (now.doubleValue() * 120));
            });
            randTimeSlider.percentageProperty().addListener((ob, old, now) -> {
                if (now == null) return;
                randTime.set((int) (now.doubleValue() * 3 * 1000));
            });
            started.addListener((ob, old, now) -> {
                if (now == null) return;
                startBtn.setDisable(now);
                stopBtn.setDisable(!now);
            });

            pane.getChildren().add(new HBox(
                new HPadding(25),
                new VBox(
                    new VPadding(20),
                    new ThemeLabel(I18n.get().brainWashLanFreqSliderDesc() + ": " + freq.get()) {{
                        freq.addListener((ob, old, now) -> {
                            if (now == null) return;
                            setText(I18n.get().brainWashLanFreqSliderDesc() + ": " + now);
                        });
                    }},
                    new VPadding(20),
                    freqSlider,
                    new VPadding(20),
                    new ThemeLabel(I18n.get().brainWashLanRandTimeSliderDesc() + ": " + randTime) {{
                        randTime.addListener((ob, old, now) -> {
                            if (now == null) return;
                            setText(I18n.get().brainWashLanRandTimeSliderDesc() + ": " + randTime);
                        });
                    }},
                    new VPadding(20),
                    randTimeSlider,
                    new VPadding(45),
                    new HBox(startBtn, new HPadding(10), stopBtn)
                )
            ));
        }

        private void start() {
            if (started.get()) {
                return;
            }
            if (audio == null || audio.length == 0) {
                List<AudioClip> ls = new ArrayList<>();
                ls.add(AudioManager.get().loadAudio("/audio/lan/ai-zhe-shi.wav"));
                ls.add(AudioManager.get().loadAudio("/audio/lan/shei-de-xin-yu.wav"));
                ls.add(AudioManager.get().loadAudio("/audio/lan/wo-hui-ji-zhu-zhe-yi-ke-de.wav"));
                ls.add(AudioManager.get().loadAudio("/audio/lan/you-fan-ying-le.wav"));
                ls = ls.stream().filter(Objects::nonNull).collect(Collectors.toList());
                //noinspection ToArrayCallWithZeroLengthArrayArgument
                audio = ls.toArray(new AudioClip[ls.size()]);
            }
            started.set(true);
            var play = this.play;
            if (play != null) {
                play.stop();
            }
            play = new Play(audio, freq.get(), randTime.get());
            play.start();
            this.play = play;
        }

        private void stop() {
            if (!started.get()) {
                return;
            }
            started.set(false);
            var play = this.play;
            this.play = null;
            if (play != null) {
                play.stop();
            }
        }

        private static class Play extends AnimationTimer {
            private final AudioClip[] audio;
            private final int freq;
            private final int randTime;

            private long lastTs = 0;
            private LinkedList<Integer> ls;

            private Play(AudioClip[] audio, int freq, int randTime) {
                this.audio = audio;
                this.freq = freq;
                this.randTime = randTime;
            }

            @Override
            public void handle(long now) {
                if (audio == null || audio.length == 0 || freq <= 0 || randTime < 0) {
                    return;
                }
                if (lastTs == 0) {
                    lastTs = now;
                    var remaining = new ArrayList<>(ls == null ? Collections.emptyList() : ls);
                    ls = new LinkedList<>();
                    int interval = 60_000 / freq;
                    for (int i = 0, total = (freq + randTime / interval); i < total; ++i) {
                        int t = interval * i;
                        if (randTime > 0) {
                            var r = ThreadLocalRandom.current().nextInt(randTime / 2) - randTime;
                            t += r;
                            if (t < 0) {
                                continue;
                            }
                        }
                        ls.add(t);
                    }
                    for (var r : remaining) {
                        if (r > 60) {
                            ls.add(r - 60);
                        }
                    }
                    ls.sort(Comparator.comparingInt(a -> a));
                    return;
                }
                var delta = (now - lastTs) / 1_000_000;
                if (delta > 60_000) {
                    lastTs = 0;
                    return;
                }
                while (true) {
                    var first = ls.peekFirst();
                    if (first == null) {
                        break;
                    }
                    if (first < delta) {
                        ls.removeFirst();
                        var idx = ThreadLocalRandom.current().nextInt(audio.length);
                        audio[idx].play();
                    } else {
                        break;
                    }
                }
            }
        }
    }
}
