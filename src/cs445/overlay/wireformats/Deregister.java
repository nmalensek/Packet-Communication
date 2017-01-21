package cs445.overlay.wireformats;

public class Deregister implements Protocol {

    private int messageType = DEREGISTER_REQUEST;
    private String ipAddress;
    private int portNumber;

}
