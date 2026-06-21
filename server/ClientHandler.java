package server;

import shared.Message;
import shared.NetworkSerializer;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final GameState gameState;
    private final Broadcaster broadcaster;
    private final LobbyManager lobby;
    private NetworkSerializer serializer;
    private volatile String playerName;
    private volatile boolean isSpectator = false;
    private boolean countedInLobby = false;

    public ClientHandler(Socket socket, GameState gameState, Broadcaster broadcaster, LobbyManager lobby) {
        this.socket = socket;
        this.gameState = gameState;
        this.broadcaster = broadcaster;
        this.lobby = lobby;
    }

    @Override
    public void run() {
        try {
            serializer = new NetworkSerializer(socket);
            while (true) {
                Message msg = serializer.receive();
                if (!handleMessage(msg)) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("[ SERVER ] Error/Disconnect for: " + playerName);
        } finally {
            disconnect();
        }
    }
    private boolean handleMessage(Message msg) {
        switch (msg.getType()) {
            case JOIN -> handleJoin(msg);
            case CLICK -> handleClick(msg);
            case REMATCH_YES -> handleRematchYes();
            case REMATCH_NO -> { return false; }
            default -> System.out.println("[ SERVER ] Unhandled message type from " + playerName + ": " + msg.getType());
        }
        return true;
    }

    private void handleJoin(Message msg) {
        this.playerName = msg.getName();

        LobbyManager.JoinDecision decision = lobby.attemptJoin();

        switch (decision) {
            case SPECTATE_GAME_IN_PROGRESS -> {
                this.isSpectator = true;
                this.countedInLobby = false;
                broadcaster.addClient(this);
                System.out.println("[ SERVER ] " + playerName + " joined as a spectator.");
                sendMessage(new Message(Message.Type.SPECTATE));
                sendMessage(new Message(Message.Type.UPDATE_GAME,
                        gameState.getBoardCopy(), gameState.getScores(), gameState.getPlayerColors()));
            }
            case SPECTATE_WAITING_REMATCH -> {
                this.isSpectator = true;
                this.countedInLobby = false;
                broadcaster.addClient(this);
                System.out.println("[ SERVER ] " + playerName + " joined during rematch vote, spectating.");
                sendMessage(new Message(Message.Type.SPECTATE));
                sendMessage(new Message(Message.Type.GAME_OVER,
                        gameState.getBoardCopy(), gameState.getScores(), gameState.getPlayerColors()));
            }
            case ACCEPTED_AS_PLAYER -> {
                this.isSpectator = false;
                this.countedInLobby = true;
                broadcaster.addClient(this);
                gameState.addPlayer(playerName);
                broadcaster.broadcast(new Message(Message.Type.JOIN, playerName));
            }
            case REJECTED_FULL -> {
                System.out.println("[ SERVER ] " + playerName + " rejected, lobby is full.");
                sendMessage(new Message(Message.Type.LOBBY_FULL));
                playerName = null;
                try { socket.close(); } catch (IOException e) { /* */ }
            }
        }
    }

    private void handleRematchYes() {
        System.out.println("[ SERVER ] " + playerName + " voted YES for rematch!");

        if (lobby.isGameInProgress()) {
            setSpectatorMode(true);
            sendMessage(new Message(Message.Type.SPECTATE));
            sendMessage(new Message(Message.Type.UPDATE_GAME,
                    gameState.getBoardCopy(), gameState.getScores(), gameState.getPlayerColors()));
            return;
        }

        lobby.registerRematchVote(this);
        System.out.println("[ SERVER ] Rematch votes: " + lobby.getRematchVoteCount());
    }

    private void disconnect() {
        if (playerName != null) {
            String leavingPlayer = playerName;
            broadcaster.broadcast(new Message(Message.Type.PLAYER_LEFT, leavingPlayer));
            broadcaster.removeClient(this);
            gameState.removePlayer(leavingPlayer);
            lobby.removeRematchVote(this);
            lobby.playerLeft(this.isSpectator, this.countedInLobby);
            playerName = null;
        }
        try { if (socket != null) socket.close(); } catch (IOException e) { e.printStackTrace(); }
    }

    private void handleClick(Message msg) {
        if (!isSpectator && lobby.isGameInProgress()) {
            gameState.paintPixel(msg.getRow(), msg.getCol(), playerName);
            broadcaster.broadcast(new Message(Message.Type.UPDATE_GAME,
                    gameState.getBoardCopy(), gameState.getScores(), gameState.getPlayerColors()));
        }
    }

    public boolean isSpectator() { return isSpectator; }
    public String getPlayerName() { return playerName; }

    public void promoteToPlayer() {
        if (playerName == null) return; // disconnected right as promotion happened
        this.isSpectator = false;
        this.countedInLobby = true;
        gameState.addPlayer(playerName);
    }

    public void setSpectatorMode(boolean spectator) { this.isSpectator = spectator; }

    public void sendMessage(Message msg) {
        synchronized (this) {
            try {
                serializer.send(msg);
            } catch (Exception e) {
                /**/
            }
        }
    }
}