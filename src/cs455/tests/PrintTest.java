package cs455.tests;

public class PrintTest {

    private String traffic = "";
    private int totalSend;
    private int totalReceive;
    private long sendSummationTotal;
    private long receiveSummationTotal;
    private int totalRelay;

    public void setupString() {
       traffic += String.format("%-15s %-10s %-10s %-15s %-15s %-8s %n", "", "",
                "", "Summation", "Summation", "");
       traffic += String.format("%-15s %-10s %-10s %-15s %-15s %-8s %n", "", "Sent",
               "Received", "sent", "received", "Relayed");
        traffic += String.format("%-15s %-10s %-10s %-15s %-15s %-8s %n", "Node ID", "messages",
                "messages", "messages", "messages", "messages");
    }

    public void getTotals() {
        traffic += String.format("%-30s %-10s %-10s %-15s %-15s %-8s %n", "",
                totalSend, totalReceive,
                sendSummationTotal, receiveSummationTotal, totalRelay);
    }

    public void processSummary(String nodeID, int sentMessages, int receivedMessages, long sentSummation,
                               long receiveSummation, int relayedMessages) {
        traffic += String.format("%-15s %-10s %-10s %-15s %-15s %-8s %n",
                nodeID, sentMessages, receivedMessages, sentSummation, receiveSummation, relayedMessages);
        totalSend = totalSend + sentMessages;
        totalReceive = totalReceive + receivedMessages;
        sendSummationTotal = sendSummationTotal + sentSummation;
        receiveSummationTotal = receiveSummationTotal + receiveSummation;
        totalRelay = totalRelay + relayedMessages;
    }

    private void addDataToString() {
        processSummary("Node1:52661", 100000, 25440, -340040800604L,
                -144703367090L, 40445);
        processSummary("Node2:53534", 100000, 25440, -340040800604L,
                -144703367090L, 40445);
        processSummary("Node3:99887", 100000, 25440, -340040800604L,
                -144703367090L, 40445);
        processSummary("Node4:57761", 100000, 25440, -340040800604L,
                -144703367090L, 40445);
        processSummary("Node5:21661", 100000, 25440, -340040800604L,
                -144703367090L, 40445);
        processSummary("Node6:90661", 100000, 25440, -340040800604L,
                -144703367090L, 40445);
        processSummary("Node7:90661", 100000, 25440, -340040800604L,
                -144703367090L, 40445);
        processSummary("Node8:90661", 100000, 25440, -340040800604L,
                -144703367090L, 40445);
        processSummary("Node9:90661", 100000, 25440, -340040800604L,
                -144703367090L, 40445);
        processSummary("Node10:90661", 100000, 25440, -340040800604L,
                -144703367090L, 40445);
    }

    public void printTotalSummary() {
        System.out.println(traffic);
    }

    public static void main(String[] args) {
        PrintTest printTest = new PrintTest();
        printTest.setupString();
        printTest.addDataToString();
        printTest.getTotals();
        printTest.printTotalSummary();
    }

}
