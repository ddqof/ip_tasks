import listener.TcpListenerThread;
import listener.UpdListenerThread;

import java.net.SocketException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String... args) throws ExecutionException, SocketException, InterruptedException {

        if (args.length != 3) {
            System.out.println("Specify host and then scan range.");
            System.exit(1);
        }
        String host = args[0];
        int lowerBound = Integer.parseInt(args[1]);
        int upperBound = Integer.parseInt(args[2]);
        if (lowerBound > upperBound) {
            System.out.println("Range start must be less than end.");
            System.exit(1);
        }
        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        TcpListenerThread tcpThread = new TcpListenerThread(host, lowerBound, upperBound);
        UpdListenerThread updThread = new UpdListenerThread(host, lowerBound, upperBound);
        threadPool.execute(tcpThread);
        threadPool.execute(updThread);

        threadPool.shutdown();
        threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

        System.out.format("TCP OPENED: %s\n", tcpThread.listenedPorts);
        System.out.format("UDP OPENED: %s\n", updThread.listenedPorts);
    }
}
