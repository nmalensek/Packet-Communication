package cs455.overlay.wireformats.nodemessages;

import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.Protocol;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ReceiveDeregisterResponse implements Protocol, Event {
    private int messageType;
    private String additionalInfo;
    private byte deRegistrationStatus;

    public ReceiveDeregisterResponse(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream byteArrayInputStream =
                new ByteArrayInputStream(marshalledBytes);
        DataInputStream dataInputStream =
                new DataInputStream(new BufferedInputStream(byteArrayInputStream));

        messageType = dataInputStream.readInt();
        deRegistrationStatus = dataInputStream.readByte();

        int additionalInfoLength = dataInputStream.readInt();
        byte[] additionalInfoBytes = new byte[additionalInfoLength];
        dataInputStream.readFully(additionalInfoBytes);

        additionalInfo = new String(additionalInfoBytes);

        byteArrayInputStream.close();
        dataInputStream.close();
    }

    public Object getType() {
        return this;
    }

    public byte[] getBytes() throws IOException {
        return new byte[0];
    }

    public void printMessage() {
        System.out.println(additionalInfo);
    }

}
