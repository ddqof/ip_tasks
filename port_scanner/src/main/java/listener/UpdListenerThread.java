package listener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class UpdListenerThread extends AbstractListenerThread{
    public final List<Integer> listenedPorts = new ArrayList<>();

    public UpdListenerThread(String host, int from, int to) throws SocketException {
        super(host, from, to);
    }

    @Override
    public void run() {
        for (int port = from; port <= to; port++) {
            try (DatagramSocket socket = new DatagramSocket()){
                socket.setSoTimeout(timeout);
                socket.bind(new InetSocketAddress(host, port));
                socket.connect(InetAddress.getByName(host), port);
                byte[] bytes = "testMessage".getBytes(StandardCharsets.UTF_8);
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
                socket.send(packet);
                socket.receive(packet);
                listenedPorts.add(port);
            } catch (IOException ignored) {
            }
        }
    }
}
