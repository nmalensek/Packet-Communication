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

    public MessageSender(int numberOfRounds, Map<String, NodeRecord> nodeMap, RoutingCache routingCache) {
        this.numberOfRounds = numberOfRounds;
        this.nodeMap = nodeMap;
        this.routingCache = routingCache;
    }

    //prevents node from messaging itself
    public void removeNodeFromMap(String nodeID) {
        nodeMap.remove(nodeID);
    }

    public void chooseRandomNode() {
        List<String> randomList = new ArrayList<>();
        for (String node : nodeMap.keySet()) {
            randomList.add(node);
        }
        int randomNode = ThreadLocalRandom.current().nextInt(0, randomList.size());
        nodeToSendMessagesTo = nodeMap.get(randomList.get(randomNode));
        getPathToSelectedNode(nodeToSendMessagesTo);
    }

    private void getPathToSelectedNode(NodeRecord nodeToMessage) {
        Map<String, LinkedList<Vertex>> shortestPathMap = routingCache.getShortestPathsMap();
        LinkedList<Vertex> path = shortestPathMap.get(nodeToMessage.getNodeID());
    }

}
