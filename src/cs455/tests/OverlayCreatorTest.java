package cs455.tests;

import cs455.overlay.node.NodeRecord;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class OverlayCreatorTest {

    private static Socket testSocket;
    private Map<String, NodeRecord> nodeMap = new ConcurrentHashMap<>();
    private int requiredConnections = 4;
    private static NodeRecord[] testNodes;
    private int attempts;
    private boolean wasSuccessful = true;

    public OverlayCreatorTest() throws IOException {
    }

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

    public void connectToNodesInSequence(List<String> sequentialList) {
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

    private void connectToRandomNodes(List<String> randomList) {
        wasSuccessful = true;
        attempts = 0;
        for (ListIterator<String> randomListIterator = randomList.listIterator(); randomListIterator.hasNext(); ) {
                String currentRecord = randomListIterator.next();
                NodeRecord currentNode = nodeMap.get(currentRecord);
                while (currentNode.getConnectionsNeededToInitiate() > 0) {
                    if (attempts < 15) {
                        int randomConnection = ThreadLocalRandom.current().nextInt(0, nodeMap.size());
                        String randomKey = randomList.get(randomConnection);
                        NodeRecord randomNode = nodeMap.get(randomKey);

                        if(currentNode.equals(randomNode) || currentNode.getNodesToConnectToList().contains(randomNode)
                                || randomNode.getNodesToConnectToList().contains(currentNode)
                                || randomNode.getNumberOfConnections() == requiredConnections) {
                            ++attempts;
                            //do not add and pick again
                        } else {
                            currentNode.addNodeToConnectTo(randomNode);
                            updateConnections(currentNode, randomNode);
                            attempts = 0;
                        }
                    } else {
                        wasSuccessful = false;
                        break;
                    }
            }
            currentNode.printNodesList();
        }
    }

    private void reEstablishConnections() {
        System.out.println("Impossible to create overlay, starting over...");
        for (NodeRecord node : nodeMap.values()) {
            node.getNodesToConnectToList().clear();
            node.resetNumberOfConnections();
        }
        testOverlayConstruction();
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
        System.out.println("-----");
    }

    private void checkIfSuccessful() {
        while(!wasSuccessful) {
            reEstablishConnections();
        }
    }

    private void testOverlayConstruction() {
        setNodeConnectionRequirement();
        List<String> stringList = new LinkedList<>(putKeysInList());
        connectToNodesInSequence(stringList);
        printSequentialConnections();
        connectToRandomNodes(stringList);
        checkIfSuccessful();
    }

    public static void main(String[] args) throws IOException {
        OverlayCreatorTest overlayCreatorTest = new OverlayCreatorTest();
        testSocket = new Socket(Inet4Address.getLocalHost().getHostName(), 2500);
        testNodes = new NodeRecord[]{
                new NodeRecord("Nicholass-MacBook-Air.local", 1234, testSocket),
                new NodeRecord("Nicholass-MacBook-Air.local", 3456, testSocket),
                new NodeRecord("Nicholass-MacBook-Air.local", 3446, testSocket),
                new NodeRecord("Nicholass-MacBook-Air.local", 3474, testSocket),
                new NodeRecord("Nicholass-MacBook-Air.local", 2534, testSocket),
                new NodeRecord("Nicholass-MacBook-Air.local", 1678, testSocket),
                new NodeRecord("Nicholass-MacBook-Air.local", 3567, testSocket),
                new NodeRecord("Nicholass-MacBook-Air.local", 4536, testSocket),
                new NodeRecord("Nicholass-MacBook-Air.local", 7445, testSocket),
                new NodeRecord("Nicholass-MacBook-Air.local", 4578, testSocket),
        };
        overlayCreatorTest.addTestNodes();
        overlayCreatorTest.testOverlayConstruction();
    }
}



//    private void connectToRandomNodes(List<String> randomList) {
//        List<Integer> integerList = new ArrayList<>();
//        for (ListIterator<String> randomListIterator = randomList.listIterator(); randomListIterator.hasNext(); ) {
//            int attempt = 0;
//            String currentRecord = randomListIterator.next();
//            NodeRecord currentNode = nodeMap.get(currentRecord);
//            while (currentNode.getConnectionsNeededToInitiate() > 0) {
//                int randomConnection = ThreadLocalRandom.current().nextInt(0, nodeMap.size());
//                String randomKey = randomList.get(randomConnection);
//                NodeRecord randomNode = nodeMap.get(randomKey);
//
//                if (integerList.contains(randomConnection)) {
//                    //skip
//                    ++attempt;
//                } else {
//                    if(currentNode.equals(randomNode) || currentNode.getNodesToConnectToList().contains(randomNode)
//                            || randomNode.getNodesToConnectToList().contains(currentNode)) {
//                        //do not add and pick again
//                        ++attempt;
//                    } else if (randomNode.getNumberOfConnections() == requiredConnections){
//                        if(!integerList.contains(randomConnection)) {
//                            integerList.add(randomConnection);
//                        }
//                        ++attempt;
//                    } else if (attempt >= 15){
//                        System.out.println("Impossible configuration, attempting to establish connections again...");
//                        reEstablishConnections(randomList);
//                    } else {
//                        currentNode.addNodeToConnectTo(randomNode);
//                        updateConnections(currentNode, randomNode);
//                    }
//                }
//            }
//            currentNode.printNodesList();
//        }
//    }