package cs445.overlay.wireformats;

public class Register implements Protocol {

    private int messageType = REGISTER_REQUEST;
    private String ipAddress;
    private int portNumber;

    public Register(String ipAddress, int portNumber) {
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
    }

}
