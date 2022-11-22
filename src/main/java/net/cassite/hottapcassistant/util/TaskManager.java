package net.cassite.hottapcassistant.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskManager {
    private TaskManager() {
    }

    private static final ExecutorService threadPool = Executors.newFixedThreadPool(16);

    public static void execute(Runnable r) {
        threadPool.execute(r);
    }

    public static void terminate() {
        threadPool.shutdown();
    }
}
