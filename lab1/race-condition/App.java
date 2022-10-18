import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        int reps = 1000;
        int threadsCount = 100;

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < threadsCount; i++) {
            Thread incrementorThread = new Thread(new Incrementor(reps));
            Thread decrementorThread = new Thread(new Decrementor(reps));
            threads.add(incrementorThread);
            threads.add(decrementorThread);
        }

        threads.forEach(Thread::start);

        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Counter.getCount());
    }
}
