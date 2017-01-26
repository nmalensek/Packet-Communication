package cs445.overlay.node;

import cs445.overlay.transport.TCPReceiverThread;
import cs445.overlay.transport.TCPSender;
import cs445.overlay.transport.TCPServerThread;
import cs445.overlay.wireformats.*;
import cs445.overlay.wireformats.eventfactory.EventFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Registry implements Node {

    private static int portnum;
    private TCPSender replySender;
    private Map<String, Integer> nodeMap = new HashMap<>();
    private Event<Deregister> deregister;
    private Event<RegisterSend> registerSendEvent;
    private EventFactory eventFactory = EventFactory.getInstance();
    private int numberOfNodes = 0;
    private TCPServerThread registerServerThread;

    public Registry() throws IOException {
        registerServerThread = new TCPServerThread(this, portnum);
    }

    public void receiveRequest() {
        //check for registration ip address and node's ip address match and whether node's
        //already registered. prints success or failure message
        int messageType;
        byte statusCode;
        String additionalInfo; //says whether registration was successful and lists number
        //registered nodes
    }

    public void onEvent(Event event) throws IOException {
        if(event instanceof RegisterReceive) {
            String host = ((RegisterReceive) event).getIdentifier();
            int port = ((RegisterReceive) event).getPortNumber();
            nodeMap.put(host, port);

        }
    }

    public void createResponse() throws IOException {
        RegisterResponse registerResponse = eventFactory.createRegisterResponseEvent().getType();
        registerResponse.setNodes(numberOfNodes);
        Socket nodeSocket = new Socket("localhost", 4444);
        TCPSender tcpSender = new TCPSender(nodeSocket, this);
        tcpSender.sendData(registerResponse.getBytes());
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
