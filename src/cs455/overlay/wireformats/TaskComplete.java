package cs455.overlay.wireformats;

public class TaskComplete implements Protocol {

    int messageType = TASK_COMPLETE;
    int portNumber;
    String ipAddress;
}
