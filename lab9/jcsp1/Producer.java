import org.jcsp.lang.CSProcess;
import org.jcsp.lang.One2OneChannelInt;

public class Producer implements CSProcess {
    private final One2OneChannelInt channel;

    public Producer(final One2OneChannelInt out) {
        channel = out;
    }

    @Override
    public void run() {
        int item = (int)(Math.random()*100)+1;
        channel.out().write(item);
    }
}
