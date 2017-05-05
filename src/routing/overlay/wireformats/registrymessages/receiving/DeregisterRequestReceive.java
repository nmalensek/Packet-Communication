package routing.overlay.wireformats.registrymessages.receiving;

import routing.overlay.wireformats.Event;
import routing.overlay.wireformats.Protocol;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class DeregisterRequestReceive implements Protocol, Event<DeregisterRequestReceive> {

    private int messageType;
    private int portNumber;
    private String identifier;

    public DeregisterRequestReceive(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream byteArrayInputStream =
                new ByteArrayInputStream(marshalledBytes);
        DataInputStream dataInputStream =
                new DataInputStream(new BufferedInputStream(byteArrayInputStream));

        messageType = dataInputStream.readInt();
        portNumber = dataInputStream.readInt();

        int identifierLength = dataInputStream.readInt();
        byte[] identifierBytes = new byte[identifierLength];
        dataInputStream.readFully(identifierBytes);

        identifier = new String(identifierBytes);

        byteArrayInputStream.close();
        dataInputStream.close();
    }

    public DeregisterRequestReceive getType() {
        return this;
    }

    @Override
    public int getMessageType() {
        return messageType;
    }

    public byte[] getBytes() {
        //unused, only receiving bytes
        byte[] marshalledBytes = null;
        return marshalledBytes;
    }

    public String getIdentifier() { return identifier; }
    public int getPortNumber() { return portNumber; }

}
