package routing.overlay.wireformats.registrymessages.receiving;

import routing.overlay.node.NodeRecord;
import routing.overlay.transport.TCPSender;
import routing.overlay.wireformats.Event;
import routing.overlay.wireformats.registrymessages.sending.RegisterRequestResponse;
import routing.overlay.wireformats.eventfactory.EventFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

public class RegistrationReceiver {
    private Event<RegisterRequestReceive> event;
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

    public RegistrationReceiver(Event<RegisterRequestReceive> event,
                                Map<String, NodeRecord> nodeMap,
                                Socket destinationSocket) throws IOException {
        this.event = event;
        this.nodeMap = nodeMap;
        this.destinationSocket = destinationSocket;

        host = ((RegisterRequestReceive) event).getIdentifier();
        port = ((RegisterRequestReceive) event).getPortNumber();
        key = host + ":" + port;
        nodeRecord = new NodeRecord(host, port, destinationSocket);
    }

    /**
     * Checks if a node's already registered and that sender is the node trying to register. Stores knowledge of node
     * if no errors occur. Also includes error handling for when the node's socket is closed before the registration
     * response is sent.
     * @throws IOException
     */
    public synchronized void checkRegistration() throws IOException {
        if (nodeMap.containsKey(key)) {
            processRegistration(destinationSocket, false,
                    "node already exists at that address.", FAILURE);
            nodeRecord = null;
        } else if(!host.equals(destinationSocket.getInetAddress().getHostAddress())) {
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

    /**
     * Sends success or failure response to the registering node.
     * @param nodeThatRegistered node that sent the original message.
     * @param isSuccessfulConnection whether the node's connection was successful.
     * @param error error if one occurred.
     * @param successOrFailure if registration succeeded or failed.
     * @throws IOException
     * @throws SocketException
     */
    public synchronized void processRegistration(Socket nodeThatRegistered, boolean isSuccessfulConnection,
                                    String error, byte successOrFailure) throws IOException, SocketException {
        RegisterRequestResponse registerRequestResponse = eventFactory.createRegisterResponseEvent().getType();
        registerRequestResponse.setAdditionalInfo(registerResponseAdditionalInfo(isSuccessfulConnection, error));
        registerRequestResponse.setSuccessOrFailure(successOrFailure);
        TCPSender replySender = nodeRecord.getSender();
        replySender.sendData(registerRequestResponse.getBytes());
    }

    private String registerResponseAdditionalInfo(boolean successfulConnection, String errorMessage) {
        if (successfulConnection) {
            return "Registration successful. Nodes registered: " + nodeMap.size();
        } else {
            return "Registration unsuccessful, error message: " + errorMessage;
        }
    }

}
