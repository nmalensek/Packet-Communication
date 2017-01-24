package cs445.overlay.wireformats;

import java.io.*;

public class RegisterReceive implements Protocol, Event<RegisterReceive> {

    private int messageType;
    private int portNumber;
    private String identifier;
    private int tracker;

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

        tracker = dataInputStream.readInt();

        printData();
        RegisterResponse registerResponse = new RegisterResponse();

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

    public void printData() {
        System.out.println(messageType);
        System.out.println(portNumber);
        System.out.println(identifier);
        System.out.println(tracker);
    }

}
