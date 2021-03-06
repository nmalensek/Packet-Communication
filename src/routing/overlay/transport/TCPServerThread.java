package routing.overlay.transport;

import routing.overlay.node.Node;
import routing.overlay.wireformats.nodemessages.Sending.BindExceptionHappened;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * code adapted from code provided by instructor at http://www.cs.colostate.edu/~cs455/lectures/CS455-HelpSession1.pdf
 */

public class TCPServerThread extends Thread {
    private Node node;
    private int portNum;
    private ServerSocket serverSocket;
    private Socket unusedSocket;

    public TCPServerThread(Node node, int portNum) {
        this.node = node;
        this.portNum = portNum;
    }

    public int getPortNumber() {
        return portNum;
    }

    /**
     * The program has intermittent BindExceptions when using the department machines, so the afflicted node now
     * deregisters itself through the first catch statement.
     */
    public void run() {
        try {
            serverSocket = new ServerSocket(portNum);
            System.out.println("Server running on port " + serverSocket.getLocalPort() + "...");
            while(true) {
                new TCPReceiverThread(serverSocket.accept(), node).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            BindExceptionHappened bindExceptionHappened = new BindExceptionHappened();
            try {
                node.onEvent(bindExceptionHappened, unusedSocket);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
