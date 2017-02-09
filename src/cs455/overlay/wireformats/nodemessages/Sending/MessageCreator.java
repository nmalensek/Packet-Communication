package cs455.overlay.wireformats.nodemessages.Sending;

import cs455.overlay.dijkstra.RoutingCache;
import cs455.overlay.dijkstra.Vertex;
import cs455.overlay.node.NodeRecord;
import cs455.overlay.util.CommunicationTracker;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MessageCreator {

    private List<Vertex> copyOfVerticesInOverlay;
    private HashMap<String, String> nodeMap;
    private Map<String, NodeRecord> copyOfDirectConnections;
    private RoutingCache routingCache;
    private String nodeToSendMessagesTo;
    private LinkedList<Vertex> path;
    private CommunicationTracker communicationTracker;

    public MessageCreator(List<Vertex> verticesInOverlay, Map<String,
            NodeRecord> directConnections, RoutingCache routingCache, CommunicationTracker communicationTracker) {
        this.copyOfVerticesInOverlay = new ArrayList<>(verticesInOverlay); //copies vertices so nothing damages original list
        this.copyOfDirectConnections = new HashMap<>(directConnections);  //copies map so nothing damages original map
        this.routingCache = routingCache;
        this.nodeMap = new HashMap<>();
        this.communicationTracker = communicationTracker;
        addVerticesToOverlayMap();
    }

    private void addVerticesToOverlayMap() {
        for (Vertex vertex : copyOfVerticesInOverlay) {
            nodeMap.put(vertex.getId(), vertex.getId());
        }
    }

    public void prepareMessage(String nodeID) {
        removeNodeFromOverlayMap(nodeID);
        chooseRandomNode();
        getPathToSelectedNode(nodeToSendMessagesTo);
    }

    //prevents node from messaging itself
    private void removeNodeFromOverlayMap(String nodeID) {
        nodeMap.remove(nodeID);
    }

    public void chooseRandomNode() {
        List<String> randomList = new ArrayList<>();
        for (String node : nodeMap.keySet()) {
            randomList.add(node);
        }
        int randomNode = ThreadLocalRandom.current().nextInt(0, randomList.size());
        nodeToSendMessagesTo = nodeMap.get(randomList.get(randomNode));
    }

    private void getPathToSelectedNode(String nodeToMessage) {
        Map<String, LinkedList<Vertex>> shortestPathMap = routingCache.getShortestPathsMap();
        path = shortestPathMap.get(nodeToMessage);
        path.removeFirst(); //origin node, should not include in path
    }

    public void sendMessage() throws IOException {
        NodeRecord nextNodeInPath = determineNextNode();
        MessageSend messageSend = new MessageSend();
        messageSend.setRoutingPath(path);
        for (int numMessages = 0; numMessages < 5; numMessages++) {
            messageSend.setPayload();
            nextNodeInPath.getSender().sendData(messageSend.getBytes());
            communicationTracker.incrementSendTracker();
            communicationTracker.incrementSendSummation(messageSend.getPayload());
        }
    }

    private NodeRecord determineNextNode() {
        Vertex nextVertex = path.getFirst();
        NodeRecord nextNode = copyOfDirectConnections.get(nextVertex.getId());
        return nextNode;
    }

}
