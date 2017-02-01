package cs455.overlay.node;

import cs455.overlay.transport.TCPReceiverThread;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.TextInputThread;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.nodemessages.Deregister;
import cs455.overlay.wireformats.nodemessages.ReceiveDeregisterResponse;
import cs455.overlay.wireformats.nodemessages.ReceiveRegistryResponse;
import cs455.overlay.wireformats.nodemessages.SendRegister;
import cs455.overlay.wireformats.eventfactory.EventFactory;

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
    private String registryHostName;
    private int registryPort;
    private int randomPort;
    private Socket registrySocket;
    private TCPSender registrySender;
    private TCPServerThread receivingSocket;
    private EventFactory eF = EventFactory.getInstance();
    private byte[] message;
    private String command;

    public MessagingNode(String registryHostName, int registryPort) throws IOException {
        this.registryHostName = registryHostName;
        this.registryPort = registryPort;
        registrySocket = new Socket(registryHostName, registryPort);
        registrySender = new TCPSender(registrySocket);
    }

    private void startUp() throws IOException {
        chooseRandomPort();
        TCPReceiverThread receiverThread = new TCPReceiverThread(registrySocket, this);
        receiverThread.start();
        register();
        createServerThread();
        listenForTextInput();
    }

    private void chooseRandomPort() {
        randomPort = ThreadLocalRandom.current().nextInt(49152, 65535);
    }

    private void register() throws IOException {
        SendRegister sendRegister = eF.createRegisterSendEvent().getType();
        sendRegister.setHostAndPort(registrySocket.getLocalAddress().toString(), randomPort);
        message = sendRegister.getBytes();
        registrySender.sendData(message);
    }

    private void deregister() throws IOException {
        Deregister deregister = eF.createDeregistrationEvent().getType();
        deregister.setHostAndPort(registrySocket.getLocalAddress().toString(), randomPort);
        message = deregister.getBytes();
        registrySender.sendData(message);
    }

    private void createServerThread() throws IOException {
        receivingSocket = new TCPServerThread(this, randomPort);
        receivingSocket.start();
    }

    public void onEvent(Event event, Socket destinationSocket) throws IOException {
        if (event instanceof ReceiveRegistryResponse) {
            ((ReceiveRegistryResponse) event).printMessage();
        } else if (event instanceof ReceiveDeregisterResponse) {
            ((ReceiveDeregisterResponse) event).printMessage();
        }
    }

    public void processText(String command) throws IOException {
        switch (command) {
            case "print-shortest-path":
                printShortestPath();
                break;
            case "exit-overlay":
                deregister();
                break;
            default:
                System.out.println("Not a valid command.");
        }
    }

    private void listenForTextInput() throws IOException {
        TextInputThread textInputThread = new TextInputThread(this);
        textInputThread.start();
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
