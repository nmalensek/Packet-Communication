package routing.overlay.wireformats.nodemessages.Sending;

import routing.overlay.wireformats.Event;

public class BindExceptionHappened implements Event<BindExceptionHappened> {

    public BindExceptionHappened getType() { return this; }

    @Override
    public int getMessageType() {
        return 100;
    }

    public byte[] getBytes() {
        byte[] unusedByteArray = null;
        return unusedByteArray;
    }
}
