package active_object;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Scheduler<T> extends Thread {
    private final Servant<T> servant;
    private final BlockingQueue<MethodRequest> activationQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<MethodRequest> waitingQueue = new LinkedBlockingQueue<>();

    public Scheduler(int bufferSize) {
        this.servant = new Servant<>(bufferSize);
    }

    Servant<T> getServant() {
        return servant;
    }

    void enqueue(MethodRequest methodRequest) throws InterruptedException {
        activationQueue.put(methodRequest);
    }

    private void dispatch() throws InterruptedException {
        // Try to execute the first message request from the waiting queue
        if (!waitingQueue.isEmpty() && !waitingQueue.peek().isGuarded()) {
            waitingQueue.take().call();
        }
        if (!activationQueue.isEmpty()) {
            MethodRequest request = activationQueue.take();
            if (request.isGuarded()) waitingQueue.add(request);
            else request.call();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                this.dispatch();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
