package routing.overlay.tests;

import routing.overlay.node.NodeRecord;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class NodeRecordTest {

    private Socket testSocket = new Socket();
    private Integer[] intList = {2, 5, 8, 11};

    private NodeRecord[] testNodes = {
            new NodeRecord("localhost", 1234, testSocket),
            new NodeRecord("localhost", 3456, testSocket),
            new NodeRecord("localhost", 3446, testSocket),
            new NodeRecord("localhost", 3474, testSocket),
            new NodeRecord("localhost", 2534, testSocket),
            new NodeRecord("localhost", 1678, testSocket),
            new NodeRecord("localhost", 3567, testSocket),
            new NodeRecord("localhost", 4536, testSocket),
            new NodeRecord("localhost", 7445, testSocket),
            new NodeRecord("localhost", 4578, testSocket),
    };

    private static String splitterTest = "host1:1234\nhost2:2345\nhost3:4567\nhost4:1111";

    private Set<Integer> randomInts = new HashSet<>();
    private List<NodeRecord> testNodeRecords = new ArrayList<>();

    public NodeRecordTest() throws IOException {
    }

    public void testElementRetrieve() {
        List<Integer> testList = new ArrayList<>();
        for(Integer i : intList) {
            testList.add(i);
            System.out.print(i + ", ");
        }
        System.out.println("");
        System.out.print("Output: ");
        for (ListIterator<Integer> integerIterator = testList.listIterator(); integerIterator.hasNext(); ) {
            try {
                Integer integer = integerIterator.next();
                int currentPosition = testList.indexOf(integer);
                int newInt = integer + testList.get(currentPosition + 1);
                System.out.print(newInt + ", ");
            } catch (IndexOutOfBoundsException e) {
                Integer lastRecord = testList.get(testList.size()-1);
                int lastPlusFirst = lastRecord + testList.get(0);
                System.out.println(lastPlusFirst);
            }

        }
        System.out.println("\nexpected output: 7, 13, 19, 13");
    }

    public void addTestNodes() {
        for(NodeRecord record : testNodes) {
            testNodeRecords.add(record);
        }
    }

    private void updateConnections(NodeRecord currentNode, NodeRecord nextNode) {
        currentNode.incrementConnections();
        currentNode.decrementConnectionsToInitiate();

        nextNode.decrementConnectionsToInitiate();
        nextNode.incrementConnections();
    }

    public void testNodeRecordModify() {
        for (ListIterator<NodeRecord> recordListIterator = testNodeRecords.listIterator(); recordListIterator.hasNext(); ) {
            try {
                NodeRecord currentRecord = recordListIterator.next();
                int currentPosition = testNodeRecords.indexOf(currentRecord);
                NodeRecord nextRecord = testNodeRecords.get(currentPosition + 1);

                currentRecord.addNodeToConnectTo(nextRecord);
                updateConnections(currentRecord, nextRecord);
                System.out.println(currentRecord.getNumberOfConnections() + ":" + currentRecord.getConnectionsNeededToInitiate() +
                        " | "+ nextRecord.getNumberOfConnections() + ":" + nextRecord.getConnectionsNeededToInitiate());
            } catch (IndexOutOfBoundsException e) {
                NodeRecord firstRecord = testNodeRecords.get(0);
                NodeRecord lastRecord = testNodeRecords.get(testNodeRecords.size()-1);
                lastRecord.addNodeToConnectTo(firstRecord);
                updateConnections(lastRecord, firstRecord);
                System.out.println(lastRecord.getNumberOfConnections() + ":" + lastRecord.getConnectionsNeededToInitiate() +
                        " | "+ firstRecord.getNumberOfConnections() + ":" + firstRecord.getConnectionsNeededToInitiate());
            }
        }
    }

    private void testPrint() throws UnknownHostException {
        String host = Inet4Address.getLocalHost().getHostAddress();
        System.out.println(host);
        String[] line = host.split("/");
        String ipAddress = line[1];
        System.out.println(ipAddress);
    }

    private void testEquals() {
        System.out.println(testNodes[0].equals(testNodes[1]));
        System.out.println("Expected: true");
        System.out.println(testNodes[0].equals(testNodes[4]));
        System.out.println("Expected: false");
    }
    private void setRandomInts() {

    }

    private void splitNodeIDs(String stringToSplit) throws IOException {
        String[] splitString = stringToSplit.split("\\n");
        for(String nodeID : splitString) {
            String[] splitID = nodeID.split(":");
            String host = splitID[0];
            int port = Integer.parseInt(splitID[1]);
            System.out.println(host + " -- " + port);
        }
    }

    public static void main(String[] args) throws IOException {
        NodeRecordTest nodeRecordTest = new NodeRecordTest();
//        nodeRecordTest.testElementRetrieve();
//        nodeRecordTest.addTestNodes();
//        nodeRecordTest.testNodeRecordModify();
//        nodeRecordTest.setRandomInts();
//        nodeRecordTest.testEquals();
//        nodeRecordTest.splitNodeIDs(splitterTest);
//        nodeRecordTest.testPrint();
    }
}
