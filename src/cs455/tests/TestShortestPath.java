package cs455.tests;

import cs455.overlay.dijkstra.Edge;
import cs455.overlay.dijkstra.Graph;
import cs455.overlay.dijkstra.ShortestPath;
import cs455.overlay.node.NodeRecord;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
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
        nodes = new ArrayList<cs455.overlay.node.NodeRecord>();
        edges = new ArrayList<Edge>();
        for (int i = 0; i < 11; i++) {
            cs455.overlay.node.NodeRecord location = new cs455.overlay.node.NodeRecord("Node_" + i, 111 + i, socket);
            nodes.add(location);
        }

        addLane("link_0", 0, 1, 85);
        addLane("link_1", 0, 2, 217);
        addLane("link_2", 0, 4, 173);
        addLane("link_3", 2, 6, 186);
        addLane("link_4", 2, 7, 103);
        addLane("link_5", 3, 7, 183);
        addLane("link_6", 5, 8, 250);
        addLane("link_7", 8, 9, 84);
        addLane("link_8", 7, 9, 167);
        addLane("link_9", 4, 9, 502);
        addLane("link_10", 9, 10, 40);
        addLane("link_11", 1, 10, 600);

        // Lets check from location Loc_0 to Loc_10
        Graph graph = new Graph(nodes, edges);
        ShortestPath dijkstra = new ShortestPath(graph);
        dijkstra.execute(nodes.get(0));
        LinkedList<NodeRecord> path = dijkstra.getPath(nodes.get(10));

        for (NodeRecord vertex : path) {
            System.out.println(vertex);
        }

    }

    private void addLane(String laneId, int sourceLocNo, int destLocNo,
                         int duration) {
        Edge lane = new Edge(laneId,nodes.get(sourceLocNo), nodes.get(destLocNo), duration );
        edges.add(lane);
    }

    public static void main(String[] args) throws IOException {
        TestShortestPath testShortestPath = new TestShortestPath();
        testShortestPath.testExcute();
    }
}
