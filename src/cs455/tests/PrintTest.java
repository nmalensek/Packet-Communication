package cs455.tests;

public class PrintTest {

    private String traffic = "";

    public void setupString() {
       traffic += String.format("%-15s %-10s %-10s %-15s %-15s %-8s %n", "", "",
                "", "Summation", "Summation", "");
       traffic += String.format("%-15s %-10s %-10s %-15s %-15s %-8s %n", "", "Sent",
               "Received", "sent", "received", "Relayed");
        traffic += String.format("%-15s %-10s %-10s %-15s %-15s %-8s %n", "Node ID", "messages",
                "messages", "messages", "messages", "messages");
    }

    public void processSummary(String nodeID, int sentMessages, int receivedMessages, long sentSummation,
                               long receiveSummation, int relayedMessages) {
        traffic += String.format("%-15s %-10s %-10s %-15s %-15s %-8s %n",
                nodeID, sentMessages, receivedMessages, sentSummation, receiveSummation, relayedMessages);
    }

    private void addDataToString() {
        processSummary("Node1:52661", 25000, 25440, -340040800604L,
                -144703367090L, 40445);
        processSummary("Node2:53534", 25000, 25440, -340040800604L,
                -144703367090L, 40445);
        processSummary("Node3:99887", 25000, 25440, -340040800604L,
                -144703367090L, 40445);
        processSummary("Node4:57761", 25000, 25440, -340040800604L,
                -144703367090L, 40445);
        processSummary("Node5:21661", 25000, 25440, -340040800604L,
                -144703367090L, 40445);
        processSummary("Node6:90661", 25000, 25440, -340040800604L,
                -144703367090L, 40445);
    }

    public void printTotalSummary() {
        System.out.println(traffic);
    }

    public static void main(String[] args) {
        PrintTest printTest = new PrintTest();
        printTest.setupString();
        printTest.addDataToString();
        printTest.printTotalSummary();
    }

}
