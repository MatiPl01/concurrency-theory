import org.jcsp.lang.Alternative;
import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Guard;
import org.jcsp.lang.One2OneChannel;

import java.util.List;

public class Buffer implements CSProcess {
    private static final String NAME = "Buffer";
    private static int nextId = 0;

    private final List<One2OneChannel> outChannels;
    private final List<One2OneChannel> inChannels;
    private final int id;
    private final int size;
    private int bufferFill;
    private Alternative alternative;

    private int operationsCount = 0;

    public Buffer(List<One2OneChannel> inChannels,
                  List<One2OneChannel> outChannels,
                  int size) {
        id = nextId++;
        this.outChannels = outChannels;
        this.inChannels = inChannels;
        this.size = size;
        this.bufferFill = 0;
    }

    public int getOperationsCount() {
        return operationsCount;
    }

    @Override
    public String toString() {
        return NAME + " " + id;
    }

    @Override
    public void run() {
        final Guard[] guards = new Guard[inChannels.size()];
        for (int i = 0; i < inChannels.size(); i++) {
            guards[i] = inChannels.get(i).in();
        }
        alternative = new Alternative(guards);

        while (true) serveRequest();
    }

    private void serveRequest() {
        int channelIdx = alternative.fairSelect();
        RequestType request =
                (RequestType) inChannels.get(channelIdx).in().read();

        switch (request) {
            case NEEDS_CONSUME -> handleConsumeRequest(channelIdx);
            case NEEDS_PRODUCE -> handleProduceRequest(channelIdx);
        }
    }

    private void handleConsumeRequest(int channelIdx) {
        operationsCount++;
        if (bufferFill > 0) {
            // Accept consumption request if buffer has enough items
            outChannels.get(channelIdx).out().write(RequestType.ACCEPT);
            // Handle client's consumption request after acceptance
            RequestType clientRequest =
                    (RequestType) inChannels.get(channelIdx).in().read();
            if (clientRequest == RequestType.CONSUME) bufferFill--;
        } else {
            outChannels.get(channelIdx).out().write(RequestType.REJECT);
        }
    }

    private void handleProduceRequest(int channelIdx) {
        operationsCount++;
        if (bufferFill < size) {
            // Accept production request if buffer has enough items
            outChannels.get(channelIdx).out().write(RequestType.ACCEPT);
            // Handle client's production request after acceptance
            RequestType clientRequest =
                    (RequestType) inChannels.get(channelIdx).in().read();
            if (clientRequest == RequestType.PRODUCE) bufferFill++;
        } else {
            outChannels.get(channelIdx).out().write(RequestType.REJECT);
        }
    }
}
