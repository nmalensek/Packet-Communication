package cs445.overlay.transport;

import cs445.overlay.node.Node;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPSender {

    private Socket socketToSendTo;
    private Node sendingNode;
    private DataOutputStream dataOutputStream;

    public TCPSender(Socket socketToSendTo, Node sendingNode) throws IOException {
        this.socketToSendTo = socketToSendTo;
        this.sendingNode = sendingNode;
        dataOutputStream = new DataOutputStream(socketToSendTo.getOutputStream());
    }

    public void sendData(byte[] dataToSend) throws IOException {
        System.out.println("doot");
        int dataLength = dataToSend.length;
        dataOutputStream.writeInt(dataLength);
        dataOutputStream.write(dataToSend, 0, dataLength);
        dataOutputStream.flush();
    }

}
