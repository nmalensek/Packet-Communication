package cs445.overlay.wireformats.eventfactory;

import cs445.overlay.node.Node;
import cs445.overlay.wireformats.Event;

import java.io.IOException;

public class EventFactory {

    private static EventFactory instance = null;

    protected EventFactory() {

    }

    public static EventFactory getInstance() {
        if (instance == null) {
            instance = new EventFactory();
        }
        return instance;
    }

    public void newEvent(Node node, Event event) throws IOException {
        node.onEvent(event);
    }
}
