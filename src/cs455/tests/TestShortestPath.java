package cs455.tests;

import cs455.overlay.dijkstra.Edge;
import cs455.overlay.dijkstra.Graph;
import cs455.overlay.dijkstra.ShortestPath;
import cs455.overlay.node.NodeRecord;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TestShortestPath {

    private List<cs455.overlay.node.NodeRecord> nodes;
    private List<Edge> edges;
    Socket socket = new Socket(Inet4Address.getLocalHost().getHostName(), 2500);

    public TestShortestPath() throws IOException {
    }

    public void testExcute() throws IOException {
        nodes = new ArrayList<NodeRecord>();
        edges = new ArrayList<Edge>();
        for (int i = 0; i < 11; i++) {
            NodeRecord location = new NodeRecord("Node_" + i, 111 + i, socket);
            nodes.add(location);
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

        // Lets check from location Loc_0 to Loc_10
        Graph graph = new Graph(nodes, edges);
        ShortestPath dijkstra = new ShortestPath(graph);
        dijkstra.execute(nodes.get(5));
        LinkedList<NodeRecord> path = dijkstra.getPath(nodes.get(3));

        for (NodeRecord vertex : path) {
            System.out.println(vertex);
        }

    }

    private void addLane(String laneId, int sourceLocNo, int destLocNo,
                         int duration) {
        String sourceID = nodes.get(sourceLocNo).getNodeID();
        String destID = nodes.get(destLocNo).getNodeID();
        Edge lane = new Edge(sourceID + " " + destID,nodes.get(sourceLocNo), nodes.get(destLocNo), duration );
        edges.add(lane);
    }

    public static void main(String[] args) throws IOException {
        TestShortestPath testShortestPath = new TestShortestPath();
        testShortestPath.testExcute();
    }
}