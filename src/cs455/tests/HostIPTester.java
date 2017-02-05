package cs455.tests;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class HostIPTester {

    private static ServerSocket serverSocket;
    private static Socket socket;

    public HostIPTester() throws IOException {
        serverSocket = new ServerSocket(2500);
        socket = serverSocket.accept();
    }

    public static void main(String[] args) throws UnknownHostException {
        try {
            HostIPTester hostIPTester = new HostIPTester();

            String ip1 = Inet4Address.getLocalHost().getHostAddress(); //should be same as socket.getInetAddress().getHostAddress()
            String hostname1 = Inet4Address.getLocalHost().getHostName();
            String canon = Inet4Address.getLocalHost().getCanonicalHostName(); //should be same as socket.getInetAddress().getHostName()
            String socketIP = socket.getInetAddress().getHostAddress();
            String socketHost = socket.getInetAddress().getHostName();

            System.out.println(ip1);
            System.out.println(hostname1);
            System.out.println(canon);
            System.out.println(socketIP);
            System.out.println(socketHost);
            System.out.println(ip1 + ":" + socketIP);
            System.out.println(canon + ":" + socketHost);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }


}
