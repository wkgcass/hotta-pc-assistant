package net.cassite.hottapcassistant.util;

import java.util.concurrent.atomic.AtomicBoolean;

public class StressWorkers {
    private static final StressWorkers INST = new StressWorkers();

    private StressWorkers() {
    }

    public static StressWorkers get() {
        return INST;
    }

    private final AtomicBoolean running = new AtomicBoolean(false);
    private int cores = 0;

    public void begin() {
        if (!running.compareAndSet(false, true)) {
            return;
        }
        if (cores == 0) {
            cores = Runtime.getRuntime().availableProcessors();
        }
        for (int i = 0; i < cores; ++i) {
            new Thread(this::stress).start();
        }
    }

    public void end() {
        running.set(false);
    }

    private void stress() {
        //noinspection StatementWithEmptyBody
        while (running.get()) {
        }
    }
}
