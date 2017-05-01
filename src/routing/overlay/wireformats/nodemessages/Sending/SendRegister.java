package routing.overlay.wireformats.nodemessages.Sending;

import routing.overlay.wireformats.Event;
import routing.overlay.wireformats.Protocol;

import java.io.*;

public class SendRegister implements Protocol, Event<SendRegister> {

    private int messageType = REGISTER_REQUEST;
    private String ipAddress;
    private int portNumber;

    public void setHostAndPort(String host, int port) {
        this.ipAddress = host;
        this.portNumber = port;
    }

    public int getMessageType() {
        return messageType;
    }

    public SendRegister getType() { return this; }

    //marshalls bytes
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream =
                new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));

        dataOutputStream.writeInt(getMessageType());
        dataOutputStream.writeInt(portNumber);

        byte[] identifierBytes = ipAddress.getBytes();
        int elementLength = identifierBytes.length;
        dataOutputStream.writeInt(elementLength);
        dataOutputStream.write(identifierBytes);

        dataOutputStream.flush();
        marshalledBytes = byteArrayOutputStream.toByteArray();

        byteArrayOutputStream.close();
        dataOutputStream.close();

        return marshalledBytes;
    }

}
