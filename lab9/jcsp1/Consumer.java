import org.jcsp.lang.CSProcess;
import org.jcsp.lang.One2OneChannelInt;

public class Consumer implements CSProcess {
    private final One2OneChannelInt channel;

    public Consumer(final One2OneChannelInt in) {
        channel = in;
    }

    @Override
    public void run() {
        int item = channel.in().read();
        System.out.println(item);
    }
}
