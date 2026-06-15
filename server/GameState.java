package server;

import shared.Config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GameState {

    private final int[][] board = new int[Config.BOARD_SIZE][Config.BOARD_SIZE];
    private final Map<String, Integer> scores = new ConcurrentHashMap<>();
    private final Map<String, Integer> playerColors = new ConcurrentHashMap<>();
    private final AtomicInteger nextColor = new AtomicInteger(1);

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public synchronized void addPlayer(String name){
        scores.put(name,0);
        playerColors.put(name,nextColor.getAndIncrement());
        System.out.println("[ GAMESTATE ] "+ name + " added, color: "+ (nextColor.get()-1));
    }

    public boolean paintPixel(int row, int col, String playerName){
        lock.writeLock().lock();
        try{
            int playerColor = playerColors.get(playerName);
            int previousColor = board[row][col];
            if (previousColor!=0){
                String previousPlayer = getPlayerByColor(previousColor);
                if(previousPlayer!=null){
                    scores.merge(previousPlayer,-1,Integer::sum);
                }
            }
            board[row][col] = playerColor;
            scores.merge(playerName,1,Integer::sum);
            return true;
        }finally {
            lock.writeLock().unlock();
        }
    }

    public int[][] getBoardCopy(){
        lock.readLock().lock();
        try{
            int[][] copy = new int[Config.BOARD_SIZE][Config.BOARD_SIZE];
            for (int i = 0; i<Config.BOARD_SIZE; i++){
                copy[i] = board[i].clone();
            }
            return copy;
        }finally {
            lock.readLock().unlock();
        }
    }

    private String getPlayerByColor(int color){
        return playerColors.entrySet().stream()
                .filter(e->e.getValue()==color)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
    public Map<String, Integer> getScores(){
        return new ConcurrentHashMap<>(scores);
    }
}
