package net.cassite.hottapcassistant.util;

import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;

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
    private final AtomicBoolean prioritySet = new AtomicBoolean(false);

    private void setPriority() {
        if (!prioritySet.compareAndSet(false, true)) {
            return;
        }
        var p = Kernel32.INSTANCE.GetCurrentProcess();
        Kernel32.INSTANCE.SetPriorityClass(p, Kernel32.REALTIME_PRIORITY_CLASS);
    }

    private interface Kernel32 extends com.sun.jna.platform.win32.Kernel32 {
        Kernel32 INSTANCE = Native.load(Kernel32.class, W32APIOptions.UNICODE_OPTIONS);
        DWORD REALTIME_PRIORITY_CLASS = new DWORD(0x00000100);

        HANDLE GetCurrentProcess();

        @SuppressWarnings("UnusedReturnValue")
        boolean SetPriorityClass(HANDLE hProcess, DWORD dwPriorityClass);
    }

    public void begin() {
        if (!running.compareAndSet(false, true)) {
            return;
        }
        setPriority();
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
