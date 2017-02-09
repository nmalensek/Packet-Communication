package cs455.overlay.wireformats;

import cs455.overlay.dijkstra.Edge;
import cs455.overlay.wireformats.registrymessages.sending.LinkWeightsSend;

import java.io.*;
import java.util.List;

public class TaskInitiate implements Protocol, Event<TaskInitiate> {

    int messageType = TASK_INITIATE;
    int rounds;

    public TaskInitiate getType() {
        return this;
    }

    //marshalls bytes
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream =
                new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));

        dataOutputStream.writeInt(messageType);
        dataOutputStream.writeInt(rounds);

        dataOutputStream.flush();
        marshalledBytes = byteArrayOutputStream.toByteArray();

        byteArrayOutputStream.close();
        dataOutputStream.close();

        return marshalledBytes;
    }

    public void readTaskInitiateMessage(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream byteArrayInputStream =
                new ByteArrayInputStream(marshalledBytes);
        DataInputStream dataInputStream =
                new DataInputStream(new BufferedInputStream(byteArrayInputStream));

        messageType = dataInputStream.readInt();
        rounds = dataInputStream.readInt();

        byteArrayInputStream.close();
        dataInputStream.close();
    }

    public void setRounds(int rounds) { this.rounds = rounds; }
    public int getRounds() { return rounds; }
}
