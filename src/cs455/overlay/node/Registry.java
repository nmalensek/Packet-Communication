package cs455.overlay.node;

import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.DeregistrationReceiver;
import cs455.overlay.util.OverlayCreator;
import cs455.overlay.util.RegistrationReceiver;
import cs455.overlay.util.TextInputThread;
import cs455.overlay.wireformats.registrymessages.ReceiveDeregisterRequest;
import cs455.overlay.wireformats.registrymessages.ReceiveRegisterRequest;
import cs455.overlay.wireformats.Event;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class Registry implements Node {

    private static int portNum;
    private Map<String, NodeRecord> nodeMap = new ConcurrentHashMap<>();
    private int connectionRequirement;

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

    //TODO add catch for non-number entries, 0 entries
    public void processText(String command) throws IOException {
        String line = command;
        int numberPortion = 0;
        String textPortion;
        String[] delimiter = line.split("\\s");
        if (delimiter.length == 2) {
            textPortion = delimiter[0];
            numberPortion = Integer.parseInt(delimiter[1]);
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

    private void verifyConnectionRequirement() {
        if(nodeMap.size() < connectionRequirement) {
            System.out.println("Not enough nodes to fulfill connection requirement, please re-enter.");
        } else {
            setupOverlay();
        }
    }

    public void setupOverlay() {
        OverlayCreator overlayCreator = new OverlayCreator(connectionRequirement, nodeMap);
        overlayCreator.createOverlay();
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
