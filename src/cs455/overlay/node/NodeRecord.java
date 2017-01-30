package cs455.overlay.node;


import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class NodeRecord {
    private String host;
    private int port;
    private Socket communicationSocket;
    private int numberOfNodeConnections;
    private int numberOfConnectionsNodeNeedsToInitiate = 4;
    private List<NodeRecord> nodesToConnectTo = new ArrayList<>();

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

    public void incrementConnections() { ++numberOfNodeConnections; }

    public int getNumberOfConnections() { return numberOfNodeConnections; }

    public int getConnectionsNeeded() { return  numberOfConnectionsNodeNeedsToInitiate; }

    public void decrementNeededConnections() { --numberOfConnectionsNodeNeedsToInitiate; }

    public void addNodeToConnectTo(NodeRecord node) { nodesToConnectTo.add(node); }

    public int getLengthOfNodeList() { return nodesToConnectTo.size(); }
}
