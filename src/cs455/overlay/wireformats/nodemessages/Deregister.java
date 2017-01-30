package cs455.overlay.wireformats.nodemessages;

import cs455.overlay.wireformats.Protocol;

public class Deregister implements Protocol {

    private int messageType = DEREGISTER_REQUEST;
    private String ipAddress;
    private int portNumber;

}
