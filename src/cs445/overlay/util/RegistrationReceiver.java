package cs445.overlay.util;

import cs445.overlay.node.NodeRecord;
import cs445.overlay.transport.TCPSender;
import cs445.overlay.wireformats.Event;
import cs445.overlay.wireformats.RegisterReceive;
import cs445.overlay.wireformats.RegisterResponse;
import cs445.overlay.wireformats.eventfactory.EventFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

public class RegistrationReceiver {
    private Event<RegisterReceive> event;
    private Map<NodeRecord, Integer> nodeMap;
    private Socket destinationSocket;
    private String host;
    private int port;
    private int newestPort;
    private NodeRecord nodeRecord;
    private EventFactory eventFactory = EventFactory.getInstance();
    private TCPSender replySender;
    private byte SUCCESS = 1;
    private byte FAILURE = 0;

    public RegistrationReceiver(Event<RegisterReceive> event,
                                Map<NodeRecord, Integer> nodeMap,
                                Socket destinationSocket) {
        this.event = event;
        this.nodeMap = nodeMap;
        this.destinationSocket = destinationSocket;

        host = ((RegisterReceive) event).getIdentifier();
        port = ((RegisterReceive) event).getPortNumber();
        newestPort = port;
        nodeRecord = new NodeRecord(host, port, destinationSocket);
    }

    public void checkRegistration() throws IOException {
        if (duplicateConnection(nodeRecord)) {
            processRegistration(destinationSocket, false,
                    "node already exists at that address.", FAILURE);
            nodeRecord = null;
        } else if(!host.equals(destinationSocket.getInetAddress().toString())) {
            processRegistration(destinationSocket, false,
                    "IP in message does not match sender\'s IP.", FAILURE);
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
            if (key.getPort() == portCheck && key.getHost().equals(hostCheck)) {
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

}
