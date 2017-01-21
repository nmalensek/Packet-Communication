package cs445.overlay.transport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by nicholas on 1/20/17.
 */
public class TCPServerThread {
    private ServerSocket serverSocket;

    public TCPServerThread(int portNum) {
        try {
            ServerSocket serverSocket = new ServerSocket(portNum);
            while(true) {
                System.out.println("Server running, listening on port " + portNum + "...");
                Socket nodeSocket = serverSocket.accept();
                System.out.println(nodeSocket + "successfully connected!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int retrievePortNum() {
        return serverSocket.getLocalPort();
    }
}
