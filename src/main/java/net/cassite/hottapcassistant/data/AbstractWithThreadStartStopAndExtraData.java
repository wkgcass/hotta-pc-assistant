package net.cassite.hottapcassistant.data;

import net.cassite.hottapcassistant.component.cooldown.WeaponCoolDown;
import net.cassite.hottapcassistant.component.cooldown.WeaponSpecialInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractWithThreadStartStopAndExtraData implements WithThreadStartStop, WithExtraData {
    private volatile Thread thread;

    private void threadRun() {
        long lastTs = System.currentTimeMillis();
        while (this.thread != null) {
            try {
                //noinspection BusyWait
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
            long ts = System.currentTimeMillis();
            long delta = ts - lastTs;
            lastTs = ts;

            mainThreadTick(ts, delta);
        }
    }

    @SuppressWarnings("unused")
    protected void threadTick(long ts, long delta) {
    }

    abstract protected String getThreadName();

    protected void mainThreadTick(long ts, long delta) {
        threadTick(ts, delta);
    }

    @Override
    public void start() {
        if (thread != null) {
            throw new IllegalStateException();
        }
        var thread = new Thread(this::threadRun, getThreadName());
        this.thread = thread;
        thread.start();
    }

    @Override
    public void stop() {
        var thread = this.thread;
        if (thread != null) {
            this.thread = null;
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException ignore) {
            }
        }
    }

    protected final List<WeaponCoolDown> extraIndicatorList = new ArrayList<>();
    protected final List<WeaponSpecialInfo> extraInfoList = new ArrayList<>();

    @Override
    public List<WeaponCoolDown> extraIndicators() {
        return Collections.unmodifiableList(extraIndicatorList);
    }

    @Override
    public List<WeaponSpecialInfo> extraInfo() {
        return Collections.unmodifiableList(extraInfoList);
    }

    @Override
    public void updateExtraData() {
    }
}
