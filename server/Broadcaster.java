package server;

import shared.Message;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Broadcaster {

    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public void addClient(ClientHandler client){
        clients.add(client);
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public void broadcast(Message message){
        for(ClientHandler client: clients){
            client.sendMessage(message);
        }
    }

}
