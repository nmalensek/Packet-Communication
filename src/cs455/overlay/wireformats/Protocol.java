package cs455.overlay.wireformats;

public interface Protocol {

    int DEREGISTER_REQUEST = 0;
    int REGISTER_REQUEST = 1;
    int REGISTER_RESPONSE = 2;
    int MESSAGING_NODES_LIST = 3;
    int LINK_WEIGHTS = 4;
    int TASK_INITIATE = 5;
    int SEND_MESSAGE = 6;
    int TASK_COMPLETE = 7;
    int PULL_TRAFFIC_SUMMARY = 8;
    int TRAFFIC_SUMMARY = 9;

}
