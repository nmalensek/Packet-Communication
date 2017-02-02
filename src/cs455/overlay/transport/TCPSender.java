package cs455.overlay.transport;

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

    public synchronized void sendData(byte[] dataToSend) throws IOException {
        int dataLength = dataToSend.length;
        dataOutputStream.writeInt(dataLength);
        dataOutputStream.write(dataToSend, 0, dataLength);
        dataOutputStream.flush();
    }

}
