package cs455.tests;

import cs455.overlay.node.NodeRecord;

import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class OverlayCreatorTest {

    private Socket testSocket = new Socket();
    private Map<String, NodeRecord> nodeMap = new ConcurrentHashMap<>();
    private int requiredConnections = 4;

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

    public void addTestNodes() {
        for(NodeRecord record : testNodes) {
            nodeMap.put(record.getHost() + ":" + record.getPort(), record);
        }
    }

    private void setNodeConnectionRequirement() {
        for (NodeRecord node : nodeMap.values()) {
            node.setNumberOfConnectionsNodeNeedsToInitiate(requiredConnections);
        }
    }

    private List<String> putKeysInList() {
        List<String> stringList = new LinkedList<>();
        for (String node : nodeMap.keySet()) {
            stringList.add(node);
        }
        return stringList;
    }

    public void connectToNodesInSequence() {
        List<String> sequentialList = new LinkedList<>(putKeysInList());
        for (ListIterator<String> recordListIterator = sequentialList.listIterator(); recordListIterator.hasNext(); ) {
            try {
                String currentRecord = recordListIterator.next();
                int currentPosition = sequentialList.indexOf(currentRecord);
                String nextRecord = sequentialList.get(currentPosition + 1);

                NodeRecord currentNode = nodeMap.get(currentRecord);
                NodeRecord nextNode = nodeMap.get(nextRecord);
                currentNode.addNodeToConnectTo(nextNode);
                updateConnections(currentNode, nextNode);
            } catch (IndexOutOfBoundsException e) {
                NodeRecord firstNode = nodeMap.get(sequentialList.get(0));
                NodeRecord lastNode = nodeMap.get(sequentialList.get(sequentialList.size()-1));
                lastNode.addNodeToConnectTo(firstNode);
                updateConnections(lastNode, firstNode);
            }
        }
    }

    private void connectToRandomNodes() {
        List<String> randomList = new LinkedList<>(putKeysInList());
        for (ListIterator<String> randomListIterator = randomList.listIterator(); randomListIterator.hasNext(); ) {
            String currentRecord = randomListIterator.next();
            NodeRecord currentNode = nodeMap.get(currentRecord);
            while (currentNode.getConnectionsNeededToInitiate() > 0) {
                int randomConnection = ThreadLocalRandom.current().nextInt(0, nodeMap.size());
                String randomKey = randomList.get(randomConnection);
                NodeRecord randomNode = nodeMap.get(randomKey);

                if(currentNode.equals(randomNode) || currentNode.getNodesToConnectToList().contains(randomNode)
                        || randomNode.getNodesToConnectToList().contains(currentNode)
                        || randomNode.getNumberOfConnections() == requiredConnections) {
                    //do not add and pick again
                } else {
                    currentNode.addNodeToConnectTo(randomNode);
                    updateConnections(currentNode, randomNode);
                }
            }
            currentNode.printNodesList();
        }
    }

    private void updateConnections(NodeRecord currentNode, NodeRecord nextNode) {
        currentNode.incrementConnections();
        currentNode.decrementConnectionsToInitiate();

        nextNode.incrementConnections();
        nextNode.decrementConnectionsToInitiate();
    }

    private void printSequentialConnections() {
        for(NodeRecord node : nodeMap.values()) {
            node.printNodesList();
        }
    }

    private void testOverlayConstruction() {
        addTestNodes();
        setNodeConnectionRequirement();
        connectToNodesInSequence();
        printSequentialConnections();
        connectToRandomNodes();
    }

    public static void main(String[] args) {
        OverlayCreatorTest overlayCreatorTest = new OverlayCreatorTest();
        overlayCreatorTest.testOverlayConstruction();
    }
}
