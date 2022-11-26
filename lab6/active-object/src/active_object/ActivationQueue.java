package active_object;

import java.util.concurrent.LinkedBlockingQueue;

public class ActivationQueue {

    LinkedBlockingQueue<MethodRequest> requestQueue = new LinkedBlockingQueue<>();

    void enqueue(MethodRequest request) throws InterruptedException {
        requestQueue.put(request);
    }

    MethodRequest dequeue() throws InterruptedException {
        return requestQueue.take();
    }

    int size() {
        return requestQueue.size();
    }
}
