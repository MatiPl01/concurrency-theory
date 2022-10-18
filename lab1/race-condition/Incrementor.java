public class Incrementor implements Runnable{
    int reps;

    public Incrementor(int reps) {
        this.reps = reps;
    }

    @Override
    public void run(){
        for (int i = 0; i < reps; i++) {
            Counter.increment();
        }
    }
}
