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

    /**
     * Stores shortest path to a given node
     * @param destination node the shortest path was calculated for
     * @param shortestPath shortest path that was calculated
     */

    public void cacheShortestPath(String destination, LinkedList<Point> shortestPath) {
        shortestPathsMap.put(destination, shortestPath);
    }

    /**
     * Retrieves all shortest paths to nodes in overlay.
     * @return all shortest paths in overlay.
     */

    public Map<String, LinkedList<Point>> getShortestPathsMap() {
        return shortestPathsMap;
    }

    /**
     * Method to print shortest paths; unformatted.
     */
    public void simplePrint() {
        for (String destination : shortestPathsMap.keySet()) {
            for (Point point : shortestPathsMap.get(destination)) {
                System.out.print(point + " : ");
            }
            System.out.println("");
        }
    }

    /**
     * Prints shortest paths formatted according to assignment requirements
     * @param thisNodeID node that's printing the shortest paths, excluded from the printed data.
     */
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
