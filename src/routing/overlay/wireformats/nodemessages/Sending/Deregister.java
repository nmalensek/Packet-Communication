package routing.overlay.wireformats.nodemessages.Sending;

import routing.overlay.wireformats.Event;
import routing.overlay.wireformats.Protocol;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Deregister implements Protocol, Event<Deregister> {

    private int messageType = DEREGISTER_REQUEST;
    private int portNumber;
    private String ipAddress;

    public void setHostAndPort(String host, int port) {
        this.ipAddress = host;
        this.portNumber = port;
    }

    public int getMessageType() {
        return messageType;
    }

    public Deregister getType() { return this; }

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
