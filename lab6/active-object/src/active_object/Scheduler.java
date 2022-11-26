package active_object;

import java.util.LinkedList;
import java.util.Queue;

public class Scheduler<T> extends Thread {
    private final Servant<T> servant;
    private final ActivationQueue activationQueue = new ActivationQueue();
    private final Queue<MethodRequest> waitingQueue = new LinkedList<>();

    public Scheduler(int bufferSize) {
        this.servant = new Servant<>(bufferSize);
    }

    Servant<T> getServant() {
        return servant;
    }

    void enqueue(MethodRequest methodRequest) throws InterruptedException {
        activationQueue.enqueue(methodRequest);
    }

    // TODO - rework this function to ensure that there is no starvation
    void dispatch() throws InterruptedException {
        System.out.println("Waiting queue:    " + waitingQueue.size());
        System.out.println("Activation queue: " + activationQueue.size());
        // Try to execute the first message request from the waiting queue
        if (waitingQueue.peek() != null && !waitingQueue.peek().isGuarded()) {
            waitingQueue.poll().call();
        } else {
            MethodRequest request = activationQueue.dequeue();
            if (request.isGuarded()) {
                waitingQueue.add(request);
            } else {
                request.call();
            }
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
