package cs455.overlay.wireformats.nodemessages.Receiving;

import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.Protocol;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class MessagingNodesListReceive implements Protocol, Event<MessagingNodesListReceive> {
    private int messageType = MESSAGING_NODES_LIST;
    private int numberOfPeerMessagingNodes; //this number of connections node should initiate. then print total connections once done.
    private String nodesToConnectTo;
    //messaging node info (hostname:portnum)

    public void listConnections() {
        System.out.println(getNodesToConnectTo());
    }

    public int getNumberOfPeerMessagingNodes() {return numberOfPeerMessagingNodes;}

    public MessagingNodesListReceive getType() { return this; }

    public MessagingNodesListReceive(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream byteArrayInputStream =
                new ByteArrayInputStream(marshalledBytes);
        DataInputStream dataInputStream =
                new DataInputStream(new BufferedInputStream(byteArrayInputStream));

        messageType = dataInputStream.readInt();
        numberOfPeerMessagingNodes = dataInputStream.readInt();

        int nodeLength = dataInputStream.readInt();
        byte[] identifierBytes = new byte[nodeLength];
        dataInputStream.readFully(identifierBytes);

        nodesToConnectTo = new String(identifierBytes);

        byteArrayInputStream.close();
        dataInputStream.close();
    }

    public String getNodesToConnectTo() {
        return nodesToConnectTo;
    }

    public byte[] getBytes() {
        byte[] marshalledBytes = null;
        return marshalledBytes;
    }
}
