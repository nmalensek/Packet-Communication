package cs455.overlay.transport;

import cs455.overlay.node.Node;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.TaskInitiate;
import cs455.overlay.wireformats.eventfactory.EventFactory;
import cs455.overlay.wireformats.nodemessages.NodeConnection;
import cs455.overlay.wireformats.nodemessages.Receiving.*;
import cs455.overlay.wireformats.registrymessages.receiving.DeregisterRequestReceive;
import cs455.overlay.wireformats.registrymessages.receiving.RegisterRequestReceive;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class TCPReceiverThread extends Thread implements Protocol {

    private Socket communicationSocket;
    private DataInputStream dataInputStream;
    private Node node;
    private EventFactory eventFactory = EventFactory.getInstance();

    public TCPReceiverThread(Socket communicationSocket, Node node) throws IOException {
        this.communicationSocket = communicationSocket;
        this.node = node;
        dataInputStream = new DataInputStream(communicationSocket.getInputStream());
    }

    public void run() {
        int dataLength;
        while (communicationSocket != null) {
            try {
                dataLength = dataInputStream.readInt();

                byte[] data = new byte[dataLength];
                dataInputStream.readFully(data, 0, dataLength);

                determineMessageType(data);

            } catch (SocketException se) {
                se.printStackTrace();
                break;
            } catch (IOException ioe) {
                ioe.printStackTrace();
                break;
            }
        }
    }

    public void determineMessageType(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream byteArrayInputStream =
                new ByteArrayInputStream(marshalledBytes);
        DataInputStream dataInputStream =
                new DataInputStream(new BufferedInputStream(byteArrayInputStream));

        int messageType = dataInputStream.readInt();
        dataInputStream.close();

        switch (messageType) {
            case DEREGISTER_REQUEST:
                Event<DeregisterRequestReceive> receiveDeregisterRequestEvent =
                        eventFactory.receiveDeregistrationEvent(marshalledBytes);
                node.onEvent(receiveDeregisterRequestEvent, communicationSocket);
                break;
            case REGISTER_REQUEST:
                Event<RegisterRequestReceive> registerReceiveEvent =
                        eventFactory.receiveRegisterEvent(marshalledBytes);
                node.onEvent(registerReceiveEvent, communicationSocket);
                break;
            case REGISTER_RESPONSE:
                Event<RegistryResponseReceive> registerResponseEvent =
                        eventFactory.receiveRegisterResponseEvent(marshalledBytes);
                node.onEvent(registerResponseEvent, communicationSocket);
                break;
            case MESSAGING_NODES_LIST:
                Event<MessagingNodesListReceive> receiveMessagingNodesListEvent =
                        eventFactory.receiveMessagingNodesList(marshalledBytes);
                node.onEvent(receiveMessagingNodesListEvent, communicationSocket);
                break;
            case LINK_WEIGHTS:
                Event<LinkWeightsReceive> receiveLinkWeightsEvent =
                        eventFactory.receiveLinkWeights(marshalledBytes);
                node.onEvent(receiveLinkWeightsEvent, communicationSocket);
            case TASK_INITIATE:
                Event<TaskInitiate> receiveTaskInitiateEvent =
                        eventFactory.receiveTaskInitiate(marshalledBytes);
                node.onEvent(receiveTaskInitiateEvent, communicationSocket);
            case SEND_MESSAGE:
                Event<MessageReceive> receiveMessage =
                        eventFactory.receiveMessage(marshalledBytes);
                node.onEvent(receiveMessage, communicationSocket);
            case TASK_COMPLETE:
                //do something
            case PULL_TRAFFIC_SUMMARY:
                //do something else
            case TRAFFIC_SUMMARY:
                //do something else
                break;
            case DEREGISTER_RESPONSE:
                Event<DeregisterResponseReceive> deregisterResponseEvent =
                        eventFactory.receiveDeregisterResponse(marshalledBytes);
                node.onEvent(deregisterResponseEvent, communicationSocket);
                break;
            case NODE_CONNECTION:
                Event<NodeConnection> receiveNodeConnectionEvent =
                        eventFactory.receiveNodeConnection(marshalledBytes);
                node.onEvent(receiveNodeConnectionEvent, communicationSocket);
                break;
            default:
                System.out.println("Something went horribly wrong, please restart.");
        }
    }
}
