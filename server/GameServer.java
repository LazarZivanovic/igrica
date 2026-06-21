package server;

import shared.Config;
import shared.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameServer {

    private final ExecutorService threadPool = Executors.newCachedThreadPool();
    private final GameState gameState = new GameState();
    private final Broadcaster broadcaster = new Broadcaster();
    private final LobbyManager lobby = new LobbyManager();

    public void start() throws IOException {
        lobby.setBroadcaster(broadcaster);

        ServerSocket serverSocket = new ServerSocket(Config.PORT);
        System.out.println("[ SERVER ] Is running on port " + Config.PORT);

        new Thread(() -> {
            while (true) {
                try {
                    System.out.println("[ LOBBY ] Waiting for players...");
                    while (!lobby.hasEnoughPlayers()) {
                        Thread.sleep(500);
                    }

                    System.out.println("[ LOBBY ] Starting countdown...");
                    if (runCountdownSynchronous()) {
                        new TimerThread(broadcaster, gameState, lobby, Config.GAME_DURATION).run();

                        while (lobby.isWaitingForRematch()) {
                            Thread.sleep(500);
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                } catch (Exception e) {
                    System.out.println("[ SERVER ] Unexpected error in game loop, recovering: " + e);
                    e.printStackTrace();
                }
            }
        }).start();

        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[ SERVER ] New player is connected!");
                threadPool.submit(new ClientHandler(clientSocket, gameState, broadcaster, lobby));
            } catch (IOException e) {
                System.out.println("[ SERVER ] Failed to accept a connection: " + e.getMessage());
            }
        }
    }

    private boolean runCountdownSynchronous() throws InterruptedException {
        lobby.setGameStarted();
        for (int i = 10; i >= 0; i--) {
            if (broadcaster.countActivePlayers() < Config.MIN_PLAYERS) {
                System.out.println("[ SERVER ] Countdown cancelled, not enough active players.");
                lobby.cancelGameStart();
                return false;
            }
            broadcaster.broadcast(new Message(Message.Type.LOBBY_COUNTDOWN, i));
            Thread.sleep(1000);
        }
        return true;
    }

    public static void main(String[] args) throws IOException {
        new GameServer().start();
    }
}