package shared;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkSerializer {

    private final ObjectOutputStream writer;
    private final ObjectInputStream reader;

    public NetworkSerializer(Socket socket) throws IOException{
        this.writer = new ObjectOutputStream(socket.getOutputStream());
        this.reader = new ObjectInputStream(socket.getInputStream());
    }

    public void send(Message message) throws IOException{
        writer.writeObject(message);
        writer.flush();
    }

    public Message receive() throws IOException, ClassNotFoundException{
        return (Message) reader.readObject();
    }

}
