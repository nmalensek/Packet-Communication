package cs445.overlay.node;

import cs445.overlay.transport.TCPServerThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ThreadLocalRandom;

public class MessagingNode {
    private int sendTracker = 0;
    private int receiveTracker = 0;
    private int relayTracker = 0;
    private long sendSummation = 0;
    private long receiveSummation = 0;
    private int randomPort;
    private int test;
    private Socket registrySocket;
    private TCPServerThread receivingSocket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader input;

    public MessagingNode(String host, int portNumber) throws IOException {
        registrySocket = new Socket(host, portNumber);
        startCommunicationLinks();
        createServerSocket();
        sendMessage(1);
    }

    private void createServerSocket() throws IOException {
        chooseRandomPort();
        receivingSocket = new TCPServerThread(randomPort);
        test = receivingSocket.retrievePortNum();
        System.out.println(test);
    }

    private void startCommunicationLinks() throws IOException {
        out = new PrintWriter(registrySocket.getOutputStream(), true);
        in = new BufferedReader(
                new InputStreamReader(registrySocket.getInputStream()));
        input = new BufferedReader(new InputStreamReader(System.in));
    }

    public void sendMessage(int messageType) {
        String ipAddress;
        int portNumber;
    }

    private void chooseRandomPort() {
        randomPort = ThreadLocalRandom.current().nextInt(49152, 65535);
    }

    public void initiateConnection() {
        int numberOfConnections;
    }

    public void acceptLinkWeights() {

    }

    public void acceptMessage() {

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
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
