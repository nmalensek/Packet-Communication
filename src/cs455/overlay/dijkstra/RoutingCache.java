package cs455.overlay.dijkstra;

import java.util.*;

public class RoutingCache {
    private String destination;
    private List<Point> shortestPath;
    private Map<String, LinkedList<Point>> shortestPathsMap = new HashMap<>();
    private List<Connection> connections;
    private Map<String, Connection> edgeMap;

    public RoutingCache(List<Connection> connections, Map<String, Connection> edgeMap) {
        this.connections = new ArrayList<>(connections);
        this.edgeMap = new HashMap<>(edgeMap);
    }

    public void cacheShortestPath(String destination, LinkedList<Point> shortestPath) {
        shortestPathsMap.put(destination, shortestPath);
    }

    public Map<String, LinkedList<Point>> getShortestPathsMap() {
        return shortestPathsMap;
    }

    public void simplePrint() {
        for (String destination : shortestPathsMap.keySet()) {
            for (Point point : shortestPathsMap.get(destination)) {
                System.out.print(point + " : ");
            }
            System.out.println("");
        }
    }

    public void printMap(String thisNodeID) {
        for (String destination : shortestPathsMap.keySet()) {
            String pathString = thisNodeID;
            for (ListIterator<Point> vertexListIterator =
                 shortestPathsMap.get(destination).listIterator(); vertexListIterator.hasNext(); ) {
                try {
                    Point currentPoint = vertexListIterator.next();
                    int currentPosition = shortestPathsMap.get(destination).indexOf(currentPoint);
                    Point nextPoint = shortestPathsMap.get(destination).get(currentPosition + 1);
                    Connection nextConnection = edgeMap.get(currentPoint.getId() + " " + nextPoint.getId());

                    pathString += "--";
                    pathString += nextConnection.getWeight();
                    pathString += "--";
                    pathString += nextPoint;
                } catch (IndexOutOfBoundsException e) {
                }
            }
            System.out.println(pathString);
        }
    }
}
