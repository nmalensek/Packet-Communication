package cs445.overlay.node;

import cs445.overlay.transport.TCPReceiverThread;
import cs445.overlay.transport.TCPSender;
import cs445.overlay.transport.TCPServerThread;
import cs445.overlay.wireformats.Event;
import cs445.overlay.wireformats.ReceiveRegistryResponse;
import cs445.overlay.wireformats.RegisterResponse;
import cs445.overlay.wireformats.RegisterSend;
import cs445.overlay.wireformats.eventfactory.EventFactory;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ThreadLocalRandom;

public class MessagingNode implements Node {
    private int sendTracker = 0;
    private int receiveTracker = 0;
    private int relayTracker = 0;
    private long sendSummation = 0;
    private long receiveSummation = 0;
    private String registryHostName;
    private int registryPort;
    private int randomPort;
    private Socket registrySocket;
    private TCPServerThread receivingSocket;
    private EventFactory eF = EventFactory.getInstance();
    private byte[] bytesToSend;

    public MessagingNode(String registryHostName, int registryPort) throws IOException {
        this.registryHostName = registryHostName;
        this.registryPort = registryPort;
        registrySocket = new Socket(registryHostName, registryPort);
    }

    private void startUp() throws IOException {
        TCPReceiverThread receiverThread = new TCPReceiverThread(registrySocket, this);
        chooseRandomPort();
        register();
        receiverThread.start();
        createServerSocket();
    }

    private void chooseRandomPort() {
        randomPort = ThreadLocalRandom.current().nextInt(49152, 65535);
    }

    private void register() throws IOException {
        RegisterSend registerSend = eF.createRegisterSendEvent().getType();
        registerSend.setHostAndPort(registrySocket.getLocalAddress().toString(), randomPort);
        onEvent(registerSend, registrySocket);
    }

    private void createServerSocket() throws IOException {
        receivingSocket = new TCPServerThread(this, randomPort);
    }

    public void onEvent(Event event, Socket destinationSocket) throws IOException {
        if (event instanceof RegisterSend) {
            bytesToSend = event.getBytes();
            TCPSender sender = new TCPSender(destinationSocket);
            sender.sendData(bytesToSend);
        } else if (event instanceof ReceiveRegistryResponse) {
            ((ReceiveRegistryResponse) event).printMessage();
        } else if (event instanceof RegisterResponse) {

        }
    }

    private void connectToNode(String host, int port) {

    }

    public void initiateConnection() {
        int numberOfConnections;
    }

    public void acceptLinkWeights() {

    }

    public void acceptMessage() throws IOException {

    }

    public void printShortestPath() {

    }

    public void exitOverlay() {

    }

    public static void main(String[] args) {
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        try {
                MessagingNode messagingNode = new MessagingNode(host, port);
                messagingNode.startUp();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
