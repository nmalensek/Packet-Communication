package cs445.overlay.wireformats;

import java.io.*;

public class Register implements Protocol, Event {

    private int messageType = REGISTER_REQUEST;
    private String ipAddress;
    private int portNumber;
    private long timestamp;
    private String identifier = "127.0.0.1";
    private int tracker = 1;

    public Register(String ipAddress, int portNumber) {
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
    }

    public int getType() { return messageType; }

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

}
