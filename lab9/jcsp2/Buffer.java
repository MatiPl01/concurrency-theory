import org.jcsp.lang.Alternative;
import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Guard;
import org.jcsp.lang.One2OneChannelInt;

/**
 * Buffer class: Manages communication between Producer2
 * and Consumer2 classes.
 */
public class Buffer implements CSProcess {
    private final One2OneChannelInt[] in; // Input from Producer
    private final One2OneChannelInt[] req; // Request for data from Consumer
    private final One2OneChannelInt[] out; // Output to Consumer
    // The buffer itself
    private final int[] buffer = new int[10];
    // Subscripts for buffer
    int hd = -1;
    int tl = -1;

    public Buffer(final One2OneChannelInt[] in, final
    One2OneChannelInt[] req, final One2OneChannelInt[] out) {
        this.in = in;
        this.req = req;
        this.out = out;
    } // constructor

    public void run() {
        final Guard[] guards = { in[0].in(), in[1].in(), req[0].in(), req[1].in() };
        final Alternative alt = new Alternative(guards);
        int countdown = 4; // Number of processes running
        while (countdown > 0) {
            int index = alt.select();
            switch (index) {
                case 0:
                case 1: // A Producer is ready to send
                    if (hd < tl + 11) // Space available
                    {
                        int item = in[index].in().read();
                        if (item < 0)
                            countdown--;
                        else {
                            hd++;
                            buffer[hd % buffer.length] = item;
                        }
                    }
                    break;
                case 2:
                case 3: // A Consumer is ready to read
                    if (tl < hd) // Item(s) available
                    {
                        req[index - 2].in().read(); // Read and discard request
                        tl++;
                        int item = buffer[tl % buffer.length];
                        out[index - 2].out().write(item);
                    } else if (countdown <= 2) // Signal consumer to end
                {
                    req[index - 2].in().read(); // Read and discard request
                    out[index - 2].out().write(-1); // Signal end
                    countdown--;
                }
                break;
            } // switch
        } // while
        System.out.println("Buffer ended.");
    } // run
} // class Buffer
