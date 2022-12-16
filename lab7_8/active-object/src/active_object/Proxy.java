package active_object;

import java.util.List;

public class Proxy<T> {
    private final Scheduler<T> scheduler;
    private final Servant<T> servant;

    public Proxy(Scheduler<T> scheduler) {
        this.scheduler = scheduler;
        servant = scheduler.getServant();
    }

    public MessageFuture<Void> put(List<T> messages) throws InterruptedException {
        MessageFuture<Void> result = new MessageFuture<>();
        MethodRequest methodRequest = new Put<>(servant, result, messages);
        scheduler.enqueue(methodRequest);
        return result;
    }

    public MessageFuture<T> get(int count) throws InterruptedException {
        MessageFuture<T> result = new MessageFuture<>();
        MethodRequest methodRequest = new Get<>(servant, result, count);
        scheduler.enqueue(methodRequest);
        return result;
    }
}
