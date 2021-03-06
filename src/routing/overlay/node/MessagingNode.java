package routing.overlay.node;

import routing.overlay.dijkstra.*;
import routing.overlay.transport.TCPReceiverThread;
import routing.overlay.transport.TCPSender;
import routing.overlay.transport.TCPServerThread;
import routing.overlay.util.CommunicationTracker;
import routing.overlay.util.TextInputThread;
import routing.overlay.wireformats.eventfactory.EventFactory;
import routing.overlay.wireformats.nodemessages.Sending.BindExceptionHappened;
import routing.overlay.wireformats.nodemessages.Sending.Deregister;
import routing.overlay.wireformats.nodemessages.Message;
import routing.overlay.wireformats.nodemessages.Sending.MessageCreator;
import routing.overlay.wireformats.nodemessages.Sending.SendRegister;
import routing.overlay.wireformats.*;
import routing.overlay.wireformats.nodemessages.NodeConnection;
import routing.overlay.wireformats.nodemessages.Receiving.*;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MessagingNode implements Node {
    private String registryHostName;
    private int registryPort;
    private int thisNodePort;
    private String thisNodeIP = Inet4Address.getLocalHost().getHostAddress();
    private String thisNodeID;
    private int requiredConnections;
    private Socket registrySocket;
    private TCPSender registrySender;
    private TCPServerThread receivingSocket;
    private EventFactory eF = EventFactory.getInstance();
    private byte[] message;
    private Map<String, NodeRecord> nodeConnections = new HashMap<>();
    private List<Connection> links;
    private List<Point> vertices;
    private Map<String, Connection> edgeMap;
    private Graph graph;
    private ShortestPath shortestPath;
    private RoutingCache routingCache;
    private CommunicationTracker communicationTracker = new CommunicationTracker();
    private MessageProcessor messageProcessor = new MessageProcessor(communicationTracker);
    private boolean linkWeightsReceived = false;
    private boolean overlaySetUp = false;
    private boolean receivedNodeList = false;

    public MessagingNode(String registryHostName, int registryPort) throws IOException {
        this.registryHostName = registryHostName;
        this.registryPort = registryPort;
        registrySocket = new Socket(registryHostName, registryPort);
        registrySender = new TCPSender(registrySocket);
    }

    /**
     * Setup method. Selects random port, starts a receiver thread with the registry so the node can receive
     * registry messages, starts listening for other node connections and text input, and then sends a registration
     * message to the registry.
     * @throws IOException
     */

    private void startUp() throws IOException {
//        chooseRandomPort();
        TCPReceiverThread receiverThread = new TCPReceiverThread(registrySocket, this);
        receiverThread.start();
        createServerThread();
        listenForTextInput();
        register();
    }

//    private void chooseRandomPort() {
//        thisNodePort = ThreadLocalRandom.current().nextInt(49152, 65535);
//    }

    public void setServerPort(int port) {
        thisNodePort = port;
    }

    private void createServerThread() throws IOException {
        receivingSocket = new TCPServerThread(this, 0); //node starts listening on random open port
        receivingSocket.start();
        thisNodePort = receivingSocket.getPortNumber();
    }

    private void listenForTextInput() throws IOException {
        TextInputThread textInputThread = new TextInputThread(this);
        textInputThread.start();
    }

    /**
     * Sends registration message to the registry, done upon startup.
     * @throws IOException
     */
    private void register() throws IOException {
        SendRegister sendRegister = eF.createRegisterSendEvent().getType();
        sendRegister.setHostAndPort(thisNodeIP, thisNodePort);
        thisNodeID = thisNodeIP + ":" + thisNodePort;
        message = sendRegister.getBytes();
        registrySender.sendData(message);
    }

    /**
     * Messaging node deregisters from overlay, only possible if overlay hasn't been established already.
     * @throws IOException
     */
    private void deregister() throws IOException {
        Deregister deregister = eF.createDeregistrationEvent().getType();
        deregister.setHostAndPort(thisNodeIP, thisNodePort);
        message = deregister.getBytes();
        registrySender.sendData(message);
    }

    /**
     * Processes events and sends a response to the sender (if applicable).
     * @param event Event generated based on the type of message that was sent to this node.
     * @param destinationSocket sender's socket, allows responses.
     * @throws IOException
     */
    public void onEvent(Event event, Socket destinationSocket) throws IOException {
        if (event instanceof RegistryResponseReceive) {
            ((RegistryResponseReceive) event).printMessage();
        } else if (event instanceof DeregisterResponseReceive) {
            byte failure = 0;
            if (((DeregisterResponseReceive) event).getDeRegistrationStatus() == failure) {
                ((DeregisterResponseReceive) event).printMessage();
            } else {
                ((DeregisterResponseReceive) event).printMessage();
                registrySocket.close();
                System.exit(0);
            }
        } else if (event instanceof MessagingNodesListReceive) {
            processMessagingNodesList(((MessagingNodesListReceive) event).getNodesToConnectTo());
            requiredConnections = ((MessagingNodesListReceive) event).getNumberOfRequiredConnections();
        } else if (event instanceof NodeConnection) {
            processNewConnection(((NodeConnection) event).getNodeID());
            if (nodeConnections.size() == requiredConnections) {
                System.out.println("All connections established. Number of connections: "
                        + nodeConnections.size());
                receivedNodeList = true;
                overlaySetUp = true;
            }
        } else if (event instanceof LinkWeightsReceive) {
            LinkWeightsProcess linkWeightsProcess = new LinkWeightsProcess();
            linkWeightsProcess.processLinkWeights(((LinkWeightsReceive) event).getLinkInfo());
            vertices = linkWeightsProcess.getPointList();
            links = linkWeightsProcess.getConnectionList();
            edgeMap = linkWeightsProcess.getEdgeMap();
            computeShortestPaths();
            messageProcessor.setDirectConnections(nodeConnections); //store direct connections for relaying messages
            linkWeightsReceived = true;
            System.out.println("Link weights received and processed. Ready to send messages.");
        } else if (event instanceof TaskInitiate) {
            if (receivedNodeList) {
                int numberOfRounds = ((TaskInitiate) event).getRounds();
                MessageCreator messageCreator = new MessageCreator(vertices, nodeConnections, routingCache, communicationTracker);
                for (int roundsSent = 0; roundsSent < numberOfRounds; roundsSent++) {
                    messageCreator.prepareMessage(thisNodeID);
                    messageCreator.sendMessage();
                }
                taskComplete();
            } else {
                System.out.println("List of nodes to connect to has not been received yet, please send again.");
            }
        } else if (event instanceof Message) {
            messageProcessor.processRoutingPath(((Message) event));
        } else if (event instanceof PullTrafficSummary) {
            createAndSendTrafficSummary();
            communicationTracker.resetCounters();
        } else if (event instanceof BindExceptionHappened) {
            deregister();
        }
    }

    /**
     * Breaks apart String from MessagingNodesList message into format nodeHost:nodePort.
     * @param stringToSplit String from MessagingNodesList message that must be split apart by newline character.
     * @throws IOException
     */
    private synchronized void processMessagingNodesList(String stringToSplit) throws IOException {
        String[] splitByNewLine = stringToSplit.split("\\n");
        for (String nodeID : splitByNewLine) {
            String[] splitIDApart = nodeID.split(":");
            String host = splitIDApart[0];
            int port = Integer.parseInt(splitIDApart[1]);
            NodeRecord nodeToInform = cacheConnection(host, port, nodeID);
            tellOtherNodeAboutConnection(nodeToInform);
        }
    }

    /**
     * Splits apart id from MessagingNodesList message, stores connection, and ignores returned NodeRecord.
     * @param nodeIDLine String separated into format nodeHost:nodePort from processMessagingNodesList method.
     * @throws IOException
     */
    private synchronized void processNewConnection(String nodeIDLine) throws IOException {
        String[] splitIDApart = nodeIDLine.split(":");
        String host = splitIDApart[0];
        int port = Integer.parseInt(splitIDApart[1]);
        cacheConnection(host, port, nodeIDLine);
    }

    /**
     * Stores knowledge of other messaging nodes that this messaging node is connected to.
     * @param host other node's host
     * @param port other node's port
     * @param nodeID other node's ID (host + port)
     * @return returns a NodeRecord object. Only used to inform the other node that a connection exists with this node.
     * @throws IOException
     */
    private synchronized NodeRecord cacheConnection(String host, int port, String nodeID) throws IOException {
        Socket nodeSocket = new Socket(host, port);
        NodeRecord newNodeRecord = new NodeRecord(host, port, nodeSocket);
        TCPReceiverThread receiverThread = new TCPReceiverThread(nodeSocket, this);
        newNodeRecord.setReceiver(receiverThread);
        nodeConnections.put(nodeID, newNodeRecord);
        return newNodeRecord;
    }

    /**
     * Informs another node that the two nodes are connected, enabling links to be bidirectional.
     * @param nodeConnectingTo node that this node is connected to.
     * @throws IOException
     */
    private synchronized void tellOtherNodeAboutConnection(NodeRecord nodeConnectingTo) throws IOException {
        NodeConnection nodeConnection = eF.sendNodeConnection().getType();
        String thisNodeID = thisNodeIP + ":" + Integer.toString(thisNodePort);
        nodeConnection.setNodeID(thisNodeID);
        nodeConnectingTo.getSender().sendData(nodeConnection.getBytes());
    }

    /**
     * Process text input from a user.
     * @param command action the user requests from the messaging node.
     * @throws IOException
     */
    public void processText(String command) throws IOException {
        switch (command) {
            case "print-shortest-path":
                if (linkWeightsReceived) {
                    printShortestPaths();
                } else {
                    System.out.println("Link weights haven\'t been received yet, " +
                            "please re-try after link weights are sent.");
                }
                break;
            case "exit-overlay":
                if (!overlaySetUp) {
                    deregister();
                } else {
                    System.out.println("Overlay is set up, cannot deregister.");
                }
                break;
//            case "list-connections":
//                printConnections();
//                break;
            default:
                System.out.println("Not a valid command.");
        }
    }

    /**
     * Searches through list of points in the overlay to find this messaging node. Used so this node is not included in
     * computing the shortest path.
     * @return returns the current messaging node.
     */
    private Point findThisNodeInVertexList() {
        for (Point point : vertices) {
            if (thisNodeID.equals(point.getId())) {
                return point;
            }
        }
        throw new RuntimeException();
    }

    /**
     * Finds the shortest path to every node in the overlay from this node. Excludes this node from the calculations
     * (shortest path from itself to itself isn't counted). Shortest paths are stored in the RoutingCache class.
     */
    private void computeShortestPaths() {
        routingCache = new RoutingCache(links, edgeMap);
        graph = new Graph(vertices, links);
        shortestPath = new ShortestPath(graph);
        LinkedList<Point> path = new LinkedList<>();
        Point thisNode = findThisNodeInVertexList();
        shortestPath.computeShortestPath(thisNode);
        for (Point destNode : vertices) {
            if (!thisNode.equals(destNode)) {
                path = shortestPath.getPath(destNode);
                routingCache.cacheShortestPath(destNode.getId(), path);
            }
        }
    }

    /**
     * Prints shortest path from this node to all other nodes in overlay.
     */
    private void printShortestPaths() {
        routingCache.printMap(thisNodeID);
//        System.out.println("----");
//        routingCache.simplePrint();
    }

    /**
     * Informs registry all messages have been sent from this node.
     * @throws IOException
     */
    private void taskComplete() throws IOException {
        TaskComplete taskComplete = new TaskComplete();
        taskComplete.setIpAddress(thisNodeIP);
        taskComplete.setPortNumber(thisNodePort);
        registrySender.sendData(taskComplete.getBytes());
//        communicationTracker.printCounters();
    }

    private void createAndSendTrafficSummary() throws IOException {
        TrafficSummary summary = communicationTracker.createTrafficSummary(thisNodeIP, thisNodePort);
        registrySender.sendData(summary.getBytes());
    }

//    private void printConnections() { //used to help debug
//        if (nodeConnections.size() == 0) {
//            System.out.println("No connections available, is the overlay set up?");
//        } else {
//            for (String s : nodeConnections.keySet()) {
//                System.out.println(s);
//            }
//        }
//    }

    public static void main(String[] args) {
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        try {
            MessagingNode messagingNode = new MessagingNode(host, port);
            messagingNode.startUp();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
