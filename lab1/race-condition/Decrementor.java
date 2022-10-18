public class Decrementor implements Runnable{
    int reps;

    public Decrementor(int reps) {
        this.reps = reps;
    }

    @Override
    public void run(){
        for (int i = 0; i < reps; i++) {
            Counter.decrement();
        }
    }
}
