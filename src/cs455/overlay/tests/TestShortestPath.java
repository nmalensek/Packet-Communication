package cs455.overlay.tests;

import cs455.overlay.dijkstra.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class TestShortestPath {

    private List<Vertex> vertices;
    private List<Edge> links;
    private Map<String, Edge> edgeMap = new HashMap<>();
    private RoutingCache routingCache;
    private Graph graph;
    private ShortestPath shortestPath;
    private String thisNodeID = "Node_0";
    private LinkedList<Vertex> path;

    public TestShortestPath() throws IOException {
    }

    public void testExecute() throws IOException {
        vertices = new ArrayList<Vertex>();
        links = new ArrayList<Edge>();
        for (int i = 0; i < 11; i++) {
            Vertex location = new Vertex("Node_" + i);
            vertices.add(location);
        }

        addLink("link_0", 0, 1, 85);
        addLink("link_0", 1, 0, 85);
        addLink("link_1", 0, 2, 217);
        addLink("link_1", 2, 0, 217);
        addLink("link_2", 0, 4, 173);
        addLink("link_2", 4, 0, 173);
        addLink("link_3", 2, 6, 186);
        addLink("link_3", 6, 2, 186);
        addLink("link_4", 2, 7, 103);
        addLink("link_4", 7, 2, 103);
        addLink("link_5", 3, 7, 183);
        addLink("link_5", 7, 3, 183);
        addLink("link_6", 5, 8, 250);
        addLink("link_6", 8, 5, 250);
        addLink("link_7", 8, 9, 84);
        addLink("link_7", 9, 8, 84);
        addLink("link_8", 7, 9, 167);
        addLink("link_8", 9, 7, 167);
        addLink("link_9", 4, 9, 502);
        addLink("link_9", 9, 4, 502);
        addLink("link_10", 9, 10, 40);
        addLink("link_10", 10, 9, 40);
        addLink("link_11", 10, 1, 600);
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

    private void getPathToSelectedNode(String nodeToMessage) {
        Map<String, LinkedList<Vertex>> shortestPathMap = routingCache.getShortestPathsMap();
        path = shortestPathMap.get(nodeToMessage);
        path.removeFirst(); //origin node, should not include in path
        for (Vertex vertex : path) {
            System.out.println(vertex);
        }
    }

    public void sendMessage() {
        String routingPath = "";
        for (Vertex vertex : path) {
            routingPath += vertex.getId();
        }
    }

    private void testRandomPayload() {
        int payload = ThreadLocalRandom.current().nextInt(1, 2147483647);
        System.out.println(payload);
    }

    private void print() {
        routingCache.printMap(thisNodeID);
    }

    private void addLink(String linkID, int sourceLocNo, int destLocNo,
                         int duration) {
        String sourceID = vertices.get(sourceLocNo).getId();
        String destID = vertices.get(destLocNo).getId();
        Edge link = new Edge(sourceID + " " + destID, vertices.get(sourceLocNo), vertices.get(destLocNo), duration);
        links.add(link);
        edgeMap.put(link.getId(), link);
    }

    public static void main(String[] args) throws IOException {
        TestShortestPath testShortestPath = new TestShortestPath();
        testShortestPath.testExecute();
        testShortestPath.computeShortestPaths();
        testShortestPath.getPathToSelectedNode("Node_2");
        testShortestPath.getPathToSelectedNode("Node_5");
//        testShortestPath.testRandomPayload();
//        testShortestPath.print();
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