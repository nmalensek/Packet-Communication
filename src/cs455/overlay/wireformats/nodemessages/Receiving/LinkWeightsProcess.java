package cs455.overlay.wireformats.nodemessages.Receiving;

import cs455.overlay.dijkstra.Connection;
import cs455.overlay.dijkstra.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinkWeightsProcess {
    private List<Point> pointList = new ArrayList<>();
    private List<Connection> connectionList = new ArrayList<>();
    private Map<String, Connection> edgeMap = new HashMap<>();

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
