package cs455.overlay.wireformats.eventfactory;

import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.nodemessages.*;
import cs455.overlay.wireformats.registrymessages.sending.DeregistrationResponse;
import cs455.overlay.wireformats.registrymessages.receiving.ReceiveDeregisterRequest;
import cs455.overlay.wireformats.registrymessages.receiving.ReceiveRegisterRequest;
import cs455.overlay.wireformats.registrymessages.sending.RespondToRegisterRequest;

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

    public static final Event<ReceiveRegisterRequest> receiveRegisterEvent(
            byte[] marshalledBytes) throws IOException {
        ReceiveRegisterRequest receiveRegisterRequest = new ReceiveRegisterRequest(marshalledBytes);
        return receiveRegisterRequest;
    }

    public static final Event<RespondToRegisterRequest> createRegisterResponseEvent() {
        RespondToRegisterRequest respondToRegisterRequest = new RespondToRegisterRequest();
        return respondToRegisterRequest;
    }

    public static final Event<ReceiveRegistryResponse> receiveRegisterResponseEvent(
            byte[] marshalledBytes) throws IOException {
        ReceiveRegistryResponse registerResponse = new ReceiveRegistryResponse(marshalledBytes);
        return registerResponse;
    }

    public static final Event<Deregister> createDeregistrationEvent() {
        Deregister sendDeregister = new Deregister();
        return sendDeregister;
    }

    public static final Event<ReceiveDeregisterRequest> receiveDeregistrationEvent(
            byte[] marshalledBytes) throws IOException {
        ReceiveDeregisterRequest receiveDeregisterRequest =
                new ReceiveDeregisterRequest(marshalledBytes);
        return receiveDeregisterRequest;
    }

    public static final Event<DeregistrationResponse> sendDeregistrationResponse() {
        DeregistrationResponse deregistrationResponse = new DeregistrationResponse();
        return deregistrationResponse;
    }

    public static final Event<ReceiveDeregisterResponse> receiveDeregisterResponse(
            byte[] marshalledBytes) throws IOException {
        ReceiveDeregisterResponse deregisterResponse = new ReceiveDeregisterResponse(marshalledBytes);
        return deregisterResponse;
    }

    public static final Event<ReceiveMessagingNodesList> receiveMessagingNodesList(
            byte[] marshalledBytes) throws IOException {
        ReceiveMessagingNodesList receiveMessagingNodesList = new ReceiveMessagingNodesList();
        receiveMessagingNodesList.receiveBytes(marshalledBytes);
        return receiveMessagingNodesList;
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
}
