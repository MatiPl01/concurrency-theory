import org.jcsp.lang.*;

import java.util.ArrayList;
import java.util.List;

public final class Main {
    private static final int PRODUCER_COUNT = 20;
    private static final int CONSUMER_COUNT = 10;
    private static final int BUFFER_SIZE = 10;
    private static final int BUFFER_COUNT = 4;

    public static void main(String[] args) {
        List<CSProcess> processes = new ArrayList<>();
        List<Actor> actors = new ArrayList<>();

        // Create consumers
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            List<One2OneChannel> inChannels = createOne2OneChannels(BUFFER_COUNT);
            List<One2OneChannel> outChannels = createOne2OneChannels(BUFFER_COUNT);
            One2OneChannel channelLB = Channel.one2one();
            Actor consumer = new Consumer(channelLB, inChannels, outChannels);
            actors.add(consumer);
            processes.add(consumer);
        }

        // Create producers
        for (int i = 0; i < PRODUCER_COUNT; i++) {
            List<One2OneChannel> inChannels = createOne2OneChannels(BUFFER_COUNT);
            List<One2OneChannel> outChannels = createOne2OneChannels(BUFFER_COUNT);
            One2OneChannel channelLB = Channel.one2one();
            Actor producer = new Producer(channelLB, inChannels, outChannels);
            actors.add(producer);
            processes.add(producer);
        }

        // Buffers
        for (int i = 0; i < BUFFER_COUNT; i++) {
            List<One2OneChannel> inChannels = new ArrayList<>();
            List<One2OneChannel> outChannels = new ArrayList<>();
            for (int j = 0; j < PRODUCER_COUNT + CONSUMER_COUNT; j++) {
                inChannels.add(actors.get(j).getOutChannels().get(i));
                outChannels.add(actors.get(j).getInChannels().get(i));
            }
            processes.add(new Buffer(inChannels, outChannels, BUFFER_SIZE));
        }

        Parallel parallel = new Parallel(processes.toArray(new CSProcess[0]));
        parallel.run();
    }

    private static List<One2OneChannel> createOne2OneChannels(int count) {
        List<One2OneChannel> channels = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            channels.add(Channel.one2one());
        }
        return channels;
    }
}
