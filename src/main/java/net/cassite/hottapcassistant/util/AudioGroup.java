package net.cassite.hottapcassistant.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

public class AudioGroup {
    private static final Comparator<AudioWrapper> comparator = (a, b) -> {
        if (a.isLastPlayed()) return 1;
        if (b.isLastPlayed()) return -1;
        return a.getCount() - b.getCount();
    };
    private final LinkedList<AudioWrapper> queue = new LinkedList<>();

    public AudioGroup(AudioWrapper[] clips) {
        for (var c : clips) {
            if (c == null) continue;
            queue.add(c);
        }
        queue.sort(comparator);
    }

    public void play() {
        Utils.runOnFX(this::playFX);
    }

    private void playFX() {
        if (queue.size() == 0) {
            return;
        }
        if (queue.size() == 1) {
            var audio = queue.peek();
            if (audio != null) {
                audio.play();
            }
            return;
        }
        var last = queue.peekLast();
        if (last.isLastPlayed()) {
            queue.removeLast();
            last.setLastPlayed(false);
        }

        var ls = new ArrayList<AudioWrapper>();
        var lastCount = -1;
        for (var a : queue) {
            if (lastCount == -1) {
                lastCount = a.getCount();
                ls.add(a);
            } else {
                if (lastCount != a.getCount()) {
                    break;
                }
                ls.add(a);
            }
        }
        var audio = ls.get(ThreadLocalRandom.current().nextInt(ls.size()));
        queue.remove(audio);
        audio.play();
        audio.setLastPlayed(true);
        queue.add(audio);
        queue.add(last);
        queue.sort(comparator);
    }
}
