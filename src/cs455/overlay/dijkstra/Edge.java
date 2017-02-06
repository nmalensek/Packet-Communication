package cs455.overlay.dijkstra;

import cs455.overlay.node.NodeRecord;

public class Edge {
    private final String id;
    private final NodeRecord source;
    private final NodeRecord destination;
    private final int weight;

    public Edge(String id, NodeRecord source, NodeRecord destination, int weight) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    public String getId() {
        return id;
    }

    public NodeRecord getSource() {
        return source;
    }

    public NodeRecord getDestination() {
        return destination;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return source.getNodeID() + " "
                + destination.getNodeID() + " " + weight;
    }
}
