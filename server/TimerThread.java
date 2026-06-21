package server;

import shared.Config;
import shared.Message;

public class TimerThread implements Runnable {

    private final Broadcaster broadcaster;
    private final GameState gameState;
    private final LobbyManager lobby;
    private final int duration;

    public TimerThread(Broadcaster broadcaster, GameState gameState, LobbyManager lobby, int duration) {
        this.broadcaster = broadcaster;
        this.gameState = gameState;
        this.lobby = lobby;
        this.duration = duration;
    }

    @Override
    public void run() {
        try {
            boolean endedEarly = false;
            for (int remaining = duration; remaining >= 0; remaining--) {
                if (broadcaster.countActivePlayers() < Config.MIN_PLAYERS) {
                    System.out.println("[ SERVER ] Not enough active players, ending round early.");
                    endedEarly = true;
                    break;
                }
                broadcaster.broadcast(new Message(Message.Type.TIMER, remaining));
                if (remaining > 0) Thread.sleep(1000);
            }

            broadcaster.broadcast(new Message(
                    Message.Type.GAME_OVER,
                    gameState.getBoardCopy(),
                    gameState.getScores(),
                    gameState.getPlayerColors()
            ));
            System.out.println("[ SERVER ] Game over!");

            boolean nobodyLeftAtAll = endedEarly && broadcaster.countActivePlayers() == 0;

            if (nobodyLeftAtAll) {
                lobby.markRoundEndedEarly();
                gameState.reset();
                for (ClientHandler client : broadcaster.getClients()) {
                    client.setSpectatorMode(true);
                    client.sendMessage(new Message(Message.Type.SPECTATE));
                }
                broadcaster.broadcast(new Message(Message.Type.UPDATE_GAME,
                        gameState.getBoardCopy(), gameState.getScores(), gameState.getPlayerColors()));
            } else {
                lobby.markRoundEnded();
                new Thread(new RematchTimerThread(broadcaster, gameState, lobby)).start();
            }

        } catch (InterruptedException e) {
            System.out.println("[ SERVER ] Timer interrupted! " + e.getMessage());
        }
    }
}