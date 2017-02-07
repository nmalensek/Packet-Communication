package cs455.overlay.wireformats.registrymessages.sending;

import cs455.overlay.dijkstra.Edge;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.Protocol;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class LinkWeightsSend implements Protocol, Event<LinkWeightsSend> {

    private int messageType = LINK_WEIGHTS;
    private int numberOfLinks;
    private String linkInfo;
    //link info - hostnameA:portnumA hostnameB:portnumB weight

    public LinkWeightsSend getType() {
        return this;
    }

    //marshalls bytes
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream =
                new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));

        dataOutputStream.writeInt(messageType);
        dataOutputStream.writeInt(numberOfLinks);

        byte[] identifierBytes = linkInfo.getBytes();
        int elementLength = identifierBytes.length;
        dataOutputStream.writeInt(elementLength);
        dataOutputStream.write(identifierBytes);

        dataOutputStream.flush();
        marshalledBytes = byteArrayOutputStream.toByteArray();

        byteArrayOutputStream.close();
        dataOutputStream.close();

        return marshalledBytes;
    }

    public void setMessagingNodes(List<Edge> linkList) {
        linkInfo = "";
        for (Edge edge : linkList) {
            linkInfo += edge.getSource().getNodeID();
            linkInfo += " ";
            linkInfo += edge.getDestination().getNodeID();
            linkInfo += " ";
            linkInfo += edge.getWeight();
            linkInfo += "\n";
        }
    }

    public void setNumberOfLinks(int numberOfLinks) {
        this.numberOfLinks = numberOfLinks;
    }
}
