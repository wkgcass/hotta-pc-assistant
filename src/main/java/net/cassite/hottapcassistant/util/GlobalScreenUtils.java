package net.cassite.hottapcassistant.util;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;

import java.util.HashMap;
import java.util.Map;

public class GlobalScreenUtils {
    private GlobalScreenUtils() {
    }

    private static Map<Object, Integer> enableKeys = new HashMap<>();

    public static synchronized void enable(Object key) {
        var n = enableKeys.get(key);
        if (n == null) {
            n = 0;
        }
        n += 1;
        enableKeys.put(key, n);

        if (enableKeys.size() == 1 && n == 1) {
            Logger.debug("register GlobalScreen");
            try {
                GlobalScreen.registerNativeHook();
            } catch (NativeHookException e) {
                Logger.error("failed to run GlobalScreen.registerNativeHook", e);
            }
        }
    }

    public static synchronized void disable(Object key) {
        var n = enableKeys.get(key);
        if (n == null) {
            Logger.error("GlobalScreenUtils.disable is called with " + key + ", but it's not enabled with this key before");
            return;
        }
        n -= 1;
        if (n == 0) {
            enableKeys.remove(key);
            if (enableKeys.isEmpty()) {
                unregister();
            }
        } else {
            enableKeys.put(key, n);
        }
    }

    public static void unregister() {
        Logger.debug("unregister GlobalScreen");
        try {
            GlobalScreen.unregisterNativeHook();
            GlobalScreen.setEventDispatcher(null);
        } catch (NativeHookException e) {
            Logger.error("failed to run GlobalScreen.unregisterNativeHook", e);
        }
    }
}
