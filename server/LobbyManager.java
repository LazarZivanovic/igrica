package server;

import shared.Config;

import java.util.concurrent.CountDownLatch;

public class LobbyManager {

    private final CountDownLatch latch = new CountDownLatch(Config.MIN_PLAYERS);
    private int playerCount = 0;

    public synchronized boolean playerJoined() {
        playerCount++;
        if (playerCount>Config.MAX_PLAYERS)return false;
        latch.countDown();
        return true;
    }

    public void waitForPlayers() throws InterruptedException{
        System.out.println("[ LOBBY ] Waiting for " + Config.MIN_PLAYERS + " players...");
        latch.await();
        System.out.println("[ LOBBY ] Game starting!");
    }

    public int getPlayerCount() { return playerCount; }
}
