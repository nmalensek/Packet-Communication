package cs455.overlay.wireformats.registrymessages.sending;

import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.Protocol;

import java.io.*;

public class RespondToRegisterRequest implements Protocol, Event {

    private int messageType = REGISTER_RESPONSE;
    private byte successOrFailure;
    private String additionalInfo;

    public RespondToRegisterRequest getType() {
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

    public void setAdditionalInfo(String info) {
        additionalInfo = info;
    }
    public void printAdditionalInfo() {
        System.out.println(additionalInfo);
    }
    public void setSuccessOrFailure(byte sOrF) { successOrFailure = sOrF; }

}
