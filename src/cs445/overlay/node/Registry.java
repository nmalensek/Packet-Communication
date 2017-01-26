package cs445.overlay.node;

import cs445.overlay.transport.TCPSender;
import cs445.overlay.transport.TCPServerThread;
import cs445.overlay.wireformats.*;
import cs445.overlay.wireformats.eventfactory.EventFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Registry implements Node {

    private static int portnum;
    private TCPSender replySender;
    private Map<Integer, NodeRecord> nodeMap = new HashMap<>();
    private Event<Deregister> deregister;
    private Event<RegisterSend> registerSendEvent;
    private EventFactory eventFactory = EventFactory.getInstance();
    private TCPServerThread registryServerThread;
    private byte SUCCESS = 1;
    private byte FAILURE = 0;

    public Registry() throws IOException {
        registryServerThread = new TCPServerThread(this, portnum);
    }

    public void receiveRequest() {
        //check for registration ip address and node's ip address match and whether node's
        //already registered. prints success or failure message
        int messageType;
        byte statusCode;
        String additionalInfo; //says whether registration was successful and lists number
        //registered nodes
    }

    public void onEvent(Event event, Socket destinationSocket) throws IOException {
        if(event instanceof RegisterReceive) {
            String host = ((RegisterReceive) event).getIdentifier();
            int port = ((RegisterReceive) event).getPortNumber();
            Socket replySocket = new Socket(host, port);
            NodeRecord newNode = new NodeRecord(host, port, replySocket);
            nodeMap.put(port, newNode);
            replyToRegistration(replySocket);
        } else if(event instanceof RegResponseReceive) {
            System.out.println("something's gone wrong");
        }
    }

    public void replyToRegistration(Socket nodeThatRegistered) throws IOException {
        RegisterResponse registerResponse = eventFactory.createRegisterResponseEvent().getType();
        registerResponse.setAdditionalInfo(nodeMap.size());
        registerResponse.setSuccessOrFailure(SUCCESS);
        replySender = new TCPSender(nodeThatRegistered);
        replySender.sendData(registerResponse.getBytes());
        registerResponse.printAdditionalInfo();
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
