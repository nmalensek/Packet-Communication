package cs455.overlay.wireformats.nodemessages.Sending;

import cs455.overlay.wireformats.Event;

public class BindExceptionHappened implements Event<BindExceptionHappened> {

    public BindExceptionHappened getType() { return this; }

    public byte[] getBytes() {
        byte[] unusedByteArray = null;
        return unusedByteArray;
    }
}
