package cs455.overlay.transport;

import cs455.overlay.node.Node;

import java.io.IOException;
import java.net.ServerSocket;

public class TCPServerThread extends Thread {
    private Node node;
    private int portNum;
    private ServerSocket serverSocket;

    public TCPServerThread(Node node, int portNum) {
        this.node = node;
        this.portNum = portNum;
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(portNum);
            System.out.println("Server running on port " + portNum + "...");
            while(true) {
                new TCPReceiverThread(serverSocket.accept(), node).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
