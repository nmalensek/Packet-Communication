package cs445.overlay.transport;

import cs445.overlay.node.Node;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPSender {

    private Socket socket;
    private Node node;
    private DataOutputStream dataOutputStream;

    public TCPSender(Socket socket, Node node) throws IOException {
        this.socket = socket;
        this.node = node;
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }

    public void sendData(byte[] dataToSend) throws IOException {
        int dataLength = dataToSend.length;
        dataOutputStream.writeInt(dataLength);
        dataOutputStream.write(dataToSend, 0, dataLength);
        dataOutputStream.flush();
    }

}
