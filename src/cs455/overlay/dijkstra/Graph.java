package cs455.overlay.dijkstra;

import cs455.overlay.node.NodeRecord;

import java.util.List;

/**
 * code adapted from http://www.vogella.com/tutorials/JavaAlgorithmsDijkstra/article.html
 */

public class Graph {
    private final List<NodeRecord> vertices;
    private final List<Edge> edges;

    public Graph(List<NodeRecord> vertices, List<Edge> edges) {
        this.vertices = vertices;
        this.edges = edges;
    }

    public List<NodeRecord> getVertices() {
        return vertices;
    }

    public List<Edge> getEdges() {
        return edges;
    }
}
