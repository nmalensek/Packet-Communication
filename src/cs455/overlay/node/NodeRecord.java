package cs455.overlay.node;


import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class NodeRecord {
    private String host;
    private int port;
    private Socket communicationSocket;
    private int numberOfNodeConnections;
    private int numberOfConnectionsNodeNeedsToInitiate;
    private List<NodeRecord> nodesToConnectTo = new ArrayList<>();

    public NodeRecord(String host, int port, Socket communicationSocket) {
        this.host = host;
        this.port = port;
        this.communicationSocket = communicationSocket;
    }

    public String getHost() {
        return host;
    }

    public int getPort() { return port; }

    public Socket getCommunicationSocket() {
        return communicationSocket;
    }

    public void incrementConnections() { ++numberOfNodeConnections; }

    public int getNumberOfConnections() { return numberOfNodeConnections; }

    public int getConnectionsNeededToInitiate() { return  numberOfConnectionsNodeNeedsToInitiate; }

    public void setNumberOfConnectionsNodeNeedsToInitiate(int numberOfConnectionsNodeNeedsToInitiate) {
        this.numberOfConnectionsNodeNeedsToInitiate = numberOfConnectionsNodeNeedsToInitiate;
    }

    public void decrementConnectionsToInitiate() { --numberOfConnectionsNodeNeedsToInitiate; }

    public void addNodeToConnectTo(NodeRecord node) { nodesToConnectTo.add(node); }

    public List getNodesToConnectToList() { return nodesToConnectTo; }

    public void printListSize() {
        System.out.println(nodesToConnectTo.size());
    }

    public void printNodesList() {
        String ports = getPort() + "::";
        for (NodeRecord nodeRecord : nodesToConnectTo) {
            ports += nodeRecord.getPort();
            ports += ":";
        }
        System.out.println(ports);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NodeRecord that = (NodeRecord) o;

        if (port != that.port) return false;
        return host != null ? host.equals(that.host) : that.host == null;
    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + port;
        return result;
    }
}
