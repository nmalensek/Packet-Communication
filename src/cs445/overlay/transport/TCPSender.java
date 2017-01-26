package cs445.overlay.transport;

import cs445.overlay.node.Node;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPSender {

    private Socket socketToSendTo;
    private DataOutputStream dataOutputStream;

    public TCPSender(Socket socketToSendTo) throws IOException {
        this.socketToSendTo = socketToSendTo;
        dataOutputStream = new DataOutputStream(socketToSendTo.getOutputStream());
    }

    public void sendData(byte[] dataToSend) throws IOException {
        int dataLength = dataToSend.length;
        dataOutputStream.writeInt(dataLength);
        dataOutputStream.write(dataToSend, 0, dataLength);
        dataOutputStream.flush();
    }

}
