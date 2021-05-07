public class PortInfo {
    public final int portNumber;
    public final PortStatus portStatus;
    public final PortType portType;

    public PortInfo(int portNumber) {
        this.portNumber = portNumber;
        this.portStatus = PortStatus.CLOSED;
        this.portType = PortType.NONE;
    }

    public PortInfo(int portNumber, PortType portType) {
        this.portNumber = portNumber;
        this.portStatus = PortStatus.OPENED;
        this.portType = portType;
    }

    public enum PortType {
        TCP, UDP, NONE
    }

    public enum PortStatus {
        CLOSED, OPENED
    }

    public String toStrValue() {
        if (portType == PortType.NONE) {
            return String.format("%d: %s", portNumber, portStatus);
        }
        return String.format("%s %d: %s", portType, portNumber, portStatus);
    }

    @Override
    public String toString() {
        return String.format("%d", portNumber);
    }
}
