package routing.overlay.node;

import routing.overlay.wireformats.Event;

import java.io.IOException;
import java.net.Socket;

public interface Node {

    void onEvent(Event event, Socket destinationSocket) throws IOException;
    void setServerPort(int port) throws IOException;
    void processText(String text) throws IOException;
}
