import org.jcsp.lang.One2OneChannel;

import java.util.List;

public class Producer extends Actor {
    private static int nextId = 0;
    protected static final String NAME = "Producer";

    private final int id;

    public Producer(List<One2OneChannel> inChannels,
                    List<One2OneChannel> outChannels) {
        super(inChannels, outChannels);
        this.id = nextId++;
    }

    @Override
    public String toString() {
        return NAME + " " + id;
    }

    @Override
    public void run() {
        System.out.println("Producer " + id + " started");

        while (true) {
            int bufferIdx = getRandomBufferIdx();

            // Request access to the specific buffer (it blocks consumer until
            outChannels.get(bufferIdx).out().write(RequestType.NEEDS_PRODUCE);
            RequestType response =
                    (RequestType) inChannels.get(bufferIdx).in().read();

            // Request consume if the buffer consume request was accepted
            if (response == RequestType.ACCEPT) {
                operationsCount++;
                outChannels.get(bufferIdx).out().write(RequestType.PRODUCE);
            }
        }
    }
}
