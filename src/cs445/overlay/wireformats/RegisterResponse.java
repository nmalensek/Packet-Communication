package cs445.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class RegisterResponse {

    private int messageType;
    private String ipAddress;
    private int portNumber;
    private long timestamp;
    private String identifier;
    private int tracker;

    public void receiveBytes(byte[] marshalledBytes) throws IOException {
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

        byteArrayInputStream.close();
        dataInputStream.close();
    }

    public void printData() {
        System.out.println(messageType);
        System.out.println(portNumber);
        System.out.println(identifier);
        System.out.println(tracker);
    }

}
