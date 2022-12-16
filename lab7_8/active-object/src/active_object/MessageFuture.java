package active_object;

import utils.Counter;

import java.util.List;

public class MessageFuture<T> {
    private List<T> messages;
    private boolean isCompleted = false;

    public boolean isCompleted() {
        return isCompleted;
    }

    public List<T> getMessages() {
        return messages;
    }

    void setMessages(List<T> messages) {
        this.messages = messages;
    }

    void complete() {
        isCompleted = true;
        Counter.registerTaskDone();
    }
}
