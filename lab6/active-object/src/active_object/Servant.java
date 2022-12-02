package active_object;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Servant<T> {
    private final int size;
    private final Queue<T> messageQueue = new LinkedList<>();

    Servant(int size) {
        this.size = size;
    }

    public int size() {
        return size;
    }

    public int messageCount() {
        return messageQueue.size();
    }

    boolean hasAtLeastElements(int count) {
        return messageQueue.size() >= count;
    }

    boolean hasAtLeastEmpty(int count) {
        return size - messageQueue.size() >= count;
    }

    void put(List<T> messages) {
        if (!hasAtLeastEmpty(messages.size())) {
            throw new Error("Stack has less than " + messages.size() + " empty spaces");
        }
        messageQueue.addAll(messages);
    }

    List<T> get(int count) {
        if (!hasAtLeastElements(count)) {
            throw new Error("Stack has less than " + count + " elements");
        }
        List<T> messages = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            messages.add(messageQueue.poll());
        }
        return messages;
    }
}
