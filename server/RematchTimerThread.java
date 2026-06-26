package server;

import shared.Message;
import java.util.List;
import java.util.Set;

public class RematchTimerThread implements Runnable {

    private static final int REMATCH_VOTE_SECONDS = 10;
    private final Broadcaster broadcaster;
    private final GameState gameState;
    private final LobbyManager lobby;

    public RematchTimerThread(Broadcaster broadcaster, GameState gameState, LobbyManager lobby) {
        this.broadcaster = broadcaster;
        this.gameState = gameState;
        this.lobby = lobby;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(REMATCH_VOTE_SECONDS * 1000L);

            List<ClientHandler> currentClients = broadcaster.getClients();

            lobby.pruneDisconnectedVoters(currentClients);

            boolean hasEnough = lobby.finalizeRematch();
            Set<ClientHandler> votedYes = lobby.getRematchVotesSnapshot();

            gameState.reset();

            for (ClientHandler client : currentClients) {
                if (votedYes.contains(client)) {
                    client.promoteToPlayer();
                    client.sendMessage(new Message(Message.Type.REMATCH_START));
                } else {
                    client.setSpectatorMode(true);
                    client.sendMessage(new Message(Message.Type.SPECTATE));
                }
            }

            if (hasEnough) {
                System.out.println("[ SERVER ] Rematch confirmed with " + votedYes.size() + " players.");
            } else {
                System.out.println("[ SERVER ] Not enough rematch votes. YES voters returned to lobby, others spectating.");
            }

            broadcaster.broadcast(new Message(Message.Type.UPDATE_GAME,
                    gameState.getBoardCopy(), gameState.getScores(), gameState.getPlayerColors()));

            lobby.resetForRematch();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}