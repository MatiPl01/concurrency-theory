public class Consumer implements Runnable {
    private final Counter counter;

    public Consumer(Counter counter) {
        this.counter = counter;
    }

    @Override
    public void run() {
        while (true) {
            try {
                counter.consume();
                System.out.println("Consume: " + counter.getCount());
            } catch (InterruptedException e) {
               throw new RuntimeException(e);
            }
        }
    }
}
