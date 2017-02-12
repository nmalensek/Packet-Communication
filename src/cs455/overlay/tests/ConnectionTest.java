package cs455.overlay.tests;


import java.io.IOException;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;

public class ConnectionTest {

    public static void main(String[] args) throws UnknownHostException {
        try {
            Socket socket = new Socket(Inet4Address.getLocalHost().getHostName(), 2500);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
