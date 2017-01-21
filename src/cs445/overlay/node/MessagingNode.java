package cs445.overlay.node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class MessagingNode {
    private int sendTracker = 0;
    private int receiveTracker = 0;
    private int relayTracker = 0;
    private long sendSummation = 0;
    private long receiveSummation = 0;
    private Socket nodeSocket;
    private ServerSocket receivingSocket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader input;

    public MessagingNode(String host, int portNumber) throws IOException {
        nodeSocket = new Socket(host, portNumber);
        startCommunicationLinks();
        changeNodeRegistration(1);
    }

    private void startCommunicationLinks() throws IOException {
        out = new PrintWriter(nodeSocket.getOutputStream(), true);
        in = new BufferedReader(
                new InputStreamReader(nodeSocket.getInputStream()));
        input = new BufferedReader(new InputStreamReader(System.in));
    }

    public void changeNodeRegistration(int messageType) {
        String ipAddress;
        int portNumber;
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
        try {
            Socket nodeSocket = new Socket("localhost", 2500);
            PrintWriter out = new PrintWriter(nodeSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(nodeSocket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            {
                String userInput;
                while ((userInput = stdIn.readLine()) != null) {
                    out.println(userInput);
                    System.out.println("server: " + in.readLine());
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
