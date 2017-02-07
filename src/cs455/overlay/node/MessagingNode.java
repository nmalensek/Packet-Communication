package cs455.overlay.node;

import cs455.overlay.transport.TCPReceiverThread;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.TextInputThread;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.nodemessages.*;
import cs455.overlay.wireformats.eventfactory.EventFactory;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class MessagingNode implements Node {
    private int sendTracker = 0;
    private int receiveTracker = 0;
    private int relayTracker = 0;
    private long sendSummation = 0;
    private long receiveSummation = 0;
    private int numberOfConnections = 0;
    private String registryHostName;
    private int registryPort;
    private int randomPort;
    private Socket registrySocket;
    private TCPSender registrySender;
    private TCPServerThread receivingSocket;
    private EventFactory eF = EventFactory.getInstance();
    private byte[] message;
    private Map<String, NodeRecord> nodeConnections = new HashMap<>();

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
        sendRegister.setHostAndPort(Inet4Address.getLocalHost().getHostAddress(), randomPort);
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
            registrySocket.close();
        } else if (event instanceof ReceiveMessagingNodesList) {
           processMessagingNodesList(((ReceiveMessagingNodesList) event).getNodesToConnectTo());
        } else if (event instanceof NodeConnection) {
            processNewConnection(((NodeConnection) event).getNodeID());
            if (nodeConnections.size() == 4) {
                System.out.println("All connections established. Number of connections: "
                        + nodeConnections.size());
            }
        }
    }

    private void processMessagingNodesList(String stringToSplit) throws IOException {
        String[] splitByNewLine = stringToSplit.split("\\n");
        for(String nodeID : splitByNewLine) {
            String[] splitIDApart = nodeID.split(":");
            String host = splitIDApart[0];
            int port = Integer.parseInt(splitIDApart[1]);
            NodeRecord nodeToInform = cacheConnection(host, port, nodeID);
            tellOtherNodeAboutConnection(nodeToInform);
        }
    }

    //splits apart id, stores connection, and ignores returned Vertex
    private void processNewConnection(String nodeIDLine) throws IOException {
        String[] splitIDApart = nodeIDLine.split(":");
        String host = splitIDApart[0];
        int port = Integer.parseInt(splitIDApart[1]);
        cacheConnection(host, port, nodeIDLine);
    }

    private NodeRecord cacheConnection(String host, int port, String nodeID) throws IOException {
        Socket nodeSocket = new Socket(host, port);
        NodeRecord newNodeRecord = new NodeRecord(host, port, nodeSocket);
        TCPReceiverThread receiverThread = new TCPReceiverThread(nodeSocket, this);
        newNodeRecord.setReceiver(receiverThread);
        nodeConnections.put(nodeID, newNodeRecord);
        return newNodeRecord;
    }

    private void tellOtherNodeAboutConnection(NodeRecord nodeConnectingTo) throws IOException {
        NodeConnection nodeConnection = eF.sendNodeConnection().getType();
        String thisNodeHost = Inet4Address.getLocalHost().getHostAddress();
        int thisNodePort = randomPort;
        String thisNodeID = thisNodeHost + ":" + Integer.toString(thisNodePort);
        nodeConnection.setNodeID(thisNodeID);
        nodeConnectingTo.getSender().sendData(nodeConnection.getBytes());
    }

    public void processText(String command) throws IOException {
        switch (command) {
            case "print-shortest-path":
                printShortestPath();
                break;
            case "exit-overlay":
                deregister();
                break;
            case "list-connections":
                printConnections();
                break;
            default:
                System.out.println("Not a valid command.");
        }
    }

    private void listenForTextInput() throws IOException {
        TextInputThread textInputThread = new TextInputThread(this);
        textInputThread.start();
    }

    private void printConnections() {
        if(nodeConnections.size() == 0) {
            System.out.println("No connections available, is the overlay set up?");
        } else{
            for (String s : nodeConnections.keySet()) {
                System.out.println(s);
            }
        }
    }

    private void getConnectionCount() {
        nodeConnections.size();
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
