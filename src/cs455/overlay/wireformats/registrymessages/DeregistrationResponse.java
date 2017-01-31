package cs455.overlay.wireformats.registrymessages;

import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.Protocol;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DeregistrationResponse implements Protocol, Event {
    private int messageType = DEREGISTER_RESPONSE;
    private byte successOrFailure;
    private String additionalInfo;

    public DeregistrationResponse getType() {
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
