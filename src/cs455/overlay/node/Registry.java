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
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ThreadLocalRandom;

public class Registry implements Node {

    private static int portNum;
    private List<NodeRecord> nodeList = new ArrayList<>();

    public void startServer() {
        TCPServerThread registryServerThread = new TCPServerThread(this, portNum);
        registryServerThread.start();
//        TextInputThread textInputThread = new TextInputThread(this);
//        textInputThread.start();
    }

    public void onEvent(Event event, Socket destinationSocket) throws IOException {
        if (event instanceof ReceiveRegisterRequest) {
            RegistrationReceiver receiver = new RegistrationReceiver(
                    ((ReceiveRegisterRequest) event), nodeList, destinationSocket);
            receiver.checkRegistration();
        } else if (event instanceof ReceiveDeregisterRequest) {
            DeregistrationReceiver deregistrationReceiver = new DeregistrationReceiver(
                    ((ReceiveDeregisterRequest) event), nodeList, destinationSocket);
            deregistrationReceiver.checkDeRegistration();
        }
    }

    public void listMessagingNodes() {
        connectToNodesInSequence();
    }

    public void connectToNodesInSequence() {
        NodeRecord lastRecord = nodeList.get(nodeList.size()-1);
        NodeRecord firstRecord = nodeList.get(0);
        for (ListIterator<NodeRecord> recordListIterator = nodeList.listIterator(); recordListIterator.hasNext(); ) {
            try {
                NodeRecord currentRecord = recordListIterator.next();
                int currentPosition = nodeList.indexOf(currentRecord);
                NodeRecord nextRecord = nodeList.get(currentPosition + 1);

                currentRecord.addNodeToConnectTo(nextRecord);
                updateConnections(currentRecord, nextRecord);
            } catch (IndexOutOfBoundsException e) {
                lastRecord.addNodeToConnectTo(firstRecord);
                updateConnections(lastRecord, firstRecord);
            }
        }
    }

    private void updateConnections(NodeRecord currentNode, NodeRecord nextNode) {
        currentNode.incrementConnections();
        currentNode.decrementNeededConnections();

        nextNode.incrementConnections();
        nextNode.decrementNeededConnections();
    }

    private void connectToRandomNodes() {
        for (ListIterator<NodeRecord> nodeListIterator = nodeList.listIterator(); nodeListIterator.hasNext(); ) {
            NodeRecord currentNode = nodeListIterator.next();
            while (currentNode.getConnectionsNeededToInitiate() > 0) {
                int randomConnection = ThreadLocalRandom.current().nextInt(0, nodeList.size()-1);
                NodeRecord randomNode = nodeList.get(randomConnection);

                if(currentNode.equals(randomNode) || currentNode.getNodesToConnectToList().contains(randomNode)) {
                    //do not add and pick again
                } else {
                    currentNode.addNodeToConnectTo(randomNode);
                    updateConnections(currentNode, randomNode);
                }
            }
        }
    }

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
