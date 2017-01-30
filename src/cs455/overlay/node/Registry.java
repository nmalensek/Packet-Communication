package cs455.overlay.node;

import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.RegistrationReceiver;
import cs455.overlay.wireformats.registrymessages.ReceiveRegisterRequest;
import cs455.overlay.wireformats.Event;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Registry implements Node {

    private static int portNum;
    private List<NodeRecord> nodeList = new ArrayList<>();

    public void startServer() {
        TCPServerThread registryServerThread = new TCPServerThread(this, portNum);
    }

    public void onEvent(Event event, Socket destinationSocket) throws IOException {
        if (event instanceof ReceiveRegisterRequest) {
            RegistrationReceiver receiver = new RegistrationReceiver(
                    ((ReceiveRegisterRequest) event), nodeList, destinationSocket);
            receiver.checkRegistration();
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

        nextNode.decrementNeededConnections();
        nextNode.incrementConnections();
    }

    private void connectToRandomNodes() {
        for (ListIterator<NodeRecord> nodeListIterator = nodeList.listIterator(); nodeListIterator.hasNext(); ) {
            NodeRecord currentNode = nodeListIterator.next();
            while (currentNode.getConnectionsNeeded() > 0) {
                //connect randomly to other nodes
                //if randomElement.getHost and .getPort = currentNode or already exist in node's list,
                //don't add and pick again
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
