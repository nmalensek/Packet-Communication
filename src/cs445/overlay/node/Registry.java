package cs445.overlay.node;

import cs445.overlay.transport.TCPSender;
import cs445.overlay.transport.TCPServerThread;
import cs445.overlay.util.RegistrationReceiver;
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
    private Map<NodeRecord, Integer> nodeMap = new HashMap<>();
    private EventFactory eventFactory = EventFactory.getInstance();
    private TCPServerThread registryServerThread;
    private int newestPort;
    private byte SUCCESS = 1;
    private byte FAILURE = 0;

    public Registry() throws IOException {
        registryServerThread = new TCPServerThread(this, portnum);
    }

    public void onEvent(Event event, Socket destinationSocket) throws IOException {
        if (event instanceof RegisterReceive) {
            RegistrationReceiver receiver = new RegistrationReceiver(
                    ((RegisterReceive) event), nodeMap, destinationSocket);
            receiver.checkRegistration();
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
