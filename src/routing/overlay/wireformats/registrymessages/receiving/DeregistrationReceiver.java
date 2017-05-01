package routing.overlay.wireformats.registrymessages.receiving;

import routing.overlay.node.NodeRecord;
import routing.overlay.transport.TCPSender;
import routing.overlay.wireformats.Event;
import routing.overlay.wireformats.eventfactory.EventFactory;
import routing.overlay.wireformats.registrymessages.sending.DeregistrationResponse;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class DeregistrationReceiver {
    private Event<DeregisterRequestReceive> event;
    private Map<String, NodeRecord> nodeMap;
    private Socket destinationSocket;
    private String deregisteringHost;
    private int deregisteringPort;
    private String key;
    private EventFactory eventFactory = EventFactory.getInstance();
    private TCPSender replySender;
    private byte SUCCESS = 1;
    private byte FAILURE = 0;
    private boolean overlayEstablished;

    public DeregistrationReceiver(Event<DeregisterRequestReceive> event,
                                Map<String, NodeRecord> nodeMap,
                                Socket destinationSocket, boolean overlayEstablished) {
        this.event = event;
        this.nodeMap = nodeMap;
        this.destinationSocket = destinationSocket;
        this.overlayEstablished = overlayEstablished;

        deregisteringHost = ((DeregisterRequestReceive) event).getIdentifier();
        deregisteringPort = ((DeregisterRequestReceive) event).getPortNumber();
        key = deregisteringHost + ":" + deregisteringPort;
    }

    /**
     * Checks if a node's registered and the message origin is the same as the host/port in the message. Nodes
     * are not allowed to deregister if the overlay's been established. Otherwise, the Registry deletes the node
     * from the overlay.
     * @throws IOException
     */
    public void checkDeRegistration() throws IOException {
        if (!nodeMap.containsKey(key)) {
            sendDeregistrationResponse(destinationSocket,
                    "Unable to deregister, node is not registered!", false, FAILURE);
        } else if(!deregisteringHost.equals(destinationSocket.getInetAddress().getHostAddress())) {
            sendDeregistrationResponse(destinationSocket,
                    "Unable to deregister, IP in message does not match sender\'s IP.", false, FAILURE);
        } else if (overlayEstablished) {
            sendDeregistrationResponse(destinationSocket,
                    "Unable to deregister, overlay is already established.", false, FAILURE);
        } else {
            sendDeregistrationResponse(destinationSocket, "Deregistration successful!",
                    true, SUCCESS);
            nodeMap.remove(key);
        }
    }

    /**
     * Informs node if deregistration was successful, and if not, what error occurred.
     * @param deregisteringNode node that's trying to deregister.
     * @param error error message (if applicable).
     * @param deregisterSuccess whether the deregistration was successful (currently unused).
     * @param successOrFailure whether the deregistration was successful.
     * @throws IOException
     */
    public void sendDeregistrationResponse(Socket deregisteringNode,
                                           String error, boolean deregisterSuccess,
                                           byte successOrFailure) throws IOException {
        DeregistrationResponse deregistrationResponse = eventFactory.sendDeregistrationResponse().getType();
        deregistrationResponse.setAdditionalInfo(error);
        deregistrationResponse.setSuccessOrFailure(successOrFailure);
        replySender = new TCPSender(deregisteringNode);
        replySender.sendData(deregistrationResponse.getBytes());
    }
}
