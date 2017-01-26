package cs445.overlay.transport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerThread {
    private ServerSocket serverSocket;
    private Socket nodeSocket;

    public TCPServerThread(int portNum) {
        try {
                ServerSocket serverSocket = new ServerSocket(portNum);
                System.out.println("Server running on port " + portNum + "...");
                nodeSocket = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getNodeSocket() { return nodeSocket; }
}
