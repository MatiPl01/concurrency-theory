import org.jcsp.lang.One2OneChannel;

import java.util.List;

public class Consumer extends Actor {
    public Consumer(One2OneChannel channelLB,
                    List<One2OneChannel> inChannels,
                    List<One2OneChannel> outChannels) {
        super(channelLB, inChannels, outChannels);
    }

    @Override
    public void run() {
        System.out.println("Consumer " + id + " started");
        while (true) {
            int bufferIdx = getRandomBufferIdx();

            // Request access to the specific buffer (it blocks consumer until
            outChannels.get(bufferIdx).out().write(RequestType.NEEDS_CONSUME);
            RequestType response =
                    (RequestType) inChannels.get(bufferIdx).in().read();

            // Request consume if the buffer consume request was accepted
            if (response == RequestType.ACCEPT) {
                outChannels.get(bufferIdx).out().write(RequestType.CONSUME);
            }
        }
    }
}
