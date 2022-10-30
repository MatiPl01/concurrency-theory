public class Main {
    public static void main(String[] args) {
        int producerCount = 10;
        int consumerCount = 10;
        int bufferSize = 1;

        Counter counter = new Counter(bufferSize);

        for (int i = 0; i < producerCount; i++) {
            new Thread(new Producer(counter)).start();
        }

        for (int i = 0; i < consumerCount; i++) {
            new Thread(new Consumer(counter)).start();
        }
    }
}
