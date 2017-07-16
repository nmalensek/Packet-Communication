package routing.overlay.transport;

import routing.overlay.node.Node;
import routing.overlay.wireformats.*;
import routing.overlay.wireformats.eventfactory.EventFactory;
import routing.overlay.wireformats.nodemessages.NodeConnection;
import routing.overlay.wireformats.nodemessages.Message;
import routing.overlay.wireformats.nodemessages.Receiving.LinkWeightsReceive;
import routing.overlay.wireformats.nodemessages.Receiving.MessagingNodesListReceive;
import routing.overlay.wireformats.registrymessages.receiving.DeregisterRequestReceive;
import routing.overlay.wireformats.registrymessages.receiving.RegisterRequestReceive;
import routing.overlay.wireformats.nodemessages.Receiving.DeregisterResponseReceive;
import routing.overlay.wireformats.nodemessages.Receiving.RegistryResponseReceive;

import java.io.*;
import java.net.Socket;

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

    /**
     * Listens for a message coming in.
     */
    public void run() {
        int dataLength;
        while (communicationSocket != null) {
            try {
                dataLength = dataInputStream.readInt();

                byte[] data = new byte[dataLength];
                dataInputStream.readFully(data, 0, dataLength);

                determineMessageType(data);

            } catch (IOException ioe) {
                ioe.getMessage();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reads first line of message to determine the message type, then passes that to a switch statement to process
     * the message the rest of the way and pass it to the node.
     * @param marshalledBytes packaged message
     * @throws IOException
     */
    public void determineMessageType(byte[] marshalledBytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream =
                new ByteArrayInputStream(marshalledBytes);
        DataInputStream dataInputStream =
                new DataInputStream(new BufferedInputStream(byteArrayInputStream));
//        ObjectInputStream objectInputStream =
//                new ObjectInputStream(byteArrayInputStream);

//        Event event = (Event) objectInputStream.readObject(); //doesn't work, probably because object output stream isn't used when sending
//            int messageType = event.getMessageType();
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
