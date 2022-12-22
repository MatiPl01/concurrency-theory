import org.jcsp.lang.CSProcess;
import org.jcsp.lang.One2OneChannel;
import utils.MyRandom;

import java.util.List;

public abstract class Actor implements CSProcess {
    private static int nextId = 0;

    protected final List<One2OneChannel> outChannels;
    protected final List<One2OneChannel> inChannels;
    protected final One2OneChannel channelLB;
    protected final int id;

    public Actor(One2OneChannel channelLB,
                 List<One2OneChannel> inChannels,
                 List<One2OneChannel> outChannels) {
        id = nextId++;
        this.channelLB = channelLB;
        this.inChannels = inChannels;
        this.outChannels = outChannels;
    }

    public List<One2OneChannel> getOutChannels() {
        return outChannels;
    }

    public List<One2OneChannel> getInChannels() {
        return inChannels;
    }

    public int getRandomBufferIdx() {
        return MyRandom.randInt(outChannels.size());
    }
}
