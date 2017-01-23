package cs445.overlay.transport;

import cs445.overlay.node.Node;
import cs445.overlay.wireformats.Event;
import cs445.overlay.wireformats.RegisterResponse;
import cs445.overlay.wireformats.eventfactory.EventFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class TCPReceiverThread implements Runnable {

    private Socket socket;
    private DataInputStream dataInputStream;
    private Node node;
    private EventFactory eventFactory = EventFactory.getInstance();
    private Event newEvent;
    private int messageType;

    public TCPReceiverThread(Socket socket, Node node) throws IOException {
        this.socket = socket;
        this.node = node;
        dataInputStream = new DataInputStream(socket.getInputStream());
    }

    public void run() {
        int dataLength;
        while (socket != null) {
            try {
                dataLength = dataInputStream.readInt();

                byte[] data = new byte[dataLength];
                dataInputStream.readFully(data, 0, dataLength);

                Event eventType = determineMessageType(data);
                eventFactory.newEvent(node, eventType, data);

            } catch (SocketException se) {
                System.out.println(se.getMessage());
                break;
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
                break;
            }
        }
    }

    public Event determineMessageType(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream byteArrayInputStream =
                new ByteArrayInputStream(marshalledBytes);
        DataInputStream dataInputStream =
                new DataInputStream(new BufferedInputStream(byteArrayInputStream));

        RegisterResponse registerResponse = new RegisterResponse();
        return registerResponse;
    }
}
