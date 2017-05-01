package routing.overlay.wireformats.nodemessages.Receiving;

import routing.overlay.dijkstra.Connection;
import routing.overlay.dijkstra.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinkWeightsProcess {
    private List<Point> pointList = new ArrayList<>();
    private List<Connection> connectionList = new ArrayList<>();
    private Map<String, Connection> edgeMap = new HashMap<>();

    /**
     * Takes link weights from LinkWeightsSend message from Registry. Splits the string by newline and then by space
     * to find the source node, destination node, and connection weight. The node then stores the specified connection.
     * This occurs for every node in the overlay; each node can now message any other node in the overlay.
     * This should result in a copy of the registry's overlay knowledge.
     * @param linkWeights String of all connections and their respective weights in the overlay (from LinkWeightsSend
     *                    message).
     */
    public void processLinkWeights(String linkWeights) {
        String[] splitByNewLine = linkWeights.split("\\n");
        for(String path : splitByNewLine) {
            String[] splitPath = path.split("\\s");
            String sourceID = splitPath[0];
            String destID = splitPath[1];
            int weight = Integer.parseInt(splitPath[2]);
            Point sourcePoint = new Point(sourceID);
            Point destinationPoint = new Point(destID);
            Connection newConnection = new Connection(sourceID + " " + destID, sourcePoint, destinationPoint, weight);
            createVertex(sourcePoint);
            createVertex(destinationPoint);
            connectionList.add(newConnection);
            edgeMap.put(newConnection.getId(), newConnection);
        }
    }

    public List<Point> getPointList() { return pointList; }

    public List<Connection> getConnectionList() { return connectionList; }

    public Map<String, Connection> getEdgeMap() { return edgeMap; }

    /**
     * Ensures no duplicates are added in the overlay reconstruction.
     * @param point node in the overlay.
     */
    private void createVertex(Point point) {
        if (!pointList.contains(point)) {
            pointList.add(point);
        }
    }

    public void getVertexListSize() {
        System.out.println(pointList.size());
    }

    public void getEdgeListSize() {
        System.out.println(connectionList.size());
    }

}
