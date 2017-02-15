package cs455.overlay.dijkstra;

import java.util.*;

/**
 * code adapted from http://www.vogella.com/tutorials/JavaAlgorithmsDijkstra/article.html
 */

public class ShortestPath {
    private final List<Point> nodes;
    private final List<Connection> connections;
    private Set<Point> nodesWithKnownShortestPath;
    private Set<Point> nodesWithoutShortestPath;
    private Map<Point, Point> predecessors;
    private Map<Point, Integer> weight;

    public ShortestPath(Graph graph) {
        this.nodes = new ArrayList<>(graph.getVertices());
        this.connections = new ArrayList<>(graph.getConnections());
    }

    /**
     * Calculates shortest path from a given point in the overlay
     * @param source node that the shortest path should be calculated from (origin)
     */

    public void computeShortestPath(Point source) {
        nodesWithKnownShortestPath = new HashSet<>();
        nodesWithoutShortestPath = new HashSet<>();
        weight = new HashMap<>();
        predecessors = new HashMap<>();
        weight.put(source, 0);
        nodesWithoutShortestPath.add(source);
        while (nodesWithoutShortestPath.size() > 0) {
            Point node = getMinimum(nodesWithoutShortestPath);
            nodesWithKnownShortestPath.add(node);
            nodesWithoutShortestPath.remove(node);
            findShortestDistances(node);
        }
    }

    /**
     * if shortest distance to the destination is greater than the current node's distance plus the link weight between
     * the source and destination, store the destination's distance plus the link weight between the two
     * @param node node that shortest path should be calculated from (origin)
     */

    private void findShortestDistances(Point node) {
        List<Point> adjacentNodes = getNeighbors(node);
        for (Point target : adjacentNodes) {
            if(getShortestDistance(target) > getShortestDistance(node) + getLinkWeight(node, target)) {
                weight.put(target, getShortestDistance(node) + getLinkWeight(node, target));
                predecessors.put(target, node);
                nodesWithoutShortestPath.add(target);
            }
        }
    }

    /**
     * Retrieves desired link weight between nodes
     * @param node source node
     * @param target destination node
     * @return connection's link weight
     */

    private int getLinkWeight(Point node, Point target) {
        for(Connection connection : connections) {
            if(connection.getSource().equals(node) && connection.getDestination().equals(target)) { //finds weight between two nodes
                return connection.getWeight();
            }
        }
        throw new RuntimeException();
    }

    /**
     * Retrieves a list of nodes that a specified node is next to in the overlay
     * @param node node to find the neighbors of.
     * @return list of nodes that the specified node is next to (has a direct connection with).
     */

    private List<Point> getNeighbors(Point node) {
        List<Point> neighbors = new ArrayList<>();
        for (Connection connection : connections) {
            if (connection.getSource().equals(node) && !shortestPathDetermined(connection.getDestination())) { //gets next possible nodes in path
                neighbors.add(connection.getDestination());
            }
        }
        return neighbors;
    }

    /**
     * For every node in the overlay, compares every distance to each other node in the overlay and sets
     * the smallest possible distance as the minimum.
     * @param vertices all nodes in the overlay.
     * @return smallest distance to any other node in the overlay.
     */

    private Point getMinimum(Set<Point> vertices) {
        Point minimum = null;
        for (Point point : vertices) {
            if (minimum == null) {
                minimum = point;
            } else if (getShortestDistance(point) < getShortestDistance(minimum)){ //compares node weight to previously calculated shortest distance
                minimum = point;
            }
        }
        return minimum;
    }

    /**
     * Whether or not a shortest path has been determined for a given point.
     * @param point node in the overlay to check if a shortest path has been calculated
     * @return true if the shortest path to a node is known, false if shortest path has yet to be calculated.
     */

    private boolean shortestPathDetermined(Point point) {
        return nodesWithKnownShortestPath.contains(point);
    } //shortest path has been determined

    /**
     * Get the distance to the specified node, set at maximum integer value if shortest path calculation is still in progress
     * @param destination node to find the shortest distance to
     * @return distance (weight) to the destination node.
     */

    private int getShortestDistance(Point destination) {
        Integer distance = weight.get(destination);
        if (distance == null) {
            return Integer.MAX_VALUE; //all nodes start with MAX_VALUE distance until shortest distance is calculated
        } else {
            return distance;
        }
    }

    /**
     * Returns shortest path to the target node.
     * @param target node to get the shortest path for.
     * @return list of points that comprise the shortest path.
     */

    public LinkedList<Point> getPath(Point target) {
        //check for no possible path unnecessary, overlay should never have a partition
        LinkedList<Point> path = new LinkedList<>();
        Point step = target;
        path.add(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            path.add(step);
        }
        //put the shortest path into the correct order
        Collections.reverse(path);
        return path;
    }

}


