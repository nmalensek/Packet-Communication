package cs455.overlay.util;

import cs455.overlay.wireformats.TrafficSummary;

public class TrafficPrinter {

    private TrafficSummary summary;
    private String traffic;
    private int numberOfSummariesReceived;

    public TrafficPrinter(TrafficSummary summary) {
        this.summary = summary;
    }

    public synchronized void processSummary() {
        String nodeID = summary.getIpAddress() + ":" + summary.getPortNumber();
        int sentMessages = summary.getSentMessages();
        long sentSummation = summary.getSendSummation();
        int receivedMessages = summary.getReceivedMessages();
        long receiveSummation = summary.getReceiveSummation();
        int relayedMessages = summary.getRelayedMessages();

        traffic += String.format("%-15s %-8s %-8s %-15s %-15s %-8s %n",
                nodeID, sentMessages, receivedMessages, sentSummation, receiveSummation, relayedMessages);
    }

    public void printTotalSummary() {
        System.out.println(traffic);
    }
}
