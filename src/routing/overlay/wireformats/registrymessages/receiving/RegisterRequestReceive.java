package routing.overlay.wireformats.registrymessages.receiving;

import routing.overlay.wireformats.Event;
import routing.overlay.wireformats.Protocol;

import java.io.*;

public class RegisterRequestReceive implements Protocol, Event<RegisterRequestReceive> {

    private int messageType;
    private int portNumber;
    private String identifier;

    public RegisterRequestReceive(byte[] marshalledBytes) throws IOException {
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

    public RegisterRequestReceive getType() {
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
