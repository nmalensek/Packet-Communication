package cs445.overlay.wireformats.eventfactory;

import cs445.overlay.node.Node;
import cs445.overlay.wireformats.Event;
import cs445.overlay.wireformats.RegisterReceive;
import cs445.overlay.wireformats.RegisterResponse;
import cs445.overlay.wireformats.RegisterSend;

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

    public static final Event<RegisterResponse> createRegisterResponseEvent() {
        RegisterResponse registerResponse = new RegisterResponse();
        return registerResponse;
    }

    public static final Event<RegisterReceive> receiveRegisterEvent(byte[] marshalledBytes) throws IOException {
        RegisterReceive registerReceive = new RegisterReceive(marshalledBytes);
        return registerReceive;
    }

    public void newEvent(Node node, Event event) throws IOException {
        node.onEvent(event);
    }
}
