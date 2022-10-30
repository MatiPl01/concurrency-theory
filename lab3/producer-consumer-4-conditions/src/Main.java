public class Main {
    public static void main(String[] args) {
        int producerCount = 10;
        int consumerCount = 10;
        int bufferSize = 10;

        Counter counter = new Counter(bufferSize);

        new Thread(new Producer(counter, bufferSize / 2)).start();

        for (int i = 0; i < producerCount - 1; i++) {
            new Thread(new Producer(counter, 1, bufferSize / 2)).start();
        }

        for (int i = 0; i < consumerCount; i++) {
            new Thread(new Consumer(counter, 1, bufferSize / 2)).start();
        }
    }
}
