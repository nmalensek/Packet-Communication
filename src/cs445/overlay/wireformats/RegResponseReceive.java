package cs445.overlay.wireformats;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class RegResponseReceive implements Protocol, Event{
    private int messageType;
    private String additionalInfo;
    private byte registrationStatus;

    public RegResponseReceive(byte[] marshalledBytes) throws IOException {
        System.out.println("reading response...");
        ByteArrayInputStream byteArrayInputStream =
                new ByteArrayInputStream(marshalledBytes);
        DataInputStream dataInputStream =
                new DataInputStream(new BufferedInputStream(byteArrayInputStream));

        messageType = dataInputStream.readInt();
        registrationStatus = dataInputStream.readByte();

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
        System.out.println(registrationStatus);
    }
}
