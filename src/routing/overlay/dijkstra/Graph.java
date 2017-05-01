package routing.overlay.dijkstra;

import java.util.List;

/**
 * code adapted from http://www.vogella.com/tutorials/JavaAlgorithmsDijkstra/article.html
 */

public class Graph {
    private final List<Point> vertices;
    private final List<Connection> connections;

    public Graph(List<Point> vertices, List<Connection> connections) {
        this.vertices = vertices;
        this.connections = connections;
    }

    public List<Point> getVertices() {
        return vertices;
    }

    public List<Connection> getConnections() {
        return connections;
    }
}
