package cs455.overlay.node;

import cs455.overlay.dijkstra.*;
import cs455.overlay.transport.TCPReceiverThread;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.CommunicationTracker;
import cs455.overlay.util.TextInputThread;
import cs455.overlay.wireformats.*;
import cs455.overlay.wireformats.nodemessages.*;
import cs455.overlay.wireformats.eventfactory.EventFactory;
import cs455.overlay.wireformats.nodemessages.Receiving.*;
import cs455.overlay.wireformats.nodemessages.Sending.Deregister;
import cs455.overlay.wireformats.nodemessages.Message;
import cs455.overlay.wireformats.nodemessages.Sending.MessageCreator;
import cs455.overlay.wireformats.nodemessages.Sending.SendRegister;

import java.io.IOException;
import java.net.BindException;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

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

    private void startUp() throws IOException {
        try {
            chooseRandomPort();
            TCPReceiverThread receiverThread = new TCPReceiverThread(registrySocket, this);
            receiverThread.start();
            register();
            createServerThread();
            listenForTextInput();
        } catch (BindException e) {
            System.exit(0);
        }
    }

    private void chooseRandomPort() {
        thisNodePort = ThreadLocalRandom.current().nextInt(49152, 65535);
    }

    private void register() throws IOException {
        SendRegister sendRegister = eF.createRegisterSendEvent().getType();
        sendRegister.setHostAndPort(thisNodeIP, thisNodePort);
        thisNodeID = thisNodeIP + ":" + thisNodePort;
        message = sendRegister.getBytes();
        registrySender.sendData(message);
    }

    private void deregister() throws IOException {
        Deregister deregister = eF.createDeregistrationEvent().getType();
        deregister.setHostAndPort(thisNodeIP, thisNodePort);
        message = deregister.getBytes();
        registrySender.sendData(message);
    }

    private void createServerThread() throws IOException {
        receivingSocket = new TCPServerThread(this, thisNodePort);
        receivingSocket.start();
    }

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
        }
    }

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

    //splits apart id, stores connection, and ignores returned Point
    private synchronized void processNewConnection(String nodeIDLine) throws IOException {
        String[] splitIDApart = nodeIDLine.split(":");
        String host = splitIDApart[0];
        int port = Integer.parseInt(splitIDApart[1]);
        cacheConnection(host, port, nodeIDLine);
    }

    private synchronized NodeRecord cacheConnection(String host, int port, String nodeID) throws IOException {
        Socket nodeSocket = new Socket(host, port);
        NodeRecord newNodeRecord = new NodeRecord(host, port, nodeSocket);
        TCPReceiverThread receiverThread = new TCPReceiverThread(nodeSocket, this);
        newNodeRecord.setReceiver(receiverThread);
        nodeConnections.put(nodeID, newNodeRecord);
        return newNodeRecord;
    }

    private synchronized void tellOtherNodeAboutConnection(NodeRecord nodeConnectingTo) throws IOException {
        NodeConnection nodeConnection = eF.sendNodeConnection().getType();
        String thisNodeID = thisNodeIP + ":" + Integer.toString(thisNodePort);
        nodeConnection.setNodeID(thisNodeID);
        nodeConnectingTo.getSender().sendData(nodeConnection.getBytes());
    }

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

    private void listenForTextInput() throws IOException {
        TextInputThread textInputThread = new TextInputThread(this);
        textInputThread.start();
    }

    private Point findThisNodeInVertexList() {
        for (Point point : vertices) {
            if (thisNodeID.equals(point.getId())) {
                return point;
            }
        }
        throw new RuntimeException();
    }

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

    private void printShortestPaths() {
        routingCache.printMap(thisNodeID);
//        System.out.println("----");
//        routingCache.simplePrint();
    }

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
