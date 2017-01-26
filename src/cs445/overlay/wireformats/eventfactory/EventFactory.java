package cs445.overlay.wireformats.eventfactory;

import cs445.overlay.node.Node;
import cs445.overlay.wireformats.*;

import java.io.IOException;

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

    public static final Event<RegResponseReceive> receiveRegisterResponseEvent(
            byte[] marshalledBytes) throws IOException {
        RegResponseReceive registerResponse = new RegResponseReceive(marshalledBytes);
        return registerResponse;
    }
}
