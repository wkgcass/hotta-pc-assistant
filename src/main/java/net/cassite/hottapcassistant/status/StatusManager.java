package net.cassite.hottapcassistant.status;

import io.vproxy.vfx.util.FXUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StatusManager {
    private static final StatusManager instance = new StatusManager();

    private StatusManager() {
    }

    public static StatusManager get() {
        return instance;
    }

    private final Map<Status, Status> allStatus = new ConcurrentHashMap<>();
    private final WeakHashMap<Runnable, Object> callbacks = new WeakHashMap<>();

    public void updateStatus(Status status) {
        allStatus.put(status, status);
        runCallback();
    }

    public void removeStatus(Status status) {
        var old = allStatus.remove(status);
        if (old == null) {
            return;
        }
        runCallback();
    }

    public void registerCallback(Runnable cb) {
        callbacks.put(cb, new Object());
    }

    public List<Status> getAllStatus() {
        var ls = new ArrayList<>(allStatus.values());
        ls.sort(Comparator.comparingInt((Status a) -> a.component.sortOrder).thenComparing(a -> a.componentName));
        return ls;
    }

    private void runCallback() {
        var cbs = new HashSet<>(callbacks.keySet());
        for (var cb : cbs) {
            FXUtils.runOnFX(cb);
        }
    }
}
