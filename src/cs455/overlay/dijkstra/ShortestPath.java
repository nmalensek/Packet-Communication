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

    //if shortest distance to the destination is greater than the current node's distance plus the link weight between
    //the source and destination, store the destination's distance plus the link weight between the two
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

    private int getLinkWeight(Point node, Point target) {
        for(Connection connection : connections) {
            if(connection.getSource().equals(node) && connection.getDestination().equals(target)) { //finds weight between two nodes
                return connection.getWeight();
            }
        }
        throw new RuntimeException();
    }

    private List<Point> getNeighbors(Point node) {
        List<Point> neighbors = new ArrayList<>();
        for (Connection connection : connections) {
            if (connection.getSource().equals(node) && !shortestPathDetermined(connection.getDestination())) { //gets next possible nodes in path
                neighbors.add(connection.getDestination());
            }
        }
        return neighbors;
    }

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

    private boolean shortestPathDetermined(Point point) {
        return nodesWithKnownShortestPath.contains(point);
    } //shortest path has been determined

    private int getShortestDistance(Point destination) {
        Integer distance = weight.get(destination);
        if (distance == null) {
            return Integer.MAX_VALUE; //all nodes start with MAX_VALUE distance
        } else {
            return distance;
        }
    }

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


