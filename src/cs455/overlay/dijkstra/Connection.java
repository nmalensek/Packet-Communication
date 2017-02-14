package cs455.overlay.dijkstra;

import java.lang.String;

/**
 * code adapted from http://www.vogella.com/tutorials/JavaAlgorithmsDijkstra/article.html
 */

public class Connection {
    private final String id;
    private final Point source;
    private final Point destination;
    private final int weight;

    public Connection(String id, Point source, Point destination, int weight) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    public String getId() {
        return id;
    }

    public Point getSource() {
        return source;
    }

    public Point getDestination() {
        return destination;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return source + " "
                + destination + " " + weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Connection connection = (Connection) o;

        return id.equals(connection.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
