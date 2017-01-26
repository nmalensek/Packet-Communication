package cs445.overlay.node;


import java.net.Socket;

public class NodeRecord {
    private String host;
    private int port;
    private Socket communicationSocket;

    public NodeRecord(String host, int port, Socket communicationSocket) {
        this.host = host;
        this.port = port;
        this.communicationSocket = communicationSocket;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public Socket getCommunicationSocket() {
        return communicationSocket;
    }
}
