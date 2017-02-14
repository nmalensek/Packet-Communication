package cs455.overlay.dijkstra;

/**
 * code adapted from http://www.vogella.com/tutorials/JavaAlgorithmsDijkstra/article.html
 */

public class Point {
    final private String id;

    public Point(String id) {
        this.id = id;
    }

    public String getId() { return id; }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        return id.equals(point.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
