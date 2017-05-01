package routing.overlay.wireformats.registrymessages.sending;

import routing.overlay.node.NodeRecord;
import routing.overlay.wireformats.Event;
import routing.overlay.wireformats.Protocol;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class MessagingNodesList implements Protocol, Event<MessagingNodesList> {

    private int messageType = MESSAGING_NODES_LIST;
    private int numberOfRequiredConnections; //number of connections overlay requires
    private String messagingNodes;

    public MessagingNodesList getType() {
        return this;
    }

    //marshalls bytes
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream =
                new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));

        dataOutputStream.writeInt(messageType);
        dataOutputStream.writeInt(numberOfRequiredConnections);

        byte[] identifierBytes = messagingNodes.getBytes();
        int elementLength = identifierBytes.length;
        dataOutputStream.writeInt(elementLength);
        dataOutputStream.write(identifierBytes);

        dataOutputStream.flush();
        marshalledBytes = byteArrayOutputStream.toByteArray();

        byteArrayOutputStream.close();
        dataOutputStream.close();

        return marshalledBytes;
    }

    public void setMessagingNodes(List<NodeRecord> nodesToConnectTo) {
        messagingNodes = "";
        for (NodeRecord node : nodesToConnectTo) {
            messagingNodes += node.getNodeID();
            messagingNodes += "\n";
        }
    }

    public void setNumberOfRequiredConnections(int nodes) {
        numberOfRequiredConnections = nodes;
    }
}
