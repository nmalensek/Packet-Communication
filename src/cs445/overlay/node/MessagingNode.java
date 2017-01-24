package cs445.overlay.node;

import cs445.overlay.transport.TCPReceiverThread;
import cs445.overlay.transport.TCPSender;
import cs445.overlay.transport.TCPServerThread;
import cs445.overlay.wireformats.Event;
import cs445.overlay.wireformats.RegisterSend;
import cs445.overlay.wireformats.eventfactory.EventFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ThreadLocalRandom;

public class MessagingNode implements Node {
    private int sendTracker = 0;
    private int receiveTracker = 0;
    private int relayTracker = 0;
    private long sendSummation = 0;
    private long receiveSummation = 0;
    private int randomPort;
    private Socket registrySocket;
    private Socket transmitSocket;
    private TCPServerThread receivingSocket;
    private EventFactory eF = EventFactory.getInstance();
    private byte[] bytesToSend;

    public MessagingNode(String host, int portNumber) throws IOException {
        registrySocket = new Socket(host, portNumber);
        createServerSocket();
    }

    private void createServerSocket() throws IOException {
        chooseRandomPort();
        RegisterSend registerSend = eF.createRegisterSendEvent().getType();
        registerSend.setHostAndPort("localhost", 4444);
        onEvent(registerSend);
        receivingSocket = new TCPServerThread(4444);
        transmitSocket = receivingSocket.getNodeSocket();
    }

    public void onEvent(Event event) throws IOException {
        bytesToSend = event.getBytes();
        TCPSender sender = new TCPSender(registrySocket);
        sender.sendData(bytesToSend);
    }

    private void connectToNode(String host, int port) {

    }

    private void chooseRandomPort() {
        randomPort = ThreadLocalRandom.current().nextInt(49152, 65535);
    }

    public void initiateConnection() {
        int numberOfConnections;
    }

    public void acceptLinkWeights() {

    }

    public void acceptMessage() throws IOException {
        TCPReceiverThread receiver = new TCPReceiverThread(transmitSocket, this);
        receiver.run();
    }

    public void printShortestPath() {

    }

    public void exitOverlay() {

    }

    public static void main(String[] args) {
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        try {
            while(true) {
                MessagingNode messagingNode = new MessagingNode(host, port);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
