package server;

import shared.Message;

import java.io.IOException;

public class TimerThread implements Runnable{

    private final Broadcaster broadcaster;
    private final GameState gameState;
    private final int duration;

    public TimerThread(Broadcaster broadcaster, GameState gameState, int duration){
        this.broadcaster=broadcaster;
        this.gameState=gameState;
        this.duration=duration;
    }

    @Override
    public void run(){
        try{
            for (int remaining = duration; remaining>=0; remaining--){
                broadcaster.broadcast(new Message(Message.Type.TIMER, remaining));
                Thread.sleep(1000);
            }
            broadcaster.broadcast(new Message(
                    Message.Type.GAME_OVER,
                    gameState.getBoardCopy(),
                    gameState.getScores()
            ));
            System.out.println("[ SERVER ] Game over!");
        }
        catch (InterruptedException e){
            System.out.println("[ SERVER ] Timer interrupted! "+ e.getMessage());
        }
    }

}
