/**
 * Przygotuj się na ocenę na nast. zajęcia z wyjasnienia, w jaki
 * sposób dochodzi do zakleszczenia
 *
 * np. mamy 2 producentów, 1 konsumenta i 1-elementowy bufor,
 * - pierwszy do monitora zostaje wpuszczony wątek konsumenta i zawiesza się na pustym buforze,
 * - kolejny do monitora wchodzi producent i zapełnia bufor,
 * - zamiast zwolnić wątek konsumenta, kóry został wyrzucony z monitora,
 *   po wykonaniu notify i czeka razem z drugim producentem na wejście,
 *   do monitora wchodzi wątek producenta, który wiesza się na pełnym buforze,
 * - mamy 2 zawieszonych producentów i żaden z nich nie wywoła już notify, bo
 *   obaj producenci czekają na pełnym buforze, dlatego konsument nie zostaje
 *   wpuszczony do monitora (bo nie dostał notify)
 */
public class App {
    public static void main(String[] args) {
        int producerCount = 2;
        int consumerCount = 1;

        Counter counter = new Counter();

        for (int i = 0; i < producerCount; i++) {
            new Thread(new Producer(counter)).start();
        }

        for (int i = 0; i < consumerCount; i++) {
            new Thread(new Consumer(counter)).start();
        }
    }
}
