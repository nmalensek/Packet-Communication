package cs445.overlay.wireformats;

import java.io.*;

public class RegisterResponse implements Protocol, Event {

    private int messageType = REGISTER_RESPONSE;
    private String additionalInfo;
    private int tracker;
    private byte[] marshalledBytes;

    public RegisterResponse getType() {
        return this;
    }
//
//    public void unmarshallBytes(byte[] marshalledBytes) throws IOException {
//        ByteArrayInputStream byteArrayInputStream =
//                new ByteArrayInputStream(marshalledBytes);
//        DataInputStream dataInputStream =
//                new DataInputStream(new BufferedInputStream(byteArrayInputStream));
//
//        messageType = dataInputStream.readInt();
//        int registrationStatus = dataInputStream.readByte();
//
//        int additionalInfoLength = dataInputStream.readInt();
//        byte[] additionalInfoBytes = new byte[additionalInfoLength];
//        dataInputStream.readFully(additionalInfoBytes);
//
//        additionalInfo = new String(additionalInfoBytes);
//
//        tracker = dataInputStream.readInt();
//
//        byteArrayInputStream.close();
//        dataInputStream.close();
//    }

    //marshalls bytes
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream =
                new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));

        dataOutputStream.writeInt(messageType);
        dataOutputStream.writeInt(1);

        byte[] identifierBytes = additionalInfo.getBytes();
        int elementLength = identifierBytes.length;
        dataOutputStream.writeInt(elementLength);
        dataOutputStream.write(identifierBytes);

        dataOutputStream.flush();
        marshalledBytes = byteArrayOutputStream.toByteArray();

        byteArrayOutputStream.close();
        dataOutputStream.close();

        return marshalledBytes;
    }

    public void setAdditionalInfo(int nodeCount) {
        additionalInfo = "Nodes registered: " + nodeCount;
    }

}
