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
    private Map<Integer, String> nodeMap = new HashMap<>();
    private EventFactory eventFactory = EventFactory.getInstance();
    private TCPServerThread registryServerThread;
    private int newestPort;
    private byte SUCCESS = 1;
    private byte FAILURE = 0;

    public Registry() throws IOException {
        registryServerThread = new TCPServerThread(this, portnum);
    }

    public void onEvent(Event event, Socket destinationSocket) throws IOException {
        if(event instanceof RegisterReceive) {
            String host = ((RegisterReceive) event).getIdentifier();
            int port = ((RegisterReceive) event).getPortNumber();
            nodeMap.put(port, host);
            newestPort = port;
            receiveRequest(destinationSocket);
        }
    }

    public void receiveRequest(Socket nodeThatRegistered) throws IOException {
        try {
            RegisterResponse registerResponse = eventFactory.createRegisterResponseEvent().getType();
            registerResponse.setAdditionalInfo(nodeMap.size());
            registerResponse.setSuccessOrFailure(SUCCESS);
            replySender = new TCPSender(nodeThatRegistered);
            replySender.sendData(registerResponse.getBytes());
            //TODO add failure conditions and failure message
        } catch (SocketException e) {
            //remove node that just registered if it fails after sending its message
            nodeMap.remove(newestPort);
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
