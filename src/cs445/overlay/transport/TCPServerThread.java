package cs445.overlay.transport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by nicholas on 1/20/17.
 */
public class TCPServerThread {

    public TCPServerThread(int portNum) {
        try {
            ServerSocket serverSocket = new ServerSocket(portNum);
            while(true) {
                System.out.println("Server running, waiting for input...");
                Socket nodeSocket = serverSocket.accept();
            }
            PrintWriter out = new PrintWriter(nodeSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(nodeSocket.getInputStream()));
            {
                String textFromClient;
                String textToClient;
                while((textFromClient = in.readLine()) !=null) {
                    if(textFromClient.equals("1")) {
                        textToClient = "#1";
                        System.out.println("sending " + textToClient);
                        out.println(textToClient);
                    } else {
                        System.out.println(nodeSocket.getOutputStream());
                        out.println(textFromClient);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
