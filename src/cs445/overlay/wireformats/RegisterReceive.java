package cs445.overlay.wireformats;

import java.io.*;

public class RegisterReceive implements Protocol, Event<RegisterReceive> {

    private int messageType;
    private int portNumber;
    private String identifier;

    public RegisterReceive(byte[] marshalledBytes) throws IOException {
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

    public RegisterReceive getType() {
        return this;
    }

    public byte[] getBytes() {
        byte[] marshalledBytes = null;

        return marshalledBytes;
    }

    public String getIdentifier() { return identifier; }
    public int getPortNumber() { return portNumber; }

}
