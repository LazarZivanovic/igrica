package server;

import shared.Message;

import java.util.Collections;
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
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }
    public List<ClientHandler> getClients() {
        return Collections.unmodifiableList(clients);
    }

    public int countActivePlayers() {
        int count = 0;
        for (ClientHandler client : clients) {
            if (!client.isSpectator()) count++;
        }
        return count;
    }
}