import org.jcsp.lang.CSProcess;
import org.jcsp.lang.One2OneChannel;
import utils.MyRandom;

import java.util.List;

public abstract class Actor implements CSProcess, OperationsCounter {
    protected final List<One2OneChannel> outChannels;
    protected final List<One2OneChannel> inChannels;

    protected int operationsCount = 0;

    public Actor(List<One2OneChannel> inChannels,
                 List<One2OneChannel> outChannels) {
        this.inChannels = inChannels;
        this.outChannels = outChannels;
    }

    @Override
    public long getOperationsCount() {
        return operationsCount;
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
