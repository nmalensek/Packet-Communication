package cs445.overlay.node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Registry {

    private static int portnum;
    private static String hostname = "localhost";
    private static ServerSocket nodeRegistry;

    public void receiveRequest() {
        //check for registration ip address and node's ip address match and whether node's
        //already registered. prints success or failure message
        int messageType;
        byte statusCode;
        String additionalInfo; //says whether registration was successful and lists number
        //registered nodes
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

    public static void main(String[] args) {
        portnum = Integer.parseInt(args[0]);
        try {
            ServerSocket nodeRegistry = new ServerSocket(portnum);
            Socket nodeSocket = nodeRegistry.accept();
            PrintWriter out = new PrintWriter(nodeSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(nodeSocket.getInputStream()));
            System.out.println("Server running, waiting for input...");
            {
                String textFromClient;
                String textToClient;
                while((textFromClient = in.readLine()) !=null) {
                    if(textFromClient.equals("1")) {
                        textToClient = "#1";
                        System.out.println("sending " + textToClient);
                        out.println(textToClient);
                    } else {
                        System.out.println(nodeSocket.getOutputStream());
                        out.println(textFromClient);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
