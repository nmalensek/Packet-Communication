package routing.overlay.util;

import routing.overlay.wireformats.TrafficSummary;

public class TrafficPrinter {

    private String traffic = "";
    private int totalMessagesSent;
    private int totalMessagesReceived;
    private long totalSendSummation;
    private long totalReceiveSummation;
    private int totalMessagesRelayed;

    public TrafficPrinter() {
        createOutputHeaders();
    }

    private void createOutputHeaders() {
        traffic += String.format("%-30s %-10s %-10s %-15s %-15s %-8s %n", "", "",
                "", "Summation", "Summation", "");
        traffic += String.format("%-30s %-10s %-10s %-15s %-15s %-8s %n", "", "Sent",
                "Received", "sent", "received", "Relayed");
        traffic += String.format("%-30s %-10s %-10s %-15s %-15s %-8s %n", "Node ID", "messages",
                "messages", "messages", "messages", "messages");
    }

    /**
     * Synchronized so the counts are accurate
     * @param summary individual node's traffic summary
     */
    public synchronized void processSummary(TrafficSummary summary) {
        String nodeID = summary.getIpAddress() + ":" + summary.getPortNumber();
        int sentMessages = summary.getSentMessages();
        totalMessagesSent = totalMessagesSent + sentMessages;

        long sentSummation = summary.getSendSummation();
        totalSendSummation = totalSendSummation + sentSummation;

        int receivedMessages = summary.getReceivedMessages();
        totalMessagesReceived = totalMessagesReceived + receivedMessages;

        long receiveSummation = summary.getReceiveSummation();
        totalReceiveSummation = totalReceiveSummation + receiveSummation;

        int relayedMessages = summary.getRelayedMessages();
        totalMessagesRelayed = totalMessagesRelayed + relayedMessages;

        traffic += String.format("%-30s %-10s %-10s %-15s %-15s %-8s %n",
                nodeID, sentMessages, receivedMessages, sentSummation, receiveSummation, relayedMessages);
    }

    public void addTotalsToString() {
        traffic += String.format("%-30s %-10s %-10s %-15s %-15s %-8s %n", "",
                totalMessagesSent, totalMessagesReceived,
                totalSendSummation, totalReceiveSummation, totalMessagesRelayed);
    }

    public void printTrafficSummary() {
        System.out.println(traffic);
    }

    public void resetTrafficStringAndCounters() {
        traffic = "";
        totalMessagesSent = 0;
        totalMessagesReceived = 0;
        totalSendSummation = 0;
        totalReceiveSummation = 0;
        totalMessagesRelayed = 0;

    }
}
