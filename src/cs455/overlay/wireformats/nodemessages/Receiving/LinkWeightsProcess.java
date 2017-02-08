package cs455.overlay.wireformats.nodemessages.Receiving;

import cs455.overlay.dijkstra.Edge;
import cs455.overlay.dijkstra.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinkWeightsProcess {
    private List<Vertex> vertexList = new ArrayList<>();
    private List<Edge> edgeList = new ArrayList<>();
    private Map<String, Edge> edgeMap = new HashMap<>();

    public void processLinkWeights(String linkWeights) {
        String[] splitByNewLine = linkWeights.split("\\n");
        for(String path : splitByNewLine) {
            String[] splitPath = path.split("\\s");
            String sourceID = splitPath[0];
            String destID = splitPath[1];
            int weight = Integer.parseInt(splitPath[2]);
            Vertex sourceVertex = new Vertex(sourceID);
            Vertex destinationVertex = new Vertex(destID);
            Edge newEdge = new Edge(sourceID + " " + destID, sourceVertex, destinationVertex, weight);
            createVertex(sourceVertex);
            createVertex(destinationVertex);
            edgeList.add(newEdge);
            edgeMap.put(newEdge.getId(), newEdge);
        }
    }

    public List<Vertex> getVertexList() { return vertexList; }

    public List<Edge> getEdgeList() { return edgeList; }

    public Map<String, Edge> getEdgeMap() { return edgeMap; }

    private void createVertex(Vertex vertex) {
        if (!vertexList.contains(vertex)) {
            vertexList.add(vertex);
        }
    }

    public void getVertexListSize() {
        System.out.println(vertexList.size());
    }

    public void getEdgeListSize() {
        System.out.println(edgeList.size());
    }

}
