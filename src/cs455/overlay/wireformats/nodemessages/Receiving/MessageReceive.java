package cs455.overlay.wireformats.nodemessages.Receiving;

import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.Protocol;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class MessageReceive implements Protocol, Event<MessageReceive> {

    private int messageType = SEND_MESSAGE;
    private String routingPath;
    private int payload;

    public String getRoutingPath() { return routingPath; }
    public int getPayload() { return payload; }

    public MessageReceive getType() { return this; }

    public MessageReceive(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream byteArrayInputStream =
                new ByteArrayInputStream(marshalledBytes);
        DataInputStream dataInputStream =
                new DataInputStream(new BufferedInputStream(byteArrayInputStream));

        messageType = dataInputStream.readInt();
        payload = dataInputStream.readInt();

        int routeLength = dataInputStream.readInt();
        byte[] identifierBytes = new byte[routeLength];
        dataInputStream.readFully(identifierBytes);

        routingPath = new String(identifierBytes);

        byteArrayInputStream.close();
        dataInputStream.close();
    }

    public byte[] getBytes() {
        byte[] marshalledBytes = null;
        return marshalledBytes;
    }
}
