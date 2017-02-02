package cs455.overlay.util;

import cs455.overlay.node.NodeRecord;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class OverlayCreator {
    private int requiredConnections;
    private Map<String, NodeRecord> registeredNodes;

    public OverlayCreator(int requiredConnections, Map<String, NodeRecord> registeredNodes) {
        this.requiredConnections = requiredConnections;
        this.registeredNodes = registeredNodes;
    }

    public void createOverlay() {
        setNodeConnectionRequirement();
        connectToNodesInSequence();
        connectToRandomNodes();
    }

    private void setNodeConnectionRequirement() {
        for (NodeRecord node : registeredNodes.values()) {
            node.setNumberOfConnectionsNodeNeedsToInitiate(requiredConnections);
        }
    }

    private List<String> putKeysInList() {
        List<String> stringList = new LinkedList<>();
        for (String node : registeredNodes.keySet()) {
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

                NodeRecord currentNode = registeredNodes.get(currentRecord);
                NodeRecord nextNode = registeredNodes.get(nextRecord);
                currentNode.addNodeToConnectTo(nextNode);
                updateConnections(currentNode, nextNode);
            } catch (IndexOutOfBoundsException e) {
                NodeRecord firstNode = registeredNodes.get(sequentialList.get(0));
                NodeRecord lastNode = registeredNodes.get(sequentialList.get(sequentialList.size()-1));
                lastNode.addNodeToConnectTo(firstNode);
                updateConnections(lastNode, firstNode);
            }
        }
    }

    private void connectToRandomNodes() {
        List<String> randomList = new LinkedList<>(putKeysInList());
        for (ListIterator<String> randomListIterator = randomList.listIterator(); randomListIterator.hasNext(); ) {
            String currentRecord = randomListIterator.next();
            NodeRecord currentNode = registeredNodes.get(currentRecord);
            while (currentNode.getConnectionsNeededToInitiate() > 0) {
                int randomConnection = ThreadLocalRandom.current().nextInt(0, registeredNodes.size());
                String randomKey = randomList.get(randomConnection);
                NodeRecord randomNode = registeredNodes.get(randomKey);

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
}
