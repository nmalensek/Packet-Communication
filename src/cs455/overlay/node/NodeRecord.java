package cs455.overlay.node;


import cs455.overlay.transport.TCPReceiverThread;
import cs455.overlay.transport.TCPSender;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class NodeRecord {
    private String host;
    private int port;
    private Socket communicationSocket;
    private int numberOfNodeConnections;
    private int numberOfConnectionsNodeNeedsToInitiate;
    private String nodeID;
    private List<NodeRecord> nodesToConnectTo = new ArrayList<>();
    private TCPSender sender;
    private TCPReceiverThread receiver;

    public NodeRecord(String host, int port, Socket communicationSocket) throws IOException {
        this.host = host;
        this.port = port;
        this.nodeID = host + ":" + port;
        this.communicationSocket = communicationSocket;
        this.sender = new TCPSender(communicationSocket);
    }

    public void resetNumberOfConnections() {
        numberOfNodeConnections = 0;
    }

    public String getHost() {
        return host;
    }

    public int getPort() { return port; }

    public String getNodeID() { return nodeID; }

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

    public TCPSender getSender() { return this.sender; }

    public TCPReceiverThread getReceiver() {
        return receiver;
    }

    public void setReceiver(TCPReceiverThread receiver) {
        this.receiver = receiver;
    }

    public void printNodesList() {
        String ports = getPort() + "::";
        for (NodeRecord nodeRecord : nodesToConnectTo) {
            ports += nodeRecord.getPort();
            ports += ":";
        }
        System.out.println(ports + numberOfNodeConnections);
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
