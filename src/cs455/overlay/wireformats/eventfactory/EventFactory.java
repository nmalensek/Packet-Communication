package cs455.overlay.wireformats.eventfactory;

import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.nodemessages.ReceiveRegistryResponse;
import cs455.overlay.wireformats.nodemessages.SendRegister;
import cs455.overlay.wireformats.registrymessages.ReceiveRegisterRequest;
import cs455.overlay.wireformats.registrymessages.RespondToRegisterRequest;

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

    public static final Event<ReceiveRegisterRequest> receiveRegReqEvent(
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
}
