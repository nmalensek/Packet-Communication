package cs455.overlay.wireformats.registrymessages.receiving;

import cs455.overlay.node.NodeRecord;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.registrymessages.sending.RespondToRegisterRequest;
import cs455.overlay.wireformats.eventfactory.EventFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

public class RegistrationReceiver {
    private Event<ReceiveRegisterRequest> event;
    private Map<String, NodeRecord> nodeMap;
    private Socket destinationSocket;
    private String host;
    private int port;
    private NodeRecord nodeRecord;
    private EventFactory eventFactory = EventFactory.getInstance();
    private TCPSender replySender;
    private byte SUCCESS = 1;
    private byte FAILURE = 0;
    private String key;

    public RegistrationReceiver(Event<ReceiveRegisterRequest> event,
                                Map<String, NodeRecord> nodeMap,
                                Socket destinationSocket) throws IOException {
        this.event = event;
        this.nodeMap = nodeMap;
        this.destinationSocket = destinationSocket;

        host = ((ReceiveRegisterRequest) event).getIdentifier();
        port = ((ReceiveRegisterRequest) event).getPortNumber();
        key = host + ":" + port;
        nodeRecord = new NodeRecord(host, port, destinationSocket);
    }

    public void checkRegistration() throws IOException {
        if (nodeMap.containsKey(key)) {
            processRegistration(destinationSocket, false,
                    "node already exists at that address.", FAILURE);
            nodeRecord = null;
        } else if(!host.equals(destinationSocket.getInetAddress().toString())) {
            processRegistration(destinationSocket, false,
                    "IP in message does not match sender\'s IP.", FAILURE);
            nodeRecord = null;
        } else {
            nodeMap.put(key, nodeRecord);
            try {
                processRegistration(destinationSocket, true, null, SUCCESS);
            } catch (SocketException e) {
                //remove node that just registered if it fails after sending its message
                System.out.println("Unable to contact node, removing from registered nodes...");
                nodeMap.remove(key);
                System.out.println("Node removed.");
            }
        }
    }

    public void processRegistration(Socket nodeThatRegistered, boolean isSuccessfulConnection,
                                    String error, byte successOrFailure) throws IOException, SocketException {
        RespondToRegisterRequest respondToRegisterRequest = eventFactory.createRegisterResponseEvent().getType();
        respondToRegisterRequest.setAdditionalInfo(registerResponseAdditionalInfo(isSuccessfulConnection, error));
        respondToRegisterRequest.setSuccessOrFailure(successOrFailure);
        TCPSender replySender = nodeRecord.getSender();
        replySender.sendData(respondToRegisterRequest.getBytes());
    }

    private String registerResponseAdditionalInfo(boolean successfulConnection, String errorMessage) {
        if (successfulConnection) {
            return "Registration successful. Nodes registered: " + nodeMap.size();
        } else {
            return "Registration unsuccessful, error message: " + errorMessage;
        }
    }

}
