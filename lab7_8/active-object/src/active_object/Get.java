package active_object;

public class Get<T> implements MethodRequest {
    private final Servant<T> servant;
    private final MessageFuture<T> messageFuture;
    int count;

    Get(Servant<T> servant, MessageFuture<T> messageFuture, int count) {
        this.servant = servant;
        this.messageFuture = messageFuture;
        this.count = count; // Number of messages to get
    }

    @Override
    public boolean isGuarded() {
        // Allow get calls only when there are enough elements in
        // the servant queue
        return !servant.hasAtLeastElements(count);
    }

    @Override
    public void call() {
        messageFuture.setMessages(servant.get(count));
        messageFuture.complete();
    }
}
