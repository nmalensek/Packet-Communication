package routing.overlay.wireformats.nodemessages.Sending;

import routing.overlay.dijkstra.Point;
import routing.overlay.dijkstra.RoutingCache;
import routing.overlay.node.NodeRecord;
import routing.overlay.util.CommunicationTracker;
import routing.overlay.wireformats.nodemessages.Message;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MessageCreator {

    private List<Point> copyOfVerticesInOverlay;
    private HashMap<String, String> nodeMap;
    private Map<String, NodeRecord> copyOfDirectConnections;
    private RoutingCache routingCache;
    private String nodeToSendMessagesTo;
    private CommunicationTracker communicationTracker;
    private String stringPath;
    private LinkedList<Point> path;

    public MessageCreator(List<Point> verticesInOverlay, Map<String,
            NodeRecord> directConnections, RoutingCache routingCache, CommunicationTracker communicationTracker) {
        this.copyOfVerticesInOverlay = new ArrayList<>(verticesInOverlay); //copies vertices so nothing damages original list
        this.copyOfDirectConnections = new HashMap<>(directConnections);  //copies map so nothing damages original map
        this.routingCache = routingCache;
        this.nodeMap = new HashMap<>();
        this.communicationTracker = communicationTracker;
        addVerticesToOverlayMap();
    }

    private void addVerticesToOverlayMap() {
        for (Point point : copyOfVerticesInOverlay) {
            nodeMap.put(point.getId(), point.getId());
        }
    }

    public void prepareMessage(String nodeID) {
        removeNodeFromOverlayMap(nodeID);
        chooseRandomNode();
        getPathToSelectedNode(nodeToSendMessagesTo);
    }

    /**
     * prevents node from messaging itself
     * @param nodeID
     */
    private void removeNodeFromOverlayMap(String nodeID) {
        nodeMap.remove(nodeID);
    }

    /**
     * Chooses random node from overlay to message.
     */
    public void chooseRandomNode() {
        List<String> randomList = new ArrayList<>();
        for (String node : nodeMap.keySet()) {
            randomList.add(node);
        }
        int randomNode = ThreadLocalRandom.current().nextInt(0, randomList.size());
        nodeToSendMessagesTo = nodeMap.get(randomList.get(randomNode));
    }

    /**
     * Finds shortest path to randomly selected node. Removes origin node from the shortest path. Each step in the path
     * is deleted so nodes can relay messages successfully.
     * @param nodeToMessage
     */
    private void getPathToSelectedNode(String nodeToMessage) {
        Map<String, LinkedList<Point>> shortestPathMap =
                new HashMap<>(routingCache.getShortestPathsMap()); //MUST COPY HERE, otherwise nodes will delete path knowledge
        path = new LinkedList<>(shortestPathMap.get(nodeToMessage)); //and here?
        path.removeFirst(); //origin node, should not include in path
        convertPathToStrings(path);
    }

    /**
     * Allows routing path to be sent as a byte array.
     * @param pointPath
     */
    private void convertPathToStrings(LinkedList<Point> pointPath) {
        stringPath = "";
        for (Point point : pointPath) {
            stringPath += point.getId();
            stringPath += "\n";
        }
    }

    /**
     * Sends message to first node in shortest path, repeats 5 times according to assignment instructions.
     * @throws IOException
     */
    public void sendMessage() throws IOException {
        NodeRecord nextNodeInPath = determineNextNode();
        Message message = new Message();
        message.setRoutingPath(stringPath);
        for (int numMessages = 0; numMessages < 5; numMessages++) {
            message.setPayload();
            nextNodeInPath.getSender().sendData(message.getBytes());
            communicationTracker.incrementSendTracker();
            communicationTracker.incrementSendSummation(message.getPayload());
        }
    }

    /**
     * Finds next node that the message should go to from the group of nodes that this node has a direct connection to.
     * @return
     */
    private NodeRecord determineNextNode() {
//        try { //uncomment to help with debugging
            Point nextPoint = path.getFirst();
            NodeRecord nextNode = copyOfDirectConnections.get(nextPoint.getId());
            return nextNode;
//        } catch (NoSuchElementException e) {
//            System.out.println(nodeToSendMessagesTo);
//        }
//        throw new RuntimeException();
    }

}
