package routing.overlay.wireformats.nodemessages.Receiving;

import routing.overlay.wireformats.Event;
import routing.overlay.wireformats.Protocol;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class LinkWeightsReceive implements Protocol, Event<LinkWeightsReceive> {

    private int messageType = LINK_WEIGHTS;
    private int numberOfLinks;
    private String linkInfo;
    //link info - hostnameA:portnumA hostnameB:portnumB weight

    public int getNumberOfLinks() { return numberOfLinks; }

    public String getLinkInfo() { return linkInfo; }

    public LinkWeightsReceive getType() { return this; }

    public LinkWeightsReceive(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream byteArrayInputStream =
                new ByteArrayInputStream(marshalledBytes);
        DataInputStream dataInputStream =
                new DataInputStream(new BufferedInputStream(byteArrayInputStream));

        messageType = dataInputStream.readInt();
        numberOfLinks = dataInputStream.readInt();

        int linkLength = dataInputStream.readInt();
        byte[] identifierBytes = new byte[linkLength];
        dataInputStream.readFully(identifierBytes);

        linkInfo = new String(identifierBytes);

        byteArrayInputStream.close();
        dataInputStream.close();
    }

    public byte[] getBytes() {
        byte[] marshalledBytes = null;
        return marshalledBytes;
    }
}
