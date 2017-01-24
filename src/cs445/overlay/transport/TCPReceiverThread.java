package cs445.overlay.transport;

import cs445.overlay.node.Node;
import cs445.overlay.wireformats.Event;
import cs445.overlay.wireformats.Protocol;
import cs445.overlay.wireformats.RegisterReceive;
import cs445.overlay.wireformats.RegisterSend;
import cs445.overlay.wireformats.eventfactory.EventFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class TCPReceiverThread implements Runnable, Protocol {

    private Socket socket;
    private DataInputStream dataInputStream;
    private Node node;
    private EventFactory eventFactory = EventFactory.getInstance();

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

                RegisterReceive registerReceive = new RegisterReceive(data);
                determineMessageType(data);

            } catch (SocketException se) {
                System.out.println(se.getMessage());
                break;
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
                break;
            }
        }
    }

    public void determineMessageType(byte[] marshalledBytes) throws IOException {
        Event event = null;
        ByteArrayInputStream byteArrayInputStream =
                new ByteArrayInputStream(marshalledBytes);
        DataInputStream dataInputStream =
                new DataInputStream(new BufferedInputStream(byteArrayInputStream));

        int messageType = dataInputStream.readInt();
        switch (messageType) {
            case DEREGISTER_REQUEST:
                //do something
            case REGISTER_REQUEST: messageType = REGISTER_REQUEST;
                RegisterSend register = eventFactory.createRegisterSendEvent().getType();
                eventFactory.newEvent(node, register);
                break;
            case REGISTER_RESPONSE: messageType = REGISTER_RESPONSE;
            //do something else
            case MESSAGING_NODES_LIST: messageType = MESSAGING_NODES_LIST;
                //do something
            case LINK_WEIGHTS: messageType = LINK_WEIGHTS;
                //do something
            case TASK_INITIATE: messageType = TASK_INITIATE;
                //do something else
            case SEND_MESSAGE: messageType = SEND_MESSAGE;
                //do something
            case TASK_COMPLETE: messageType = TASK_COMPLETE;
                //do something
            case PULL_TRAFFIC_SUMMARY: messageType = PULL_TRAFFIC_SUMMARY;
                //do something else
            case TRAFFIC_SUMMARY: messageType = TRAFFIC_SUMMARY;
                //do something else
            default:
                //error
        }
    }
}
