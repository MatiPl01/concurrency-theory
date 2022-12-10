package active_object;

import utils.ExpensiveComputation;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Scheduler<T> extends Thread {
    private final Servant<T> servant;
    private final int bufferWork;
    private final BlockingQueue<MethodRequest> activationQueue =
            new LinkedBlockingQueue<>();
    private final BlockingQueue<MethodRequest> waitingQueue =
            new LinkedBlockingQueue<>();

    public Scheduler(int bufferSize, int bufferWork) {
        this.servant = new Servant<>(bufferSize);
        this.bufferWork = bufferWork;
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
            // Otherwise, take the first request from the activation queue
        } else if (!activationQueue.isEmpty()) {
            MethodRequest request = activationQueue.take();
            // If there are still some requests waiting and the new request
            // is of the same kind as waiting requests, add the new request
            // to the waiting queue to prevent starvation
            // Also add the new request to the waiting queue if it is guarded
            if ((!waitingQueue.isEmpty() &&
                waitingQueue.peek().getClass().equals(request.getClass())) ||
                request.isGuarded()) {
                waitingQueue.add(request);
            } else {
                request.call();
            }
        }
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) this.dispatch();
            ExpensiveComputation.compute(bufferWork);
        } catch (InterruptedException e) {
            interrupt();
        }
    }
}
