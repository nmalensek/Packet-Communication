package cs455.overlay.wireformats.registrymessages.sending;

import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.Protocol;

import java.io.*;

public class SendMessagingNodesList implements Protocol, Event<SendMessagingNodesList> {

    int messageType = MESSAGING_NODES_LIST;
    private long timestamp;
    private String identifier;
    private int tracker;
    //messaging node info (hostname:portnum)

    public void receiveBytes(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream byteArrayInputStream =
                new ByteArrayInputStream(marshalledBytes);
        DataInputStream dataInputStream =
                new DataInputStream(new BufferedInputStream(byteArrayInputStream));

        messageType = dataInputStream.readInt();
        timestamp = dataInputStream.readLong();

        int identifierLength = dataInputStream.readInt();
        byte[] identifierBytes = new byte[identifierLength];
        dataInputStream.readFully(identifierBytes);

        identifier = new String(identifierBytes);

        tracker = dataInputStream.readInt();

        byteArrayInputStream.close();
        dataInputStream.close();
    }

    public SendMessagingNodesList getType() {
        return this;
    }

    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream =
                new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));

        dataOutputStream.writeInt(messageType);
        dataOutputStream.writeLong(timestamp);

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
