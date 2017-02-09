package cs455.tests;

import cs455.overlay.dijkstra.*;

import java.io.IOException;
import java.util.*;

public class TestShortestPath {

    private List<Vertex> vertices;
    private List<Edge> links;
    private Map<String, Edge> edgeMap = new HashMap<>();
    private RoutingCache routingCache;
    private Graph graph;
    private ShortestPath shortestPath;
    private String thisNodeID = "Node_0";

    public TestShortestPath() throws IOException {
    }

    public void testExecute() throws IOException {
        vertices = new ArrayList<Vertex>();
        links = new ArrayList<Edge>();
        for (int i = 0; i < 11; i++) {
            Vertex location = new Vertex("Node_" + i);
            vertices.add(location);
        }

        addLane("link_0", 0, 1, 85);
        addLane("link_0", 1, 0, 85);
        addLane("link_1", 0, 2, 217);
        addLane("link_1", 2, 0, 217);
        addLane("link_2", 0, 4, 173);
        addLane("link_2", 4, 0, 173);
        addLane("link_3", 2, 6, 186);
        addLane("link_3", 6, 2, 186);
        addLane("link_4", 2, 7, 103);
        addLane("link_4", 7, 2, 103);
        addLane("link_5", 3, 7, 183);
        addLane("link_5", 7, 3, 183);
        addLane("link_6", 5, 8, 250);
        addLane("link_6", 8, 5, 250);
        addLane("link_7", 8, 9, 84);
        addLane("link_7", 9, 8, 84);
        addLane("link_8", 7, 9, 167);
        addLane("link_8", 9, 7, 167);
        addLane("link_9", 4, 9, 502);
        addLane("link_9", 9, 4, 502);
        addLane("link_10", 9, 10, 40);
        addLane("link_10", 10, 9, 40);
        addLane("link_11", 10, 1, 600);
    }

    private Vertex findThisNodeInVertexList() {
        for (Vertex vertex : vertices) {
            if(thisNodeID.equals(vertex.getId())) {
                return vertex;
            }
        }
        throw new RuntimeException();
    }


    private void computeShortestPaths() {
        routingCache = new RoutingCache(links, edgeMap);
        graph = new Graph(vertices, links);
        shortestPath = new ShortestPath(graph);
        LinkedList<Vertex> path = new LinkedList<>();
        Vertex thisNode = findThisNodeInVertexList();
        shortestPath.computeShortestPath(thisNode);
        for (Vertex destNode : vertices) {
            if (!thisNode.equals(destNode)) {
                path = shortestPath.getPath(destNode);
                routingCache.cacheShortestPath(destNode.getId(), path);
            }
        }
    }

    private void print() {
        routingCache.printMap(thisNodeID);
//        routingCache.simplePrint();
    }

    private void addLane(String laneId, int sourceLocNo, int destLocNo,
                         int duration) {
        String sourceID = vertices.get(sourceLocNo).getId();
        String destID = vertices.get(destLocNo).getId();
        Edge lane = new Edge(sourceID + " " + destID, vertices.get(sourceLocNo), vertices.get(destLocNo), duration);
        links.add(lane);
        edgeMap.put(lane.getId(), lane);
    }

    public static void main(String[] args) throws IOException {
        TestShortestPath testShortestPath = new TestShortestPath();
        testShortestPath.testExecute();
        testShortestPath.computeShortestPaths();
        testShortestPath.print();
    }
}

//    ShortestPath dijkstra = new ShortestPath(graph);
//        dijkstra.computeShortestPath(nodes.get(4));
//    LinkedList<Vertex> path = dijkstra.getPath(nodes.get(8));
//
//        for (Vertex vertex : path) {
//        System.out.println(vertex);
//    }

//
//        dijkstra.computeShortestPath(nodes.get(0));
//                for (Vertex destNode : nodes) {
//                if (!nodes.get(0).equals(destNode)) {
//                path = dijkstra.getPath(destNode);
//                String pathString = "";
//                pathString += nodes.get(0).toString();
//                for (ListIterator<Vertex> vertexListIterator = path.listIterator(); vertexListIterator.hasNext();) {
//        try {
//        Vertex currentVertex = vertexListIterator.next();
//        int currentPosition = path.indexOf(currentVertex);
//        Vertex nextVertex = path.get(currentPosition + 1);
//        Edge nextEdge = edgeMap.get(currentVertex.getId() + " " + nextVertex.getId());
//
//        pathString += "--";
//        pathString += nextEdge.getWeight();
//        pathString += "--";
//        pathString += nextVertex;
//        } catch (IndexOutOfBoundsException e) {
//        pathString = pathString.substring(0, pathString.length());
//        }
//
//        }
//        System.out.println(pathString);
//        }
//        }
//        }