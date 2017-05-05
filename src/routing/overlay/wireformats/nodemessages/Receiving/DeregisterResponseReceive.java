package routing.overlay.wireformats.nodemessages.Receiving;

import routing.overlay.wireformats.Event;
import routing.overlay.wireformats.Protocol;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class DeregisterResponseReceive implements Protocol, Event {
    private int messageType;
    private String additionalInfo;
    private byte deRegistrationStatus;

    public DeregisterResponseReceive(byte[] marshalledBytes) throws IOException {
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

    @Override
    public int getMessageType() {
        return messageType;
    }

    public byte[] getBytes() throws IOException {
        return new byte[0];
    }

    public void printMessage() {
        System.out.println(additionalInfo);
    }
    public byte getDeRegistrationStatus() { return deRegistrationStatus; }

}
