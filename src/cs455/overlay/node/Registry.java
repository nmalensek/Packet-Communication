package cs455.overlay.node;

import cs455.overlay.dijkstra.Edge;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.registrymessages.receiving.DeregistrationReceiver;
import cs455.overlay.util.OverlayCreator;
import cs455.overlay.wireformats.registrymessages.receiving.RegistrationReceiver;
import cs455.overlay.util.TextInputThread;
import cs455.overlay.wireformats.registrymessages.receiving.ReceiveDeregisterRequest;
import cs455.overlay.wireformats.registrymessages.receiving.ReceiveRegisterRequest;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.registrymessages.sending.MessagingNodesList;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class Registry implements Node {

    private static int portNum;
    private Map<String, NodeRecord> nodeMap = new HashMap<>();
    private int connectionRequirement;
    private List<Edge> links;

    public void startServer() {
        TCPServerThread registryServerThread = new TCPServerThread(this, portNum);
        registryServerThread.start();
        TextInputThread textInputThread = new TextInputThread(this);
        textInputThread.start();
    }

    public void onEvent(Event event, Socket destinationSocket) throws IOException {
        if (event instanceof ReceiveRegisterRequest) {
            RegistrationReceiver receiver = new RegistrationReceiver(
                    ((ReceiveRegisterRequest) event), nodeMap, destinationSocket);
            receiver.checkRegistration();
        } else if (event instanceof ReceiveDeregisterRequest) {
            DeregistrationReceiver deregistrationReceiver = new DeregistrationReceiver(
                    ((ReceiveDeregisterRequest) event), nodeMap, destinationSocket);
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
                connectionRequirement = numberPortion;
                verifyConnectionRequirement();
                setupOverlay();
                sendMessagingNodesList();
                break;
            default:
                System.out.println("Not a valid command.");
        }
    }

    public void listMessagingNodes() {
        for (String node : nodeMap.keySet()) {
            System.out.println(node);
        }
    }
    //TODO check for 2 nodes (can only have 1 connection) and 1 node
    private void verifyConnectionRequirement() {
        if(nodeMap.size() < connectionRequirement) {
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
        for (cs455.overlay.node.NodeRecord node : nodeMap.values()) {
            messagingNodesList.setNumberOfPeerMessagingNodes(node.getConnectionsNeededToInitiate());
            messagingNodesList.setMessagingNodes(node.getNodesToConnectToList());
            node.getSender().sendData(messagingNodesList.getBytes());
        }
    }

    private void mapVertices() {
//        nodes = new ArrayList<>();
//        for (cs455.overlay.node.NodeRecord node : nodeMap.values()) {
//            nodes.add(location);
//        }
    }

    private void mapLinks() {
//        links = new ArrayList<>();
//        for (cs455.overlay.node.NodeRecord node : nodeMap.values()) {
//            for(cs455.overlay.node.NodeRecord nodeRecord : node.getNodesToConnectToList()) {
//
//            }
//            Edge edge = new Edge()
//        }
    }

    public void assignLinkWeights() {
    }

    public void initiateTask() {

    }

    public void listWeights() {

    }

    public void sendOverlayLinkWeights() {

    }

    public void start() {

    }

    public static void main(String[] args) throws IOException {
        portNum = Integer.parseInt(args[0]);
        Registry registry = new Registry();
        registry.startServer();
    }
}
