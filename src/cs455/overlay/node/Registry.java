package cs455.overlay.node;

import cs455.overlay.dijkstra.Edge;
import cs455.overlay.dijkstra.Vertex;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.TaskInitiate;
import cs455.overlay.wireformats.registrymessages.receiving.DeregistrationReceiver;
import cs455.overlay.util.OverlayCreator;
import cs455.overlay.wireformats.registrymessages.receiving.RegistrationReceiver;
import cs455.overlay.util.TextInputThread;
import cs455.overlay.wireformats.registrymessages.receiving.DeregisterRequestReceive;
import cs455.overlay.wireformats.registrymessages.receiving.RegisterRequestReceive;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.registrymessages.sending.LinkWeightsSend;
import cs455.overlay.wireformats.registrymessages.sending.MessagingNodesList;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Registry implements Node {

    private static int portNum;
    private Map<String, NodeRecord> nodeMap = new HashMap<>();
    private int connectionRequirement;
    private List<Edge> links;
    private boolean overlayEstablished = false;
    private boolean linkWeightsSent = false;

    public void startServer() {
        TCPServerThread registryServerThread = new TCPServerThread(this, portNum);
        registryServerThread.start();
        TextInputThread textInputThread = new TextInputThread(this);
        textInputThread.start();
    }

    public void onEvent(Event event, Socket destinationSocket) throws IOException {
        if (event instanceof RegisterRequestReceive) {
            RegistrationReceiver receiver = new RegistrationReceiver(
                    ((RegisterRequestReceive) event), nodeMap, destinationSocket);
            receiver.checkRegistration();
        } else if (event instanceof DeregisterRequestReceive) {
            DeregistrationReceiver deregistrationReceiver = new DeregistrationReceiver(
                    ((DeregisterRequestReceive) event), nodeMap, destinationSocket);
            deregistrationReceiver.checkDeRegistration();
        }
    }

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
                    verifyConnectionRequirement();
                    setupOverlay();
                    mapLinksAndAssignWeights();
                    sendMessagingNodesList();
                    overlayEstablished = true;
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
                if (overlayEstablished) {
                    initiateTask(numberPortion);
                }
            default:
                System.out.println("Not a valid command.");
        }
    }

    public void listMessagingNodes() {
        for (String node : nodeMap.keySet()) {
            System.out.println(node);
        }
    }

    //TODO check for 2 nodes (can only have 1 connection)
    private void verifyConnectionRequirement() {
        if (nodeMap.size() < connectionRequirement || nodeMap.size() == 1) {
            System.out.println("Not enough nodes to fulfill connection requirement, please re-enter.");
        }
    }

    public void setupOverlay() {
        OverlayCreator overlayCreator = new OverlayCreator(connectionRequirement, nodeMap);
        overlayCreator.createOverlay();
    }

    private int wasANumberEntered(String stringToCheck) {
        try {
            return Integer.parseInt(stringToCheck);
        } catch (NumberFormatException e) {
            System.out.println("Not a number, please re-enter.");
            return 0;
        }
    }

    private void sendMessagingNodesList() throws IOException {
        MessagingNodesList messagingNodesList = new MessagingNodesList();
        for (NodeRecord node : nodeMap.values()) {
            messagingNodesList.setNumberOfPeerMessagingNodes(node.getConnectionsNeededToInitiate());
            messagingNodesList.setMessagingNodes(node.getNodesToConnectToList());
            node.getSender().sendData(messagingNodesList.getBytes());
        }
    }

    private void mapLinksAndAssignWeights() {
        links = new ArrayList<>();
        for (NodeRecord source : nodeMap.values()) {
            for (NodeRecord destination : source.getNodesToConnectToList()) {
                int weight = ThreadLocalRandom.current().nextInt(1, 11);
                Vertex start = new Vertex(source.getNodeID());
                Vertex end = new Vertex(destination.getNodeID());
                Edge edge = new Edge(start.getId() + " " + end.getId(),
                        start, end, weight);
                Edge reverseEdge = new Edge(end.getId() + start.getId(),
                        end, start, weight);
                links.add(edge);
                links.add(reverseEdge);
            }
        }
    }

    private void listWeights() {
        for (Edge link : links) {
            System.out.println(link);
        }
    }

    public void sendOverlayLinkWeights() throws IOException {
        LinkWeightsSend linkWeightsSend = new LinkWeightsSend();
        linkWeightsSend.setNumberOfLinks(links.size());
        linkWeightsSend.setMessagingNodes(links);
        for (NodeRecord node : nodeMap.values()) {
            node.getSender().sendData(linkWeightsSend.getBytes());
        }
    }

    public void initiateTask(int numberOfRounds) throws IOException {
        TaskInitiate taskInitiate = new TaskInitiate();
        taskInitiate.setRounds(numberOfRounds);
        for (NodeRecord nodeRecord : nodeMap.values()) {
            nodeRecord.getSender().sendData(taskInitiate.getBytes());
        }
    }

    public static void main(String[] args) throws IOException {
        portNum = Integer.parseInt(args[0]);
        Registry registry = new Registry();
        registry.startServer();
    }
}
