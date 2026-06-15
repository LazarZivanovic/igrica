package server;

import shared.Config;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameServer {

    private ExecutorService threadPool = Executors.newFixedThreadPool(Config.MAX_PLAYERS);

    public void start() throws IOException{
        ServerSocket serverSocket = new ServerSocket(Config.PORT);
        System.out.println("[ SERVER ] Is running on port "+ Config.PORT);
        while(true){
            Socket clientSocket = serverSocket.accept();
            System.out.println("[ SERVER ] New player is connected!");
            threadPool.submit(new ClientHandler(clientSocket));
        }
    }
    public static void main(String[] args) throws IOException{
        new GameServer().start();
    }

}
