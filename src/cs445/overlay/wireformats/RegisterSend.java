package cs445.overlay.wireformats;

import java.io.*;

public class RegisterSend implements Protocol, Event<RegisterSend> {

    private int messageType = REGISTER_REQUEST;
    private String ipAddress;
    private int portNumber;
    private String identifier = "127.0.0.1";
    private int tracker = 1;

    public void setHostAndPort(String host, int port) {
        this.ipAddress = host;
        this.portNumber = port;
    }

    public int getMessageType() {
        return messageType;
    }

    public RegisterSend getType() { return this; }

    //marshalls bytes
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream =
                new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));

        dataOutputStream.writeInt(getMessageType());
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
