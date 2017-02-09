package cs455.overlay.wireformats.eventfactory;

import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.nodemessages.Receiving.*;
import cs455.overlay.wireformats.nodemessages.Sending.MessageSend;
import cs455.overlay.wireformats.TaskInitiate;
import cs455.overlay.wireformats.nodemessages.*;
import cs455.overlay.wireformats.nodemessages.Sending.Deregister;
import cs455.overlay.wireformats.nodemessages.Sending.SendRegister;
import cs455.overlay.wireformats.registrymessages.sending.DeregistrationResponse;
import cs455.overlay.wireformats.registrymessages.receiving.DeregisterRequestReceive;
import cs455.overlay.wireformats.registrymessages.receiving.RegisterRequestReceive;
import cs455.overlay.wireformats.registrymessages.sending.RegisterRequestResponse;

import java.io.IOException;
import java.net.Socket;

public final class EventFactory {

    private static EventFactory instance = null;

    protected EventFactory() {

    }

    public static EventFactory getInstance() {
        if (instance == null) {
            instance = new EventFactory();
        }
        return instance;
    }

    public static final Socket openSocket(String host, int port) throws IOException {
        Socket replySocket = new Socket(host, port);
        return replySocket;
    }

    public static final Event<SendRegister> createRegisterSendEvent() {
        SendRegister sendRegister = new SendRegister();
        return sendRegister;
    }

    public static final Event<RegisterRequestReceive> receiveRegisterEvent(
            byte[] marshalledBytes) throws IOException {
        RegisterRequestReceive registerRequestReceive = new RegisterRequestReceive(marshalledBytes);
        return registerRequestReceive;
    }

    public static final Event<RegisterRequestResponse> createRegisterResponseEvent() {
        RegisterRequestResponse registerRequestResponse = new RegisterRequestResponse();
        return registerRequestResponse;
    }

    public static final Event<RegistryResponseReceive> receiveRegisterResponseEvent(
            byte[] marshalledBytes) throws IOException {
        RegistryResponseReceive registerResponse = new RegistryResponseReceive(marshalledBytes);
        return registerResponse;
    }

    public static final Event<Deregister> createDeregistrationEvent() {
        Deregister sendDeregister = new Deregister();
        return sendDeregister;
    }

    public static final Event<DeregisterRequestReceive> receiveDeregistrationEvent(
            byte[] marshalledBytes) throws IOException {
        DeregisterRequestReceive deregisterRequestReceive =
                new DeregisterRequestReceive(marshalledBytes);
        return deregisterRequestReceive;
    }

    public static final Event<DeregistrationResponse> sendDeregistrationResponse() {
        DeregistrationResponse deregistrationResponse = new DeregistrationResponse();
        return deregistrationResponse;
    }

    public static final Event<DeregisterResponseReceive> receiveDeregisterResponse(
            byte[] marshalledBytes) throws IOException {
        DeregisterResponseReceive deregisterResponse = new DeregisterResponseReceive(marshalledBytes);
        return deregisterResponse;
    }

    public static final Event<MessagingNodesListReceive> receiveMessagingNodesList(
            byte[] marshalledBytes) throws IOException {
        MessagingNodesListReceive messagingNodesListReceive = new MessagingNodesListReceive(marshalledBytes);
        return messagingNodesListReceive;
    }

    public static final Event<NodeConnection> sendNodeConnection() {
        NodeConnection sendNodeConnection = new NodeConnection();
        return sendNodeConnection;
    }

    public static final Event<NodeConnection> receiveNodeConnection(
            byte[] marshalledBytes) throws IOException {
        NodeConnection receiveNodeConnection = new NodeConnection();
        receiveNodeConnection.receiveBytes(marshalledBytes);
        return receiveNodeConnection;
    }

    public static final Event<LinkWeightsReceive> receiveLinkWeights(
            byte[] marshalledBytes) throws IOException {
        LinkWeightsReceive linkWeightsReceive = new LinkWeightsReceive(marshalledBytes);
        return linkWeightsReceive;
    }

    public static final Event<TaskInitiate> receiveTaskInitiate(
            byte[] marshalledBytes) throws IOException {
        TaskInitiate taskInitiate = new TaskInitiate();
        taskInitiate.readTaskInitiateMessage(marshalledBytes);
        return taskInitiate;
    }

    public static final Event<MessageReceive> receiveMessage(
            byte[] marshalledBytes) throws IOException {
        MessageReceive messageReceive = new MessageReceive(marshalledBytes);
        return messageReceive;
    }
}
