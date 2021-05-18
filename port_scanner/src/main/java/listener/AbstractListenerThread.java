package listener;


public abstract class AbstractListenerThread extends Thread {
    protected final int from;
    protected final int to;
    protected final String host;
    protected final int timeout = 50;

    public AbstractListenerThread(String host, int from, int to) {
        this.host = host;
        this.from = from;
        this.to = to;
    }
}
