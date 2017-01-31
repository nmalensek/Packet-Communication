package cs455.overlay.util;

import cs455.overlay.node.NodeRecord;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.registrymessages.ReceiveRegisterRequest;
import cs455.overlay.wireformats.registrymessages.RespondToRegisterRequest;
import cs455.overlay.wireformats.eventfactory.EventFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

public class RegistrationReceiver {
    private Event<ReceiveRegisterRequest> event;
    private List<NodeRecord> nodeList;
    private Socket destinationSocket;
    private String host;
    private int port;
    private int newestPort;
    private NodeRecord nodeRecord;
    private EventFactory eventFactory = EventFactory.getInstance();
    private TCPSender replySender;
    private byte SUCCESS = 1;
    private byte FAILURE = 0;

    public RegistrationReceiver(Event<ReceiveRegisterRequest> event,
                                List<NodeRecord> nodeList,
                                Socket destinationSocket) {
        this.event = event;
        this.nodeList = nodeList;
        this.destinationSocket = destinationSocket;

        host = ((ReceiveRegisterRequest) event).getIdentifier();
        port = ((ReceiveRegisterRequest) event).getPortNumber();
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
            nodeList.add(nodeRecord);
            try {
                processRegistration(destinationSocket, true, null, SUCCESS);
            } catch (SocketException e) {
                //remove node that just registered if it fails after sending its message
                System.out.println("Unable to contact node, removing from registered nodes...");
                nodeList.remove(nodeRecord);
                System.out.println("Node removed.");
            }
        }
    }

    public void processRegistration(Socket nodeThatRegistered, boolean isSuccessfulConnection,
                                    String error, byte successOrFailure) throws IOException, SocketException {
        RespondToRegisterRequest respondToRegisterRequest = eventFactory.createRegisterResponseEvent().getType();
        respondToRegisterRequest.setAdditionalInfo(registerResponseAdditionalInfo(isSuccessfulConnection, error));
        respondToRegisterRequest.setSuccessOrFailure(successOrFailure);
        replySender = new TCPSender(nodeThatRegistered);
        replySender.sendData(respondToRegisterRequest.getBytes());
    }

    private boolean duplicateConnection(NodeRecord newNodeRecord) {
        boolean isDuplicate = false;
        for (NodeRecord node : nodeList) {
            if (node.equals(newNodeRecord)) {
                isDuplicate = true;
                break;
            }
        }
        return isDuplicate;
    }

    private String registerResponseAdditionalInfo(boolean successfulConnection, String errorMessage) {
        if (successfulConnection) {
            return "Nodes registered: " + nodeList.size();
        } else {
            return "Registration unsuccessful, error message: " + errorMessage;
        }
    }

}
