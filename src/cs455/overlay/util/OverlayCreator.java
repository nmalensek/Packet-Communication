package cs455.overlay.util;

import cs455.overlay.node.NodeRecord;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class OverlayCreator {
    private int requiredConnections;
    private int attempts;
    private boolean wasSuccessful = true;
    private Map<String, NodeRecord> registeredNodes;

    public OverlayCreator(int requiredConnections, Map<String, NodeRecord> registeredNodes) {
        this.requiredConnections = requiredConnections;
        this.registeredNodes = registeredNodes;
    }

    public void createOverlay() {
        setNodeConnectionRequirement();
        List<String> keyList = putKeysInList();
        connectToNodesInSequence(keyList);
        connectToRandomNodes(keyList);
        checkIfSuccessful();
    }

    /**
     * Number of connections a node needs to initiate to satisfy the overlay's requirements (given by user text input).
     */
    private void setNodeConnectionRequirement() {
        for (NodeRecord node : registeredNodes.values()) {
            node.setNumberOfConnectionsNodeNeedsToInitiate(requiredConnections);
        }
    }

    /**
     * Puts map of overlay nodes in a list so they can connect sequentially.
     * @return
     */
    private List<String> putKeysInList() {
        List<String> stringList = new LinkedList<>();
        for (String node : registeredNodes.keySet()) {
            stringList.add(node);
        }
        return stringList;
    }

    /**
     * Every node connects to its neighbor, which prevents partitioning. When the end of the list is reached, the last
     * element connects to the first element.
     * @param sequentialList
     */
    public void connectToNodesInSequence(List<String> sequentialList) {
        for (ListIterator<String> recordListIterator = sequentialList.listIterator(); recordListIterator.hasNext();) {
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

    /**
     * Connects each node to another random node in the overlay by choosing a random element in the list of keys (node IDs).
     * A node cannot connect to itself or connect to a node that it's already connected to. This process continues until
     * the node has a list of nodes to connect to equal to the number specified in the "setup-overlay #" command.
     * @param randomList
     */
    //TODO if have time, re-implement by taking a list, shuffling the list, and connecting to pre-defined elements in list
    private void connectToRandomNodes(List<String> randomList) {
        wasSuccessful = true;
        attempts = 0;
        for (ListIterator<String> randomListIterator = randomList.listIterator(); randomListIterator.hasNext(); ) {
            String currentRecord = randomListIterator.next();
            NodeRecord currentNode = registeredNodes.get(currentRecord);
            while (currentNode.getConnectionsNeededToInitiate() > 0) {
                if (attempts < 15) {
                    int randomConnection = ThreadLocalRandom.current().nextInt(0, registeredNodes.size());
                    String randomKey = randomList.get(randomConnection);
                    NodeRecord randomNode = registeredNodes.get(randomKey);

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
//            currentNode.printNodesList();
        }
    }

    /**
     * Increments amount of connections a node has and decrements the number of connections a node needs.
     * @param currentNode node that's connecting to other nodes.
     * @param nextNode node that's being connected to.
     */
    private void updateConnections(NodeRecord currentNode, NodeRecord nextNode) {
        currentNode.incrementConnections();
        currentNode.decrementConnectionsToInitiate();

        nextNode.incrementConnections();
        nextNode.decrementConnectionsToInitiate();
    }

    /**
     * Overlay creation can result in impossible overlays (cannot meet connection requirement), so the Registry resets
     * the overlay and tries to create it again in that case.
     */
    private void reEstablishConnections() {
//        System.out.println("Impossible to create overlay, starting over...");
        for (NodeRecord node : registeredNodes.values()) {
            node.getNodesToConnectToList().clear();
            node.resetNumberOfConnections();
        }
        createOverlay();
    }

    private void checkIfSuccessful() {
        while(!wasSuccessful) {
            reEstablishConnections();
        }
    }
}
