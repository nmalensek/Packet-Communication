package cs455.overlay.dijkstra;

import cs455.overlay.node.NodeRecord;

import java.util.*;

public class ShortestPath {
    private final List<NodeRecord> nodes;
    private final List<Edge> edges;
    private Set<NodeRecord> settledNodes;
    private Set<NodeRecord> unSettledNodes;
    private Map<NodeRecord, NodeRecord> predecessors;
    private Map<NodeRecord, Integer> distance;

    public ShortestPath(Graph graph) {
        this.nodes = new ArrayList<>(graph.getVertices());
        this.edges = new ArrayList<>(graph.getEdges());
    }

    public void execute(NodeRecord source) {
        settledNodes = new HashSet<>();
        unSettledNodes = new HashSet<>();
        distance = new HashMap<>();
        predecessors = new HashMap<>();
        distance.put(source, 0);
        unSettledNodes.add(source);
        while (unSettledNodes.size() > 0) {
            NodeRecord node = getMinimum(unSettledNodes);
            settledNodes.add(node);
            unSettledNodes.remove(node);
            findMinimalDistances(node);
        }
    }

    private void findMinimalDistances(NodeRecord node) {
        List<NodeRecord> adjacentNodes = getNeighbors(node);
        for (NodeRecord target : adjacentNodes) {
            if(getShortestDistance(target) > getShortestDistance(node) + getDistance(node, target)) {
                distance.put(target, getShortestDistance(node) + getDistance(node, target));
                predecessors.put(target, node);
                unSettledNodes.add(target);
            }
        }
    }

    private int getDistance(NodeRecord node, NodeRecord target) {
        for(Edge edge : edges) {
            if(edge.getSource().equals(node) && edge.getDestination().equals(target)) {
                return edge.getWeight();
            }
        }
        throw new RuntimeException();
    }

    private List<NodeRecord> getNeighbors(NodeRecord node) {
        List<NodeRecord> neighbors = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.getSource().equals(node) && !isSettled(edge.getDestination())) {
                neighbors.add(edge.getDestination());
            }
        }
        return neighbors;
    }

    private NodeRecord getMinimum(Set<NodeRecord> vertices) {
        NodeRecord minimum = null;
        for (NodeRecord vertex : vertices) {
            if (minimum == null) {
                minimum = vertex;
            } else if (getShortestDistance(vertex) < getShortestDistance(minimum)){
                minimum = vertex;
            }
        }
        return minimum;
    }

    private boolean isSettled(NodeRecord vertex) {
        return settledNodes.contains(vertex);
    }

    private int getShortestDistance(NodeRecord destination) {
        Integer d = distance.get(destination);
        if(d == null) {
            return Integer.MAX_VALUE;
        } else {
            return d;
        }
    }

    public LinkedList<NodeRecord> getPath(NodeRecord target) {
        LinkedList<NodeRecord> path = new LinkedList<>();
        NodeRecord step = target;
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


