package cs445.overlay.node;

import cs445.overlay.transport.TCPReceiverThread;
import cs445.overlay.transport.TCPSender;
import cs445.overlay.transport.TCPServerThread;
import cs445.overlay.wireformats.Event;
import cs445.overlay.wireformats.RegisterResponse;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;

public class Registry implements Node {

    private static int portnum;
    private TCPSender sender;
    private TCPReceiverThread receiverThread;
    private static ServerSocket nodeRegistry;
    private Set nodeSet = new HashSet();

    public Registry() throws IOException {
        TCPServerThread tcpServerThread = new TCPServerThread(portnum);
        sender = new TCPSender(tcpServerThread.getNodeSocket());
        receiverThread = new TCPReceiverThread(tcpServerThread.getNodeSocket(), this);
        receiverThread.run();
    }

    public void receiveRequest() {
        //check for registration ip address and node's ip address match and whether node's
        //already registered. prints success or failure message
        int messageType;
        byte statusCode;
        String additionalInfo; //says whether registration was successful and lists number
        //registered nodes
    }

    public void onEvent(Event event, byte[] bytes) throws IOException {
        if(event.getType() == 2) {
            RegisterResponse registerResponse = new RegisterResponse();
            registerResponse.unmarshallBytes(bytes);

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
