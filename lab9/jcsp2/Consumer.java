import org.jcsp.lang.CSProcess;
import org.jcsp.lang.One2OneChannelInt;

/**
 * Consumer class: reads ints from input channel, displays them,
 * then
 * terminates when a negative value is read.
 */
public class Consumer implements CSProcess {
    private final One2OneChannelInt in;
    private final One2OneChannelInt req;

    public Consumer(final One2OneChannelInt req, final
    One2OneChannelInt in) {
        this.req = req;
        this.in = in;
    } // constructor

    public void run() {
        int item;
        while (true) {
            req.out().write(0); // Request data - blocks until data is available
            item = in.in().read();
            if (item < 0)
                break;
            System.out.println(item);
        } // for
        System.out.println("Consumer ended.");
    } // run
} // class Consumer2
