package cs445.overlay.transport;

import cs445.overlay.node.Node;

import java.io.IOException;
import java.net.ServerSocket;

public class TCPServerThread {
    private ServerSocket serverSocket;
    private TCPReceiverThread tcpReceiverThread;

    public TCPServerThread(Node node, int portNum) {
        try {
            while(true) {
                serverSocket = new ServerSocket(portNum);
                System.out.println("Server running on port " + portNum + "...");
                tcpReceiverThread = new TCPReceiverThread(serverSocket.accept(), node);
                tcpReceiverThread.run();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
