package cs455.overlay.wireformats;

public class TrafficSummary implements Protocol {

    int messageType = TRAFFIC_SUMMARY;
    String ipAddress;
    int portNumber;
    private int sendTracker;
    private int receiveTracker;
    private int relayTracker;
    private long sendSummation;
    private long receiveSummation;


}
