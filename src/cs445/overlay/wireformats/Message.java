package cs445.overlay.wireformats;

public class Message implements Protocol {

    int messageType = SEND_MESSAGE;
    byte payload;
}
