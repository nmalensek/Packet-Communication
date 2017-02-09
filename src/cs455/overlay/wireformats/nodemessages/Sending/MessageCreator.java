package cs455.overlay.wireformats.nodemessages.Sending;

import cs455.overlay.dijkstra.RoutingCache;
import cs455.overlay.dijkstra.Vertex;
import cs455.overlay.node.NodeRecord;
import cs455.overlay.wireformats.MessageSend;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MessageCreator {

    private int numberOfRounds;
    private Map<String, NodeRecord> copyOfNodeMap;
    private RoutingCache routingCache;
    private NodeRecord nodeToSendMessagesTo;
    private LinkedList<Vertex> path;

    public MessageCreator(int numberOfRounds, Map<String, NodeRecord> nodeMap, RoutingCache routingCache) {
        this.numberOfRounds = numberOfRounds;
        this.copyOfNodeMap = new HashMap<>(nodeMap); //copies nodeMap so removeNodeFromMap doesn't permanently remove node
        this.routingCache = routingCache;
    }

    public void prepareMessage(String nodeID) {
        removeNodeFromMap(nodeID);
        chooseRandomNode();
        getPathToSelectedNode(nodeToSendMessagesTo);
    }

    //prevents node from messaging itself
    private void removeNodeFromMap(String nodeID) {
        copyOfNodeMap.remove(nodeID);
    }

    public void chooseRandomNode() {
        List<String> randomList = new ArrayList<>();
        for (String node : copyOfNodeMap.keySet()) {
            randomList.add(node);
        }
        int randomNode = ThreadLocalRandom.current().nextInt(0, randomList.size());
        nodeToSendMessagesTo = copyOfNodeMap.get(randomList.get(randomNode));
    }

    private void getPathToSelectedNode(NodeRecord nodeToMessage) {
        Map<String, LinkedList<Vertex>> shortestPathMap = routingCache.getShortestPathsMap();
        path = shortestPathMap.get(nodeToMessage.getNodeID());
        path.removeFirst(); //origin node, should not include in path
    }

    public void sendMessage() throws IOException {
        MessageSend messageSend = new MessageSend();
        messageSend.setRoutingpath(path);
        messageSend.setPayload();
        NodeRecord nextNodeInPath = determineNextNode();
        nextNodeInPath.
    }

    private NodeRecord determineNextNode() {
        Vertex nextVertex = path.getFirst();
        NodeRecord nextNode = copyOfNodeMap.get(nextVertex.getId());
        return nextNode;
    }

}
