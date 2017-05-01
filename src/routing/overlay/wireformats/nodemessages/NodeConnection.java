package routing.overlay.wireformats.nodemessages;

import routing.overlay.wireformats.Event;
import routing.overlay.wireformats.Protocol;

import java.io.*;

public class NodeConnection implements Protocol, Event<NodeConnection> {

    private int messageType = NODE_CONNECTION;
    private String nodeID;

    public void setNodeID(String nodeID) {
        this.nodeID = nodeID;
    }

    public String getNodeID() { return nodeID; }

    public int getMessageType() { return messageType; }

    /**
     * Used to debug program (confirm nodes are connected).
     */
    public void printHello() {
        System.out.println("Hello from " + getNodeID());
    }

    public NodeConnection getType() { return this; }

    //marshalls bytes
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream =
                new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));

        dataOutputStream.writeInt(getMessageType());

        byte[] nodeIDBytes = nodeID.getBytes();
        int elementLength = nodeIDBytes.length;
        dataOutputStream.writeInt(elementLength);
        dataOutputStream.write(nodeIDBytes);

        dataOutputStream.flush();
        marshalledBytes = byteArrayOutputStream.toByteArray();

        byteArrayOutputStream.close();
        dataOutputStream.close();

        return marshalledBytes;
    }

    public void receiveBytes(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream byteArrayInputStream =
                new ByteArrayInputStream(marshalledBytes);
        DataInputStream dataInputStream =
                new DataInputStream(new BufferedInputStream(byteArrayInputStream));

        messageType = dataInputStream.readInt();

        int nodeIDLength = dataInputStream.readInt();
        byte[] identifierBytes = new byte[nodeIDLength];
        dataInputStream.readFully(identifierBytes);

        nodeID = new String(identifierBytes);

        byteArrayInputStream.close();
        dataInputStream.close();
    }

}
