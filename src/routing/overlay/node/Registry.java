package routing.overlay.node;

import routing.overlay.dijkstra.Connection;
import routing.overlay.dijkstra.Point;
import routing.overlay.transport.TCPServerThread;
import routing.overlay.util.TrafficPrinter;
import routing.overlay.wireformats.*;
import routing.overlay.wireformats.registrymessages.receiving.DeregistrationReceiver;
import routing.overlay.util.OverlayCreator;
import routing.overlay.wireformats.registrymessages.receiving.RegistrationReceiver;
import routing.overlay.util.TextInputThread;
import routing.overlay.wireformats.registrymessages.receiving.DeregisterRequestReceive;
import routing.overlay.wireformats.registrymessages.receiving.RegisterRequestReceive;
import routing.overlay.wireformats.registrymessages.sending.LinkWeightsSend;
import routing.overlay.wireformats.registrymessages.sending.MessagingNodesList;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Registry implements Node {

    private static int portNum;
    private Map<String, NodeRecord> nodeMap = new HashMap<>();
    private int connectionRequirement;
    private List<Connection> links;
    private boolean overlayEstablished = false;
    private boolean linkWeightsSent = false;
    private int finishedNodes;
    private int numberOfSummariesReceived;
    private TrafficPrinter trafficPrinter = new TrafficPrinter();

    /**
     * Starts listening for node connections and text input from the user.
     */
    public void startServer() {
        TCPServerThread registryServerThread = new TCPServerThread(this, portNum);
        registryServerThread.start();
        TextInputThread textInputThread = new TextInputThread(this);
        textInputThread.start();
    }

    /**
     * Processes events and sends a follow-up message to the sender as applicable.
     * @param event event that occurred.
     * @param destinationSocket sender's communication socket, allows a response.
     * @throws IOException
     */
    public void onEvent(Event event, Socket destinationSocket) throws IOException {
        if (event instanceof RegisterRequestReceive) {
            receiveRegistration(((RegisterRequestReceive) event), destinationSocket);
        } else if (event instanceof DeregisterRequestReceive) {
            DeregistrationReceiver deregistrationReceiver = new DeregistrationReceiver(
                    ((DeregisterRequestReceive) event), nodeMap, destinationSocket, overlayEstablished);
            deregistrationReceiver.checkDeRegistration();
        } else if (event instanceof TaskComplete) {
            ++finishedNodes;
            if(finishedNodes == nodeMap.size()) {
                try {
                    Thread.sleep(15000);
                    pullTrafficSummary();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else if (event instanceof TrafficSummary) {
            trafficPrinter.processSummary(((TrafficSummary) event));
            ++numberOfSummariesReceived;
            if (numberOfSummariesReceived == nodeMap.size()) {
                trafficPrinter.addTotalsToString();
                trafficPrinter.printTrafficSummary();
                numberOfSummariesReceived = 0;
            }
        }
    }

    /**
     * Receives a node's registration. Synchronized because a race condition occasionally happened where one node
     * would overwrite another node's registration.
     * @param registerRequestReceive copy of the message received so it can be processed further.
     * @param destinationSocket sender's socket, allows a response.
     * @throws IOException
     */
    private synchronized void receiveRegistration(RegisterRequestReceive registerRequestReceive,
                                                  Socket destinationSocket) throws IOException {
        RegistrationReceiver receiver = new RegistrationReceiver(
                registerRequestReceive, nodeMap, destinationSocket);
        receiver.checkRegistration();
    }

    /**
     * Process text input from a user.
     * @param command action the user requests from the messaging node.
     * @throws IOException
     */
    public void processText(String command) throws IOException {
        String line = command;
        int numberPortion = 0;
        String textPortion;
        String[] delimiter = line.split("\\s");
        if (delimiter.length == 2) {
            textPortion = delimiter[0];
            numberPortion = wasANumberEntered(delimiter[1]);
        } else {
            textPortion = delimiter[0];
        }
        switch (textPortion) {
            case "list-messaging-nodes":
                listMessagingNodes();
                break;
            case "setup-overlay":
                if (!overlayEstablished) {
                    connectionRequirement = numberPortion;
                    if (validConnectionRequirement()) {
                        setupOverlay();
                        mapLinksAndAssignWeights();
                        sendMessagingNodesList();
                        overlayEstablished = true;
                    }
                } else {
                    System.out.println("Overlay is already established.");
                }
                break;
            case "list-weights":
                if(overlayEstablished) {
                    listWeights();
                } else {
                    System.out.println("Overlay hasn\'t been successfully set up yet, " +
                            "please set up the overlay.");
                }
                break;
            case "send-overlay-link-weights":
                if(!linkWeightsSent && overlayEstablished) {
                    sendOverlayLinkWeights();
                    linkWeightsSent = true;
                } else if (!linkWeightsSent && !overlayEstablished){
                    System.out.println("Overlay hasn\'t been successfully set up yet, " +
                            "please set up the overlay first.");
                } else if (linkWeightsSent) {
                    System.out.println("Link weights have already been sent.");
                }
                break;
            case "start":
                if (overlayEstablished && linkWeightsSent) {
                    //counter resets when task starts in case there are still messages from last run coming in
                    trafficPrinter.resetTrafficStringAndCounters();
                    finishedNodes = 0;
                    initiateTask(numberPortion);
                } else {
                    System.out.println("Overlay hasn\'t been successfully set up yet, " +
                            "please set up the overlay.");
                }
                break;
            default:
                System.out.println("Not a valid command.");
        }
    }

    private void listMessagingNodes() {
        for (String node : nodeMap.keySet()) {
            System.out.println(node);
        }
    }

    private boolean validConnectionRequirement() {
        if (nodeMap.size() < connectionRequirement || nodeMap.size() < 3) {
            System.out.println("Not enough nodes to fulfill connection requirement, please re-enter.");
            return false;
        } else {
            return true;
        }
    }

    private void setupOverlay() {
        OverlayCreator overlayCreator = new OverlayCreator(connectionRequirement, nodeMap);
        overlayCreator.createOverlay();
    }

    /**
     * Displays an error message if a user tries to specify a non-integer when setting up the overlay.
     * @param stringToCheck string the user entered.
     * @return
     */
    private int wasANumberEntered(String stringToCheck) {
        try {
            return Integer.parseInt(stringToCheck);
        } catch (NumberFormatException e) {
            System.out.println("Not a number, please re-enter.");
            return 0;
        }
    }

    /**
     * Sends a list of nodes to connect to to every node in the overlay.
     * @throws IOException
     */
    private void sendMessagingNodesList() throws IOException {
        MessagingNodesList messagingNodesList = new MessagingNodesList();
        for (NodeRecord node : nodeMap.values()) {
            messagingNodesList.setNumberOfRequiredConnections(connectionRequirement);
            messagingNodesList.setMessagingNodes(node.getNodesToConnectToList());
            node.getSender().sendData(messagingNodesList.getBytes());
        }
    }

    /**
     * Assigns random weights to connections in overlay. Connections are unidirectional at this point, so the connection
     * is then reversed to make it bidirectional.
     */
    private void mapLinksAndAssignWeights() {
        links = new ArrayList<>();
        for (NodeRecord source : nodeMap.values()) {
            for (NodeRecord destination : source.getNodesToConnectToList()) {
                int weight = ThreadLocalRandom.current().nextInt(1, 11);
                Point start = new Point(source.getNodeID());
                Point end = new Point(destination.getNodeID());
                Connection connection = new Connection(start.getId() + " " + end.getId(),
                        start, end, weight);
                Connection reverseConnection = new Connection(end.getId() + start.getId(),
                        end, start, weight);
                links.add(connection);
                links.add(reverseConnection); //makes links bidirectional
            }
        }
    }

    /**
     * Prints every other connection (connections are duplicated in mapLinksAndAssignWeights method to make them
     * bidirectional).
     */
    private void listWeights() {
        for (int i = 0; i < links.size(); i +=2) {
            System.out.println(links.get(i));
        }
    }

    /**
     * Sends all link weights for all connections to all nodes in the overlay.
     * @throws IOException
     */
    private void sendOverlayLinkWeights() throws IOException {
        LinkWeightsSend linkWeightsSend = new LinkWeightsSend();
        linkWeightsSend.setNumberOfLinks(links.size());
        linkWeightsSend.setMessagingNodes(links);
        for (NodeRecord node : nodeMap.values()) {
            node.getSender().sendData(linkWeightsSend.getBytes());
        }
    }

    /**
     * Tells nodes to start sending messages to each other.
     * @param numberOfRounds how many rounds of 5 messages each a node should send.
     * @throws IOException
     */
    private void initiateTask(int numberOfRounds) throws IOException {
        TaskInitiate taskInitiate = new TaskInitiate();
        taskInitiate.setRounds(numberOfRounds);
        for (NodeRecord nodeRecord : nodeMap.values()) {
            nodeRecord.getSender().sendData(taskInitiate.getBytes());
        }
    }

    private void pullTrafficSummary() throws IOException {
        PullTrafficSummary pullTrafficSummary = new PullTrafficSummary();
        for (NodeRecord nodeRecord : nodeMap.values()) {
            nodeRecord.getSender().sendData(pullTrafficSummary.getBytes());
        }
    }

    public void setServerPort(int port) throws IOException {
        //doesn't apply to registry
    }

    public static void main(String[] args) throws IOException {
        portNum = Integer.parseInt(args[0]);
        Registry registry = new Registry();
        registry.startServer();
    }
}
