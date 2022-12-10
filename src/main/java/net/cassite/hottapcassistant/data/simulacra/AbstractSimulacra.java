package net.cassite.hottapcassistant.data.simulacra;

import net.cassite.hottapcassistant.data.Simulacra;
import net.cassite.hottapcassistant.data.Weapon;
import net.cassite.hottapcassistant.data.WeaponContext;

public abstract class AbstractSimulacra implements Simulacra {
    protected final String name;
    private volatile Thread thread;

    protected AbstractSimulacra() {
        this.name = buildName();
    }

    @Override
    public final String getName() {
        return name;
    }

    abstract protected String buildName();

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

            threadTick(ts, delta);
        }
    }

    @SuppressWarnings("unused")
    protected void threadTick(long ts, long delta) {
    }

    @Override
    public void start() {
        if (thread != null) {
            throw new IllegalStateException();
        }
        var thread = new Thread(this::threadRun, "thread-" + getName());
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

    @Override
    public void alertSkillUsed(WeaponContext ctx, Weapon w) {
    }
}
