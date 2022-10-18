public class Counter {
    private int count = 0;

    public int getCount() {
        return count;
    }

    public synchronized void produce() throws InterruptedException {
        while (count == 1) wait();
        count++;
        notify();
    }

    public synchronized void consume() throws InterruptedException {
        while (count == 0) wait();
        count--;
        notify();
    }
}
