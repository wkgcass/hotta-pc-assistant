package net.cassite.hottapcassistant.data;

public abstract class AbstractWithThreadStartStop implements WithThreadStartStop {
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
}
