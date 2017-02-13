package cs455.overlay.dijkstra;

import java.util.*;

/**
 * code adapted from http://www.vogella.com/tutorials/JavaAlgorithmsDijkstra/article.html
 */

public class ShortestPath {
    private final List<Vertex> nodes;
    private final List<Edge> edges;
    private Set<Vertex> settledNodes;
    private Set<Vertex> unSettledNodes;
    private Map<Vertex, Vertex> predecessors;
    private Map<Vertex, Integer> weight;

    public ShortestPath(Graph graph) {
        this.nodes = new ArrayList<>(graph.getVertices());
        this.edges = new ArrayList<>(graph.getEdges());
    }

    public void computeShortestPath(Vertex source) {
        settledNodes = new HashSet<>();
        unSettledNodes = new HashSet<>();
        weight = new HashMap<>();
        predecessors = new HashMap<>();
        weight.put(source, 0);
        unSettledNodes.add(source);
        while (unSettledNodes.size() > 0) {
            Vertex node = getMinimum(unSettledNodes);
            settledNodes.add(node);
            unSettledNodes.remove(node);
            findShortestDistances(node);
        }
    }

    //if shortest distance to the destination is greater than the current node's distance plus the link weight between
    //the source and destination, store the destination's distance plus the link weight between the two
    private void findShortestDistances(Vertex node) {
        List<Vertex> adjacentNodes = getNeighbors(node);
        for (Vertex target : adjacentNodes) {
            if(getShortestDistance(target) > getShortestDistance(node) + getLinkWeight(node, target)) {
                weight.put(target, getShortestDistance(node) + getLinkWeight(node, target));
                predecessors.put(target, node);
                unSettledNodes.add(target);
            }
        }
    }

    private int getLinkWeight(Vertex node, Vertex target) {
        for(Edge edge : edges) {
            if(edge.getSource().equals(node) && edge.getDestination().equals(target)) { //finds weight between two nodes
                return edge.getWeight();
            }
        }
        throw new RuntimeException();
    }

    private List<Vertex> getNeighbors(Vertex node) {
        List<Vertex> neighbors = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.getSource().equals(node) && !isSettled(edge.getDestination())) { //gets next possible nodes in path
                neighbors.add(edge.getDestination());
            }
        }
        return neighbors;
    }

    private Vertex getMinimum(Set<Vertex> vertices) {
        Vertex minimum = null;
        for (Vertex vertex : vertices) {
            if (minimum == null) {
                minimum = vertex;
            } else if (getShortestDistance(vertex) < getShortestDistance(minimum)){ //compares node weight to previously calculated shortest distance
                minimum = vertex;
            }
        }
        return minimum;
    }

    private boolean isSettled(Vertex vertex) {
        return settledNodes.contains(vertex);
    } //shortest path has been determined

    private int getShortestDistance(Vertex destination) {
        Integer distance = weight.get(destination);
        if (distance == null) {
            return Integer.MAX_VALUE; //all nodes start with MAX_VALUE distance
        } else {
            return distance;
        }
    }

    public LinkedList<Vertex> getPath(Vertex target) {
        LinkedList<Vertex> path = new LinkedList<>();
        Vertex step = target;
        // check if a path exists
        if (predecessors.get(step) == null) {
            return null;
        }
        path.add(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            path.add(step);
        }
        // Put it into the correct order
        Collections.reverse(path);
        return path;
    }

}


