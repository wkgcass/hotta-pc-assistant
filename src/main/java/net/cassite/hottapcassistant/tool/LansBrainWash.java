package net.cassite.hottapcassistant.tool;

import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import net.cassite.hottapcassistant.component.HPadding;
import net.cassite.hottapcassistant.component.VPadding;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.AudioManager;
import net.cassite.hottapcassistant.util.FontManager;
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
    protected Stage buildStage() {
        return new S();
    }

    @Override
    protected void terminate0() {
        var stage = (S) this.stage;
        if (stage != null) {
            stage.stop();
        }
    }

    private static class S extends Stage {
        private final SimpleBooleanProperty started = new SimpleBooleanProperty(false);
        private AudioClip[] audio = null;
        private int freq = 30;
        private int randTime = 1000;
        private Play play = null;

        public S() {
            setWidth(550);
            setHeight(300);
            var pane = new Pane();
            var scene = new Scene(pane);
            setScene(scene);

            var freqSlider = new Slider(0, 120, 30);
            freqSlider.setShowTickMarks(true);
            freqSlider.setShowTickLabels(true);
            freqSlider.setMajorTickUnit(10);
            freqSlider.setBlockIncrement(1);
            freqSlider.setPrefWidth(500);

            var randTimeSlider = new Slider(0, 3, 1);
            randTimeSlider.setShowTickMarks(true);
            randTimeSlider.setShowTickLabels(true);
            randTimeSlider.setMajorTickUnit(0.5);
            randTimeSlider.setBlockIncrement(0.1);
            randTimeSlider.setPrefWidth(500);

            var startBtn = new Button(I18n.get().brainWashLanStartBtn()) {{
                FontManager.setFont(this);
            }};
            startBtn.setOnAction(e -> start());
            var stopBtn = new Button(I18n.get().brainWashLanStopBtn()) {{
                FontManager.setFont(this);
                setDisable(true);
            }};
            stopBtn.setOnAction(e -> stop());

            freqSlider.valueProperty().addListener((ob, old, now) -> {
                if (now == null) return;
                if (now.intValue() == 0) {
                    startBtn.setDisable(true);
                } else {
                    startBtn.setDisable(started.get());
                }
                freq = now.intValue();
            });
            randTimeSlider.valueProperty().addListener((ob, old, now) -> {
                if (now == null) return;
                randTime = (int) (now.doubleValue() * 1000);
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
                    new Label(I18n.get().brainWashLanFreqSliderDesc()) {{
                        FontManager.setFont(this);
                    }},
                    new VPadding(5),
                    freqSlider,
                    new Separator() {{
                        setPadding(new Insets(10, 0, 10, 0));
                    }},
                    new Label(I18n.get().brainWashLanRandTimeSliderDesc()) {{
                        FontManager.setFont(this);
                    }},
                    randTimeSlider,
                    new VPadding(5),
                    new Separator() {{
                        setPadding(new Insets(10, 0, 10, 0));
                    }},
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
            play = new Play(audio, freq, randTime);
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
