package cs445.overlay.wireformats.eventfactory;

import cs445.overlay.wireformats.*;

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

    public static final Event<RegisterSend> createRegisterSendEvent() {
        RegisterSend registerSend = new RegisterSend();
        return registerSend;
    }

    public static final Event<RegisterReceive> receiveRegReqEvent(
            byte[] marshalledBytes) throws IOException {
        RegisterReceive registerReceive = new RegisterReceive(marshalledBytes);
        return registerReceive;
    }

    public static final Event<RegisterResponse> createRegisterResponseEvent() {
        RegisterResponse registerResponse = new RegisterResponse();
        return registerResponse;
    }

    public static final Event<ReceiveRegistryResponse> receiveRegisterResponseEvent(
            byte[] marshalledBytes) throws IOException {
        ReceiveRegistryResponse registerResponse = new ReceiveRegistryResponse(marshalledBytes);
        return registerResponse;
    }
}
