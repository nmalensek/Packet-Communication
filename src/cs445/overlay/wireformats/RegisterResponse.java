package cs445.overlay.wireformats;

import java.io.*;

public class RegisterResponse implements Protocol, Event {

    private int messageType = REGISTER_RESPONSE;
    private String additionalInfo;
    private byte successOrFailure;

    public RegisterResponse getType() {
        return this;
    }

    //marshalls bytes
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream =
                new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));

        dataOutputStream.writeInt(messageType);
        dataOutputStream.writeByte(successOrFailure);

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
    public void printAdditionalInfo() {
        System.out.println(additionalInfo);
    }
    public void setSuccessOrFailure(byte sOrF) { successOrFailure = sOrF; }

}
