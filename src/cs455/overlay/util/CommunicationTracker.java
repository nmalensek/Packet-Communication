package cs455.overlay.util;

public class CommunicationTracker {

    private int sendTracker = 0;
    private int receiveTracker = 0;
    private int relayTracker = 0;
    private long sendSummation = 0;
    private long receiveSummation = 0;

    public synchronized void incrementSendTracker() {
        sendTracker++;
    }

    public synchronized void incrementReceiveTracker() {
        receiveTracker++;
    }

    public synchronized void incrementRelayTracker() {
        relayTracker++;
    }

    public synchronized void incrementSendSummation(int amountToAdd) {
        sendSummation = sendSummation + amountToAdd;
    }

    public synchronized void incrementReceiveSummation(int amountToAdd) {
        receiveSummation = receiveSummation + amountToAdd;
    }

    public void resetCounters() {
        int sendTracker = 0;
        int receiveTracker = 0;
        int relayTracker = 0;
        long sendSummation = 0;
        long receiveSummation = 0;
    }

}
