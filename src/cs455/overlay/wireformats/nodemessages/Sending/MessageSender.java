package cs455.overlay.wireformats.nodemessages.Sending;

import cs455.overlay.dijkstra.RoutingCache;
import cs455.overlay.dijkstra.Vertex;
import cs455.overlay.node.NodeRecord;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MessageSender {

    private int numberOfRounds;
    private Map<String, NodeRecord> nodeMap;
    private RoutingCache routingCache;
    private NodeRecord nodeToSendMessagesTo;
    private LinkedList<Vertex> path;

    public MessageSender(int numberOfRounds, Map<String, NodeRecord> nodeMap, RoutingCache routingCache) {
        this.numberOfRounds = numberOfRounds;
        this.nodeMap = nodeMap;
        this.routingCache = routingCache;
    }

    public void prepareMessage(String nodeID) {
        removeNodeFromMap(nodeID);
        chooseRandomNode();
        getPathToSelectedNode(nodeToSendMessagesTo);
    }

    //prevents node from messaging itself
    private void removeNodeFromMap(String nodeID) {
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

    private void getPathToSelectedNode(NodeRecord nodeToMessage) {
        Map<String, LinkedList<Vertex>> shortestPathMap = routingCache.getShortestPathsMap();
        path = shortestPathMap.get(nodeToMessage.getNodeID());
        path.removeFirst(); //origin node, should not include in path
    }

    public void sendMessage() {
        String routingPath = "";
        for (Vertex vertex : path) {
            routingPath += vertex.getId();
        }
    }

}
