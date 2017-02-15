package cs455.overlay.wireformats.eventfactory;

import cs455.overlay.wireformats.*;
import cs455.overlay.wireformats.nodemessages.Receiving.*;
import cs455.overlay.wireformats.nodemessages.*;
import cs455.overlay.wireformats.nodemessages.Sending.Deregister;
import cs455.overlay.wireformats.nodemessages.Message;
import cs455.overlay.wireformats.nodemessages.Sending.SendRegister;
import cs455.overlay.wireformats.registrymessages.sending.DeregistrationResponse;
import cs455.overlay.wireformats.registrymessages.receiving.DeregisterRequestReceive;
import cs455.overlay.wireformats.registrymessages.receiving.RegisterRequestReceive;
import cs455.overlay.wireformats.registrymessages.sending.RegisterRequestResponse;

import java.io.IOException;

public final class EventFactory {

    /**
     * Class creates events based on the type of message received at a node so the node can respond accordingly.
     */

    private static final EventFactory instance = new EventFactory();

    private EventFactory() { }

    public static EventFactory getInstance() {
        return instance;
    }

    public static Event<SendRegister> createRegisterSendEvent() {
        SendRegister sendRegister = new SendRegister();
        return sendRegister;
    }

    public static Event<RegisterRequestReceive> receiveRegisterEvent(
            byte[] marshalledBytes) throws IOException {
        RegisterRequestReceive registerRequestReceive = new RegisterRequestReceive(marshalledBytes);
        return registerRequestReceive;
    }

    public static Event<RegisterRequestResponse> createRegisterResponseEvent() {
        RegisterRequestResponse registerRequestResponse = new RegisterRequestResponse();
        return registerRequestResponse;
    }

    public static Event<RegistryResponseReceive> receiveRegisterResponseEvent(
            byte[] marshalledBytes) throws IOException {
        RegistryResponseReceive registerResponse = new RegistryResponseReceive(marshalledBytes);
        return registerResponse;
    }

    public static Event<Deregister> createDeregistrationEvent() {
        Deregister sendDeregister = new Deregister();
        return sendDeregister;
    }

    public static Event<DeregisterRequestReceive> receiveDeregistrationEvent(
            byte[] marshalledBytes) throws IOException {
        DeregisterRequestReceive deregisterRequestReceive =
                new DeregisterRequestReceive(marshalledBytes);
        return deregisterRequestReceive;
    }

    public static Event<DeregistrationResponse> sendDeregistrationResponse() {
        DeregistrationResponse deregistrationResponse = new DeregistrationResponse();
        return deregistrationResponse;
    }

    public static Event<DeregisterResponseReceive> receiveDeregisterResponse(
            byte[] marshalledBytes) throws IOException {
        DeregisterResponseReceive deregisterResponse = new DeregisterResponseReceive(marshalledBytes);
        return deregisterResponse;
    }

    public static Event<MessagingNodesListReceive> receiveMessagingNodesList(
            byte[] marshalledBytes) throws IOException {
        MessagingNodesListReceive messagingNodesListReceive = new MessagingNodesListReceive(marshalledBytes);
        return messagingNodesListReceive;
    }

    public static Event<NodeConnection> sendNodeConnection() {
        NodeConnection sendNodeConnection = new NodeConnection();
        return sendNodeConnection;
    }

    public static Event<NodeConnection> receiveNodeConnection(
            byte[] marshalledBytes) throws IOException {
        NodeConnection receiveNodeConnection = new NodeConnection();
        receiveNodeConnection.receiveBytes(marshalledBytes);
        return receiveNodeConnection;
    }

    public static Event<LinkWeightsReceive> receiveLinkWeights(
            byte[] marshalledBytes) throws IOException {
        LinkWeightsReceive linkWeightsReceive = new LinkWeightsReceive(marshalledBytes);
        return linkWeightsReceive;
    }

    public static Event<TaskInitiate> receiveTaskInitiate(
            byte[] marshalledBytes) throws IOException {
        TaskInitiate taskInitiate = new TaskInitiate();
        taskInitiate.readTaskInitiateMessage(marshalledBytes);
        return taskInitiate;
    }

    public static Event<Message> receiveMessage(
            byte[] marshalledBytes) throws IOException {
        Message messageReceive = new Message();
        messageReceive.readMessage(marshalledBytes);
        return messageReceive;
    }

    public static Event<TaskComplete> taskComplete(
            byte[] marshalledBytes) throws IOException {
        TaskComplete taskComplete = new TaskComplete();
        taskComplete.readMessage(marshalledBytes);
        return taskComplete;
    }

    public static Event<PullTrafficSummary> receivePullTrafficSummary(
            byte[] marshalledBytes) throws IOException {
        PullTrafficSummary pullTrafficSummary = new PullTrafficSummary();
        pullTrafficSummary.readMessage(marshalledBytes);
        return pullTrafficSummary;
    }

    public static Event<TrafficSummary> receiveTrafficSummary(
            byte[] marshalledBytes) throws IOException {
        TrafficSummary trafficSummary = new TrafficSummary();
        trafficSummary.readMessage(marshalledBytes);
        return trafficSummary;
    }
}
