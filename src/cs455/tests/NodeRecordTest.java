package cs455.tests;

import cs455.overlay.node.NodeRecord;

import java.net.Socket;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class NodeRecordTest {

    private Socket testSocket = new Socket();
    private Integer[] intList = {2, 5, 8, 11};
    private NodeRecord[] testNodes = {
            new NodeRecord("localhost", 1234, testSocket),
            new NodeRecord("localhost", 2345, testSocket),
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

    private Set<Integer> randomInts = new HashSet<>();
    private List<NodeRecord> testNodeRecords = new ArrayList<>();

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
        currentNode.decrementNeededConnections();

        nextNode.decrementNeededConnections();
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
                System.out.println(currentRecord.getNumberOfConnections() + ":" + currentRecord.getConnectionsNeeded() +
                        " | "+ nextRecord.getNumberOfConnections() + ":" + nextRecord.getConnectionsNeeded());
            } catch (IndexOutOfBoundsException e) {
                NodeRecord firstRecord = testNodeRecords.get(0);
                NodeRecord lastRecord = testNodeRecords.get(testNodeRecords.size()-1);
                lastRecord.addNodeToConnectTo(firstRecord);
                updateConnections(lastRecord, firstRecord);
                System.out.println(lastRecord.getNumberOfConnections() + ":" + lastRecord.getConnectionsNeeded() +
                        " | "+ firstRecord.getNumberOfConnections() + ":" + firstRecord.getConnectionsNeeded());
            }
        }
    }

    private void setRandomInts() {

    }

    public static void main(String[] args) {
        NodeRecordTest nodeRecordTest = new NodeRecordTest();
//        nodeRecordTest.testElementRetrieve();
//        nodeRecordTest.addTestNodes();
//        nodeRecordTest.testNodeRecordModify();
        nodeRecordTest.setRandomInts();
    }
}