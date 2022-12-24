package net.cassite.hottapcassistant.util;

import javafx.scene.media.AudioClip;

public class AudioWrapper {
    private final AudioClip clip;
    private int count = 0;
    private boolean lastPlayed = false;

    public AudioWrapper(AudioClip clip) {
        this.clip = clip;
    }

    public int getCount() {
        return count;
    }

    public void play() {
        Utils.runOnFX(() -> {
            ++count;
            clip.play();
        });
    }

    public boolean isLastPlayed() {
        return false;
    }

    public void setLastPlayed(boolean lastPlayed) {
        this.lastPlayed = lastPlayed;
    }
}
