import org.jcsp.lang.CSProcess;
import org.jcsp.lang.One2OneChannelInt;

public class Producer implements CSProcess {
    private final One2OneChannelInt channel;
    private final int start;

    public Producer(final One2OneChannelInt out, int start) {
        channel = out;
        this.start = start;
    } // constructor

    public void run() {
        int item;
        for (int k = 0; k < 100; k++) {
            item = (int) (Math.random() * 100) + 1 + start;
            channel.out().write(item);
        } // for
        channel.out().write(-1);
        System.out.println("Producer" + start + " ended.");
    } // run
} // class Producer2
