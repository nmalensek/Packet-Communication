package cs455.overlay.transport;

import cs455.overlay.node.Node;
import cs455.overlay.wireformats.*;
import cs455.overlay.wireformats.eventfactory.EventFactory;
import cs455.overlay.wireformats.nodemessages.NodeConnection;
import cs455.overlay.wireformats.nodemessages.Receiving.*;
import cs455.overlay.wireformats.nodemessages.Message;
import cs455.overlay.wireformats.registrymessages.receiving.DeregisterRequestReceive;
import cs455.overlay.wireformats.registrymessages.receiving.RegisterRequestReceive;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.Socket;
import java.net.SocketException;

/**
 * code adapted from code provided by instructor at http://www.cs.colostate.edu/~cs455/lectures/CS455-HelpSession1.pdf
 */

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
                System.out.println("test");
                break;
            } catch (IOException ioe) {
                ioe.getMessage();
            }
        }
    }

    public synchronized void determineMessageType(byte[] marshalledBytes) throws IOException {
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
                break;
            case TASK_INITIATE:
                Event<TaskInitiate> receiveTaskInitiateEvent =
                        eventFactory.receiveTaskInitiate(marshalledBytes);
                node.onEvent(receiveTaskInitiateEvent, communicationSocket);
                break;
            case SEND_MESSAGE:
                Event<Message> receiveMessageEvent =
                        eventFactory.receiveMessage(marshalledBytes);
                node.onEvent(receiveMessageEvent, communicationSocket);
                break;
            case TASK_COMPLETE:
                Event<TaskComplete> taskCompleteEvent =
                        eventFactory.taskComplete(marshalledBytes);
                node.onEvent(taskCompleteEvent, communicationSocket);
                break;
            case PULL_TRAFFIC_SUMMARY:
                Event<PullTrafficSummary> receivePullTrafficSummary =
                        eventFactory.receivePullTrafficSummary(marshalledBytes);
                node.onEvent(receivePullTrafficSummary, communicationSocket);
                break;
            case TRAFFIC_SUMMARY:
                Event<TrafficSummary> receiveTrafficSummary =
                        eventFactory.receiveTrafficSummary(marshalledBytes);
                node.onEvent(receiveTrafficSummary, communicationSocket);
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
