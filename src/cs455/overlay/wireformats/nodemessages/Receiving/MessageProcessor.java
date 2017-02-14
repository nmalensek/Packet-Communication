package cs455.overlay.wireformats.nodemessages.Receiving;

import cs455.overlay.node.NodeRecord;
import cs455.overlay.util.CommunicationTracker;
import cs455.overlay.wireformats.nodemessages.Message;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MessageProcessor {

    private CommunicationTracker communicationTracker;
    private Map<String, NodeRecord> copyOfDirectConnections;

    public MessageProcessor(CommunicationTracker communicationTracker) {
        this.communicationTracker = communicationTracker;
    }

    public void setDirectConnections(Map<String, NodeRecord> directConnections) {
        copyOfDirectConnections = new HashMap<>(directConnections);
    }

    private NodeRecord getNextNode(String nodeID) {
        NodeRecord nextNode = copyOfDirectConnections.get(nodeID);
        return nextNode;
    }

    public void processRoutingPath(Message message) throws IOException {
        String route = message.getRoutingPath();
        String[] separateNodes = route.split("\\n");
        if (separateNodes.length == 1) { //this node is destination
            incrementCounters(message);
        } else {
            prepareMessageForNextNode(separateNodes, message);
        }
    }

    private void incrementCounters(Message message) {
        communicationTracker.incrementReceiveTracker();
        communicationTracker.incrementReceiveSummation(message.getPayload());
    }

    private void prepareMessageForNextNode(String[] nodeIDArray, Message message) throws IOException {
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
