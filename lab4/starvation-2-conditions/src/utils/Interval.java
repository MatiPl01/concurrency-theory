package utils;

import java.util.concurrent.TimeUnit;

public class Interval {
    private final Runnable callback;
    private final int millis;
    private Thread thread;

    public Interval(Runnable callback, int millis) {
        this.callback = callback;
        this.millis = millis;
    }

    public void start() {
        if (thread != null) return;
        thread = new Thread(() -> {
            while (!thread.isInterrupted()) {
                try {
                    callback.run();
                    TimeUnit.MILLISECONDS.sleep(millis);
                } catch (InterruptedException e) {
                    clear();
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
    }

    public void clear() {
        if (thread != null && thread.isAlive()) thread.interrupt();
        thread = null;
    }
}
