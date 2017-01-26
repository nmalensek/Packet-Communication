package cs445.overlay.wireformats;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class RegResponseReceive implements Protocol, Event{
    private int messageType;
    private String additionalInfo;

    public RegResponseReceive(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream byteArrayInputStream =
                new ByteArrayInputStream(marshalledBytes);
        DataInputStream dataInputStream =
                new DataInputStream(new BufferedInputStream(byteArrayInputStream));

        messageType = dataInputStream.readInt();
        int registrationStatus = dataInputStream.readInt();

        int additionalInfoLength = dataInputStream.readInt();
        byte[] additionalInfoBytes = new byte[additionalInfoLength];
        dataInputStream.readFully(additionalInfoBytes);

        additionalInfo = new String(additionalInfoBytes);

        System.out.println("data read");

        byteArrayInputStream.close();
        dataInputStream.close();
    }

    public void testPrint() {
        System.out.println("message received");
    }

    public Object getType() {
        return this;
    }

    public byte[] getBytes() throws IOException {
        return new byte[0];
    }
}
