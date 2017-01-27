package cs445.overlay.transport;

import cs445.overlay.node.Node;
import cs445.overlay.wireformats.*;
import cs445.overlay.wireformats.eventfactory.EventFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class TCPReceiverThread extends Thread implements Protocol {

    private Socket communicationSocket;
    private DataInputStream dataInputStream;
    private Node node;
    private EventFactory eventFactory = EventFactory.getInstance();

    public TCPReceiverThread(Socket communicationSocket, Node node) throws IOException {
        this.communicationSocket = communicationSocket;
        this.node = node;
        dataInputStream = new DataInputStream(communicationSocket.getInputStream());
    }

    public void run() {
        int dataLength;
        while (communicationSocket != null) {
            try {
                dataLength = dataInputStream.readInt();

                byte[] data = new byte[dataLength];
                dataInputStream.readFully(data, 0, dataLength);

                determineMessageType(data);

            } catch (SocketException se) {
                se.printStackTrace();
                break;
            } catch (IOException ioe) {
                ioe.printStackTrace();
                break;
            }
        }
    }

    public void determineMessageType(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream byteArrayInputStream =
                new ByteArrayInputStream(marshalledBytes);
        DataInputStream dataInputStream =
                new DataInputStream(new BufferedInputStream(byteArrayInputStream));

        int messageType = dataInputStream.readInt();

        switch (messageType) {
            case DEREGISTER_REQUEST:
                //do something
            case REGISTER_REQUEST:
                Event<RegisterReceive> registerReceiveEvent =
                        eventFactory.receiveRegReqEvent(marshalledBytes);
                node.onEvent(registerReceiveEvent, communicationSocket);
                break;
            case REGISTER_RESPONSE:
                Event<RegResponseReceive> registerResponseEvent =
                        eventFactory.receiveRegisterResponseEvent(marshalledBytes);
                node.onEvent(registerResponseEvent, communicationSocket);
                break;
            case MESSAGING_NODES_LIST:
                //do something
            case LINK_WEIGHTS:
                //do something
            case TASK_INITIATE:
                //do something else
            case SEND_MESSAGE:
                //do something
            case TASK_COMPLETE:
                //do something
            case PULL_TRAFFIC_SUMMARY:
                //do something else
            case TRAFFIC_SUMMARY:
                //do something else
            default:
                //error
        }
    }
}
