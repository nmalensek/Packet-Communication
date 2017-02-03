package cs455.overlay.wireformats.registrymessages.receiving;

import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.Protocol;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ReceiveDeregisterRequest implements Protocol, Event<ReceiveDeregisterRequest> {

    private int messageType;
    private int portNumber;
    private String identifier;

    public ReceiveDeregisterRequest(byte[] marshalledBytes) throws IOException {
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

    public ReceiveDeregisterRequest getType() {
        return this;
    }

    public byte[] getBytes() {
        //unused, only receiving bytes
        byte[] marshalledBytes = null;
        return marshalledBytes;
    }

    public String getIdentifier() { return identifier; }
    public int getPortNumber() { return portNumber; }

}
