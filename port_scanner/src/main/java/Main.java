import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    public static void main(String... args) throws ExecutionException {
        if (args.length != 2) {
            System.out.println("Specify scan range.");
            System.exit(1);
        }
        int lowerBound = Integer.parseInt(args[0]);
        int upperBound = Integer.parseInt(args[1]);
        if (lowerBound > upperBound) {
            System.out.println("Second args must be less than first.");
            System.exit(1);
        }
        ExecutorService executor = Executors.newFixedThreadPool(8);
        List<TcpListenerTask> tcpTasks = new ArrayList<>();
        List<UpdListenerTask> udpTasks = new ArrayList<>();
        for (int i = lowerBound; i <= upperBound; i++) {
            tcpTasks.add(new TcpListenerTask(i));
            udpTasks.add(new UpdListenerTask(i));
        }
        try {
            List<PortInfo> openedTcp = getOpenPorts(executor.invokeAll(tcpTasks), PortInfo.PortType.TCP);
            List<PortInfo> openedUdp = getOpenPorts(executor.invokeAll(udpTasks), PortInfo.PortType.UDP);
            System.out.format("TCP OPENED: %s\n", openedTcp);
            System.out.format("UDP OPENED: %s\n", openedUdp);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }

    private static List<PortInfo> getOpenPorts(
            List<Future<PortInfo>> portInfos,
            PortInfo.PortType portType) throws ExecutionException, InterruptedException {
        List<PortInfo> result = new ArrayList<>();
        for (Future<PortInfo> future : portInfos) {
            PortInfo portInfo = future.get();
            if (portInfo.portStatus == PortInfo.PortStatus.OPENED && portInfo.portType == portType) {
                result.add(portInfo);
            }
        }
        return result;
    }
}
