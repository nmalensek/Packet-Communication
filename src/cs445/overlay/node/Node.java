package cs445.overlay.node;

import cs445.overlay.wireformats.Event;

import java.io.IOException;

public interface Node {

    void onEvent(Event event) throws IOException;
}
