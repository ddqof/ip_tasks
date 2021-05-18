package listener;

public class PortInfo {
    public final int portNumber;
    public final Protocol protocol;


    public PortInfo(int portNumber, Protocol protocol) {
        this.portNumber = portNumber;
        this.protocol = protocol;
    }

    public enum Protocol {
        HTTP, SMTP, POP3, DNS
    }

    public String toStrValue() {
        return String.format("%s: %d", protocol, portNumber);
    }

    @Override
    public String toString() {
        return String.format("%d", portNumber);
    }
}
