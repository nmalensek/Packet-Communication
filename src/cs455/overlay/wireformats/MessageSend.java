package cs455.overlay.wireformats;

import cs455.overlay.dijkstra.Vertex;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MessageSend implements Protocol, Event<MessageSend> {

    private int messageType = SEND_MESSAGE;
    private String routingPath;
    private int payload;

    public MessageSend getType() {
        return this;
    }

    //marshalls bytes
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream =
                new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));

        dataOutputStream.writeInt(messageType);
        dataOutputStream.writeInt(payload);

        byte[] identifierBytes = routingPath.getBytes();
        int elementLength = identifierBytes.length;
        dataOutputStream.writeInt(elementLength);
        dataOutputStream.write(identifierBytes);

        dataOutputStream.flush();
        marshalledBytes = byteArrayOutputStream.toByteArray();

        byteArrayOutputStream.close();
        dataOutputStream.close();

        return marshalledBytes;
    }

    public void setRoutingpath(List<Vertex> routeList) {
        routingPath = "";
        for (Vertex vertex : routeList) {
            routingPath += vertex.getId();
        }
    }

    public void setPayload() {
        payload = ThreadLocalRandom.current().nextInt(-2147483648, 2147483647);
    }

    public String getRoutingPath() { return routingPath; }
}
