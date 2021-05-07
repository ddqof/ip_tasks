import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.concurrent.Callable;

public class UpdListenerTask implements Callable<PortInfo> {
    private final int portNumber;

    public UpdListenerTask(int portNumber) {
        this.portNumber = portNumber;
    }

    @Override
    public PortInfo call() {
        try (DatagramSocket udp = new DatagramSocket(portNumber)) {
            return new PortInfo(portNumber);
        } catch (IOException e) {
            return new PortInfo(portNumber, PortInfo.PortType.UDP);
        }
    }
}
