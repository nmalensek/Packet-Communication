package routing.overlay.wireformats.nodemessages.Receiving;

import routing.overlay.node.NodeRecord;
import routing.overlay.util.CommunicationTracker;
import routing.overlay.wireformats.nodemessages.Message;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MessageProcessor {

    private CommunicationTracker communicationTracker;
    private Map<String, NodeRecord> copyOfDirectConnections;

    public MessageProcessor(CommunicationTracker communicationTracker) {
        this.communicationTracker = communicationTracker;
    }

    /**
     * Copies map of a node's direct connections so the Map can be operated on as necessary.
     * @param directConnections
     */
    public void setDirectConnections(Map<String, NodeRecord> directConnections) {
        copyOfDirectConnections = new HashMap<>(directConnections);
    }

    /**
     * Retrieves NodeRecord of next node in shortest path.
     * @param nodeID Node that needs to be retrieved.
     * @return
     */
    private synchronized NodeRecord getNextNode(String nodeID) {
        NodeRecord nextNode = copyOfDirectConnections.get(nodeID);
        return nextNode;
    }

    /**
     * Processes message. If the routing path size is 1, this node is the destination and the message doesn't need to be
     * relayed. Otherwise, passes the message along to the prepareMessageForNextNode method.
     * @param message message containing a random int payload.
     * @throws IOException
     */
    public synchronized void processRoutingPath(Message message) throws IOException {
        String route = message.getRoutingPath();
        String[] separateNodes = route.split("\\n");
        if (separateNodes.length == 1) { //this node is destination
            incrementCounters(message);
        } else {
            prepareMessageForNextNode(separateNodes, message);
        }
    }

    /**
     * Increments received messages and receive summation if this node is the destination (sink) node.
     * @param message
     */
    private void incrementCounters(Message message) {
        communicationTracker.incrementReceiveTracker();
        communicationTracker.incrementReceiveSummation(message.getPayload());
    }

    /**
     * Called if a message needs to be relayed. Removes current node from routing path, puts routing path back into
     * a string, and sends the message to the next node in the routing path.
     * @param nodeIDArray routing path in String[] form.
     * @param message message containing a random int payload.
     * @throws IOException
     */
    private synchronized void prepareMessageForNextNode(String[] nodeIDArray, Message message) throws IOException {
        String updatedRoute = "";
        nodeIDArray[0] = null; //this node received message, remove from remaining route
        for (String remainingNodes : nodeIDArray) {
            if (remainingNodes != null) {
                updatedRoute += remainingNodes;
                updatedRoute += "\n";
            }
        }
//        try { //uncomment try catch to help with debugging
            NodeRecord nextNode = getNextNode(nodeIDArray[1]);
            message.setRoutingPath(updatedRoute);
            nextNode.getSender().sendData(message.getBytes());
            communicationTracker.incrementRelayTracker();
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//            System.out.println(message.getRoutingPath());
//            System.out.println(updatedRoute);
//            System.out.println(getNextNode(nodeIDArray[1]).getNodeID());
//        }
    }
}
