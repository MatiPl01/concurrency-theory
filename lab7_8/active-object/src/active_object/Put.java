package active_object;

import java.util.List;

public class Put<T> implements MethodRequest {
    private final Servant<T> servant;
    private final MessageFuture<Void> messageFuture;
    private final List<T> messages;

    Put(Servant<T> servant, MessageFuture<Void> messageFuture, List<T> messages) {
        this.servant = servant;
        this.messageFuture = messageFuture;
        this.messages = messages;
    }

    @Override
    public boolean isGuarded() {
        // Allow put calls only when the queue has enough empty slots
        return !servant.hasAtLeastEmpty(messages.size());
    }

    @Override
    public void call() {
        servant.put(messages);
        messageFuture.complete();
    }
}
