package listener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TcpListenerThread extends AbstractListenerThread {
    public final List<Integer> listenedPorts = new ArrayList<>();
    protected final Map<PortInfo.Protocol, byte[]> packets = new HashMap();

    public TcpListenerThread(String host, int from, int to) throws SocketException {
        super(host, from, to);
    }

    @Override
    public void run() {
        for (int port = from; port <= to; port++) {
            try (Socket socket = new Socket()){
                socket.connect(new InetSocketAddress(host, port), timeout);
                socket.setSoTimeout(timeout);
                listenedPorts.add(port);
            } catch (IOException ignored) {
            }
        }
    }
}
