package server;

import shared.Config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class LobbyManager {

    public enum JoinDecision {
        ACCEPTED_AS_PLAYER,
        SPECTATE_GAME_IN_PROGRESS,
        SPECTATE_WAITING_REMATCH,
        REJECTED_FULL
    }

    private final Set<ClientHandler> rematchVotes = new CopyOnWriteArraySet<>();
    private boolean rematchDecided = false;
    private boolean gameStarted = false;
    private boolean waitingForRematch = false;
    private int lobbyPlayerCount = 0;
    private Broadcaster broadcaster;

    public void setBroadcaster(Broadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    public synchronized boolean hasEnoughPlayers() {
        if (waitingForRematch) return false;
        if (gameStarted) return false;
        int active = broadcaster != null ? broadcaster.countActivePlayers() : lobbyPlayerCount;
        return active >= Config.MIN_PLAYERS;
    }
    public synchronized JoinDecision attemptJoin() {
        if (gameStarted) {
            return JoinDecision.SPECTATE_GAME_IN_PROGRESS;
        }
        if (waitingForRematch) {
            return JoinDecision.SPECTATE_WAITING_REMATCH;
        }
        if (lobbyPlayerCount >= Config.MAX_PLAYERS) {
            return JoinDecision.REJECTED_FULL;
        }
        lobbyPlayerCount++;
        return JoinDecision.ACCEPTED_AS_PLAYER;
    }

    public synchronized void setGameStarted() {
        this.gameStarted = true;
    }
    public synchronized void cancelGameStart() {
        this.gameStarted = false;
    }

    public synchronized boolean isGameInProgress() {
        return gameStarted;
    }

    public synchronized void playerLeft(boolean wasSpectator, boolean countedInLobby) {
        if (countedInLobby && lobbyPlayerCount > 0) {
            lobbyPlayerCount--;
        }
    }

    public synchronized void removeRematchVote(ClientHandler client) {
        rematchVotes.remove(client);
    }

    public synchronized void registerRematchVote(ClientHandler client) {
        if (!waitingForRematch || rematchDecided) return;
        rematchVotes.add(client);
    }

    public synchronized Set<ClientHandler> getRematchVotesSnapshot() {
        return new HashSet<>(rematchVotes);
    }

    public synchronized void pruneDisconnectedVoters(List<ClientHandler> currentClients) {
        rematchVotes.removeIf(client -> !currentClients.contains(client));
    }

    public synchronized boolean finalizeRematch() {
        if (rematchDecided) return false;
        if (rematchVotes.size() < Config.MIN_PLAYERS) return false;
        rematchDecided = true;
        return true;
    }

    public synchronized int getRematchVoteCount() {
        return rematchVotes.size();
    }
    public synchronized void markRoundEnded() {
        this.gameStarted = false;
        this.waitingForRematch = true;
        this.rematchVotes.clear();
        this.rematchDecided = false;
    }
    public synchronized void markRoundEndedEarly() {
        this.gameStarted = false;
        this.waitingForRematch = false;
        this.rematchVotes.clear();
        this.rematchDecided = false;
        this.lobbyPlayerCount = broadcaster != null ? broadcaster.countActivePlayers() : 0;
    }

    public synchronized boolean isWaitingForRematch() {
        return waitingForRematch;
    }

    public synchronized void resetForRematch() {
        rematchVotes.clear();
        rematchDecided = false;
        gameStarted = false;
        waitingForRematch = false;
        lobbyPlayerCount = broadcaster != null ? broadcaster.countActivePlayers() : 0;
    }
}