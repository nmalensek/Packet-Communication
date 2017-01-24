package cs445.overlay.wireformats;

import java.io.IOException;

public interface Event<T> {

    T getType();
    byte[] getBytes() throws IOException;
}
