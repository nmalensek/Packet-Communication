package cs455.overlay.node;

import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.DeregistrationReceiver;
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
//TODO add catch for non-number entries, 0 entries and # < #nodes for setup-overlay
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
                System.out.println(textPortion + " " + numberPortion);
                connectToNodesInSequence();
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

    private List<String> putKeysInList() {
        List<String> stringList = new LinkedList<>();
        for (String node : nodeMap.keySet()) {
            stringList.add(node);
        }
        return stringList;
    }

    public void connectToNodesInSequence() {
        List<String> sequentialList = new ArrayList<>(putKeysInList());
        for (ListIterator<String> recordListIterator = sequentialList.listIterator(); recordListIterator.hasNext(); ) {
            try {
                String currentRecord = recordListIterator.next();
                int currentPosition = sequentialList.indexOf(currentRecord);
                String nextRecord = sequentialList.get(currentPosition + 1);

                NodeRecord currentNode = nodeMap.get(currentRecord);
                NodeRecord nextNode = nodeMap.get(nextRecord);
                currentNode.addNodeToConnectTo(nextNode);
                updateConnections(currentNode, nextNode);
            } catch (IndexOutOfBoundsException e) {
                NodeRecord firstNode = nodeMap.get(sequentialList.get(0));
                NodeRecord lastNode = nodeMap.get(sequentialList.get(sequentialList.size()-1));
                lastNode.addNodeToConnectTo(firstNode);
                updateConnections(lastNode, firstNode);
            }
        }
    }

    private void updateConnections(NodeRecord currentNode, NodeRecord nextNode) {
        currentNode.incrementConnections();
        currentNode.decrementNeededConnections();

        nextNode.incrementConnections();
        nextNode.decrementNeededConnections();
    }

//    private void connectToRandomNodes() {
//        for (ListIterator<NodeRecord> nodeListIterator = nodeMap.listIterator(); nodeListIterator.hasNext(); ) {
//            NodeRecord currentNode = nodeListIterator.next();
//            while (currentNode.getConnectionsNeededToInitiate() > 0) {
//                int randomConnection = ThreadLocalRandom.current().nextInt(0, nodeMap.size()-1);
//                NodeRecord randomNode = nodeMap.get(randomConnection);
//
//                if(currentNode.equals(randomNode) || currentNode.getNodesToConnectToList().contains(randomNode)) {
//                    //do not add and pick again
//                } else {
//                    currentNode.addNodeToConnectTo(randomNode);
//                    updateConnections(currentNode, randomNode);
//                }
//            }
//        }
//    }

    public void assignLinkWeights() {

    }

    public void initiateTask() {

    }

    public void listWeights() {

    }

    public void setupOverlay() {

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
