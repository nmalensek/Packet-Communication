package cs445.overlay.node;

import cs445.overlay.transport.TCPSender;
import cs445.overlay.transport.TCPServerThread;
import cs445.overlay.wireformats.*;
import cs445.overlay.wireformats.eventfactory.EventFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class Registry implements Node {

    private static int portnum;
    private TCPSender replySender;
    private Map<NodeRecord, Integer> nodeMap = new HashMap<>();
    private EventFactory eventFactory = EventFactory.getInstance();
    private TCPServerThread registryServerThread;
    private int newestPort;
    private byte SUCCESS = 1;
    private byte FAILURE = 0;

    public Registry() throws IOException {
        registryServerThread = new TCPServerThread(this, portnum);
    }

    public void onEvent(Event event, Socket destinationSocket) throws IOException {
        if (event instanceof RegisterReceive) {
            String host = ((RegisterReceive) event).getIdentifier();
            int port = ((RegisterReceive) event).getPortNumber();
            newestPort = port;
            NodeRecord nodeRecord = new NodeRecord(host, port, destinationSocket);
            if (duplicateConnection(nodeRecord)) {
                processRegistration(destinationSocket, false,
                        "node already exists at that address.", FAILURE);
                nodeRecord = null;
            } else {
                nodeMap.put(nodeRecord, port);
                try {
                    processRegistration(destinationSocket, true, null, SUCCESS);
                } catch (SocketException e) {
                    //remove node that just registered if it fails after sending its message
                    System.out.println("Unable to contact node, removing from registered nodes...");
                    nodeMap.remove(nodeRecord);
                    System.out.println("Node removed.");
                }
            }
        }
    }

    private void registrationFailure(Socket nodeConnection) {

    }

    public void processRegistration(Socket nodeThatRegistered, boolean isSuccessfulConnection,
                                    String error, byte successOrFailure) throws IOException, SocketException {
            RegisterResponse registerResponse = eventFactory.createRegisterResponseEvent().getType();
            registerResponse.setAdditionalInfo(registerResponseAdditionalInfo(isSuccessfulConnection, error));
            registerResponse.setSuccessOrFailure(successOrFailure);
            replySender = new TCPSender(nodeThatRegistered);
            replySender.sendData(registerResponse.getBytes());
    }

    private boolean duplicateConnection(NodeRecord nodeRecord) {
        int portCheck = nodeRecord.getPort();
        String hostCheck = nodeRecord.getHost();
        boolean isDuplicate = false;
            for (NodeRecord key : nodeMap.keySet()) {
                if(key.getPort() == portCheck && key.getHost().equals(hostCheck)) {
                    isDuplicate = true;
                    break;
                }
            }
        return isDuplicate;
    }

    private String registerResponseAdditionalInfo(boolean successfulConnection, String errorMessage) {
        if (successfulConnection) {
            return "Nodes registered: " + nodeMap.size();
        } else {
            return "Registration unsuccessful, error message: " + errorMessage;
        }
    }

    public void assignLinkWeights() {

    }

    public void initiateTask() {

    }

    public void listMessagingNodes() {

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
        portnum = Integer.parseInt(args[0]);
        Registry registry = new Registry();
    }
}
