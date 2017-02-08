package cs455.overlay.dijkstra;

import java.util.*;

public class RoutingCache {
    private String destination;
    private List<Vertex> shortestPath;
    private Map<String, LinkedList<Vertex>> shortestPathsMap = new HashMap<>();
    private List<Edge> edges;
    private Map<String, Edge> edgeMap;

    public RoutingCache(List<Edge> edges, Map<String, Edge> edgeMap) {
        this.edges = new ArrayList<>(edges);
        this.edgeMap = new HashMap<>(edgeMap);
    }

    public void cacheShortestPath(String destination, LinkedList<Vertex> shortestPath) {
        shortestPathsMap.put(destination, shortestPath);
    }

    public Map<String, LinkedList<Vertex>> getShortestPathsMap() {
        return shortestPathsMap;
    }

    public void simplePrint() {
        for (String destination : shortestPathsMap.keySet()) {
            for (Vertex vertex : shortestPathsMap.get(destination)) {
                System.out.print(vertex + " : ");
            }
            System.out.println("");
        }
    }

    public void printMap(String thisNodeID) {
        for (String destination : shortestPathsMap.keySet()) {
            String pathString = thisNodeID;
            for (ListIterator<Vertex> vertexListIterator =
                 shortestPathsMap.get(destination).listIterator(); vertexListIterator.hasNext(); ) {
                try {
                    Vertex currentVertex = vertexListIterator.next();
                    int currentPosition = shortestPathsMap.get(destination).indexOf(currentVertex);
                    Vertex nextVertex = shortestPathsMap.get(destination).get(currentPosition + 1);
                    Edge nextEdge = edgeMap.get(currentVertex.getId() + " " + nextVertex.getId());

                    pathString += "--";
                    pathString += nextEdge.getWeight();
                    pathString += "--";
                    pathString += nextVertex;
                } catch (IndexOutOfBoundsException e) {
                }
            }
            System.out.println(pathString);
        }
    }
}
