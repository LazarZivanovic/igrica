package client;

import shared.Message;
import shared.NetworkSerializer;
import shared.Config;
import client.ui.GameWindow;
import javax.swing.*;
import java.io.IOException;
import java.net.Socket;

public class NetworkHandler {
    private Socket socket;
    private NetworkSerializer serializer;
    private GameWindow gameWindow;
    private String myName;

    public NetworkHandler(String host, int port) {
        try {
            myName = JOptionPane.showInputDialog(null, "Enter your name:", "Login", JOptionPane.QUESTION_MESSAGE);
            if (myName == null) return;
            if (myName.trim().isEmpty()) {
                myName = "Player_" + (int)(Math.random() * 1000);
            }

            socket = new Socket(host, port);
            serializer = new NetworkSerializer(socket);
            System.out.println("[CLIENT] Connected to server!");

            gameWindow = new GameWindow(this);
            serializer.send(new Message(Message.Type.JOIN, myName));
            new Thread(this::listenToServer).start();

        } catch (IOException e) {
            int choice = JOptionPane.showConfirmDialog(null,
                    "Server is not available. Try again?",
                    "Connection Error",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                new NetworkHandler(host, port);
            }
        }
    }

    private void listenToServer() {
        try {
            while (true) {
                Message msg = serializer.receive();

                if (msg.getType() == Message.Type.UPDATE_GAME) {
                    SwingUtilities.invokeLater(() -> {
                        gameWindow.getPixelCanvas().updateGrid(msg.getGrid());
                        gameWindow.getScorePanel().updateScores(msg.getScores(), msg.getPlayerColors());
                    });
                }else if (msg.getType() == Message.Type.TIMER) {
                    SwingUtilities.invokeLater(() -> {
                        gameWindow.updateTimer(msg.getRemainingTime());
                        gameWindow.getPixelCanvas().startGame();
                    });
                } else if (msg.getType() == Message.Type.LOBBY_COUNTDOWN) {
                    SwingUtilities.invokeLater(() ->
                            gameWindow.showCountdown(msg.getRemainingTime()));
                } else if (msg.getType() == Message.Type.GAME_OVER) {
                    SwingUtilities.invokeLater(() -> {
                        gameWindow.showGameOver(msg.getScores());
                        int choice = JOptionPane.showConfirmDialog(
                                gameWindow, "Play again?", "Rematch",
                                JOptionPane.YES_NO_OPTION
                        );
                        if (choice == JOptionPane.YES_OPTION) {
                            try {
                                serializer.send(new Message(Message.Type.REMATCH_YES, myName));
                                gameWindow.showWaitingForRematch();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                serializer.send(new Message(Message.Type.REMATCH_NO, myName));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.exit(0);
                        }
                    });
                } else if (msg.getType() == Message.Type.REMATCH_START) {
                    SwingUtilities.invokeLater(gameWindow::resetForRematch);
                }else if (msg.getType() == Message.Type.SPECTATE) {
                    SwingUtilities.invokeLater(gameWindow::enterSpectatorMode);
                } else if (msg.getType() == Message.Type.LOBBY_FULL) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(gameWindow,
                                "Lobby is full. Please try again later.",
                                "Cannot Join", JOptionPane.WARNING_MESSAGE);
                        System.exit(0);
                    });
                }else if (msg.getType() == Message.Type.PLAYER_LEFT) {
                    String nameToRemove = msg.getName();
                    SwingUtilities.invokeLater(() -> {
                    gameWindow.getScorePanel().removePlayer(nameToRemove);
                });
            }
            }
        } catch (Exception e) {
            System.out.println("[CLIENT] Connection lost.");
        } finally {
            closeAll();
        }
    }

    public void sendClick(int row, int col) {
        try {
            serializer.send(new Message(Message.Type.CLICK, row, col));
        } catch (IOException e) {
            System.err.println("[CLIENT] Send error: " + e.getMessage());
        }
    }

    private void closeAll() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMyName() { return myName; }
}