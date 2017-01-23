package cs445.overlay.wireformats;

import java.io.*;

public class RegisterResponse implements Protocol, Event {

    private int messageType = REGISTER_RESPONSE;
    private String ipAddress;
    private int portNumber;
    private long timestamp;
    private String identifier;
    private int tracker;

    public int getType() {
        return messageType;
    }

    public void unmarshallBytes(byte[] marshalledBytes) throws IOException {
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

        byteArrayInputStream.close();
        dataInputStream.close();
    }

    //marshalls bytes
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream =
                new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));

        dataOutputStream.writeInt(messageType);
        dataOutputStream.writeInt(portNumber);

        byte[] identifierBytes = identifier.getBytes();
        int elementLength = identifierBytes.length;
        dataOutputStream.writeInt(elementLength);
        dataOutputStream.write(identifierBytes);

        dataOutputStream.writeInt(tracker);

        dataOutputStream.flush();
        marshalledBytes = byteArrayOutputStream.toByteArray();

        byteArrayOutputStream.close();
        dataOutputStream.close();

        return marshalledBytes;
    }

    public void printData() {
        System.out.println(messageType);
        System.out.println(portNumber);
        System.out.println(identifier);
        System.out.println(tracker);
    }

}
