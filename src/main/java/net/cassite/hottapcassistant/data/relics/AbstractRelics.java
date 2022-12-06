package net.cassite.hottapcassistant.data.relics;

import javafx.scene.image.Image;
import net.cassite.hottapcassistant.data.Relics;

public abstract class AbstractRelics implements Relics {
    protected final String name;
    protected final Image image;
    protected int stars;
    private volatile Thread thread;

    protected AbstractRelics() {
        this.name = buildName();
        this.image = buildImage();
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final Image getImage() {
        return image;
    }

    abstract protected String buildName();

    abstract protected Image buildImage();

    @Override
    public void init(int stars) {
        this.stars = stars;
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
}
