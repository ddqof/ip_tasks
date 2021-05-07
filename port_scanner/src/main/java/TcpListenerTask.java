import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.concurrent.Callable;

public class TcpListenerTask implements Callable<PortInfo>  {
    private final int portNumber;

    public TcpListenerTask(int portNumber) {
        this.portNumber = portNumber;
    }

    @Override
    public PortInfo call() {
        try (ServerSocket tcp = new ServerSocket(portNumber)) {
            return new PortInfo(portNumber);
        } catch (IOException e) {
            return new PortInfo(portNumber, PortInfo.PortType.TCP);
        }
    }
}
