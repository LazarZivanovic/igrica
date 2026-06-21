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

    public void addPlayer(String name){
        lock.writeLock().lock();
        try {
            scores.put(name, 0);
            if (!playerColors.containsKey(name)) {
                playerColors.put(name, nextColor.getAndIncrement());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removePlayer(String name) {
        lock.writeLock().lock();
        try {
            scores.remove(name);
            playerColors.remove(name);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Map<String, Integer> getPlayerColors(){
        lock.readLock().lock();
        try {
            return new ConcurrentHashMap<>(playerColors);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Map<String, Integer> getScores(){
        lock.readLock().lock();
        try {
            return new ConcurrentHashMap<>(scores);
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean paintPixel(int row, int col, String playerName){
        if (row < 0 || row >= Config.BOARD_SIZE || col < 0 || col >= Config.BOARD_SIZE) {
            return false;
        }
        lock.writeLock().lock();
        try{
            Integer playerColor = playerColors.get(playerName);
            if (playerColor == null) {
                return false;
            }
            int previousColor = board[row][col];
            if (previousColor != 0){
                String previousPlayer = getPlayerByColor(previousColor);
                if (previousPlayer != null){
                    scores.merge(previousPlayer, -1, Integer::sum);
                }
            }
            board[row][col] = playerColor;
            scores.merge(playerName, 1, Integer::sum);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int[][] getBoardCopy(){
        lock.readLock().lock();
        try{
            int[][] copy = new int[Config.BOARD_SIZE][Config.BOARD_SIZE];
            for (int i = 0; i < Config.BOARD_SIZE; i++){
                copy[i] = board[i].clone();
            }
            return copy;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void reset() {
        lock.writeLock().lock();
        try {
            for (int[] row : board) {
                java.util.Arrays.fill(row, 0);
            }
            scores.clear();
            playerColors.clear();
            nextColor.set(1);
        } finally {
            lock.writeLock().unlock();
        }
        System.out.println("[ GAMESTATE ] Board reset!");
    }

    private String getPlayerByColor(int color){
        return playerColors.entrySet().stream()
                .filter(e -> e.getValue() == color)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
}