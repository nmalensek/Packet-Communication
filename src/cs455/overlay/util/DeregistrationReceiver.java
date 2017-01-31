package cs455.overlay.util;

import cs455.overlay.node.NodeRecord;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.eventfactory.EventFactory;
import cs455.overlay.wireformats.registrymessages.DeregistrationResponse;
import cs455.overlay.wireformats.registrymessages.ReceiveDeregisterRequest;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class DeregistrationReceiver {
    private Event<ReceiveDeregisterRequest> event;
    private List<NodeRecord> nodeList;
    private Socket destinationSocket;
    private String deregisteringHost;
    private int deregisteringPort;
    private int newestPort;
    private NodeRecord nodeRecord;
    private EventFactory eventFactory = EventFactory.getInstance();
    private TCPSender replySender;
    private byte SUCCESS = 1;
    private byte FAILURE = 0;

    public DeregistrationReceiver(Event<ReceiveDeregisterRequest> event,
                                List<NodeRecord> nodeList,
                                Socket destinationSocket) {
        this.event = event;
        this.nodeList = nodeList;
        this.destinationSocket = destinationSocket;

        deregisteringHost = ((ReceiveDeregisterRequest) event).getIdentifier();
        deregisteringPort = ((ReceiveDeregisterRequest) event).getPortNumber();
    }

    public void checkDeRegistration() throws IOException {
        if (wasNotRegistered(deregisteringHost, deregisteringPort)) {
            sendDeregistrationResponse(destinationSocket,
                    "Unable to deregister, node is not registered!", false, FAILURE);
        } else if(!deregisteringHost.equals(destinationSocket.getInetAddress().toString())) {
            sendDeregistrationResponse(destinationSocket,
                    "Unable to deregister, IP in message does not match sender\'s IP.", false, FAILURE);
        } else {
            sendDeregistrationResponse(destinationSocket, "Deregistration successful!",
                    true, SUCCESS);
            completeDeregistration(deregisteringHost, deregisteringPort);
        }
    }

    public void sendDeregistrationResponse(Socket deregisteringNode,
                                           String error, boolean deregisterSuccess,
                                           byte successOrFailure) throws IOException {
        DeregistrationResponse deregistrationResponse = eventFactory.sendDeregistrationResponse().getType();
        deregistrationResponse.setAdditionalInfo(error);
        replySender = new TCPSender(deregisteringNode);
        replySender.sendData(deregistrationResponse.getBytes());
    }

    private void completeDeregistration(String host, int port) throws IOException {
        for (NodeRecord node : nodeList) {
            if (node.getHost().equals(host) && node.getPort() == port) {
                nodeList.remove(node);
                break;
            }
        }
    }

    private boolean wasNotRegistered(String host, int port) {
        boolean wasNotRegistered = true;
        for (NodeRecord node : nodeList) {
            if (node.getHost().equals(host) && node.getPort() == port) {
                wasNotRegistered = false;
                break;
            }
        }
        return wasNotRegistered;
    }
}
