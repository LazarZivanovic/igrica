package client.ui;

import client.NetworkHandler;
import shared.Config;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class GameWindow extends JFrame {

    private PixelCanvas pixelCanvas;
    private ScorePanel scorePanel;
    private JProgressBar timerBar;
    private JLabel timeLabel;
    private NetworkHandler networkHandler;

    public GameWindow(NetworkHandler networkHandler) {
        this.networkHandler = networkHandler;

        setTitle("Pixel War");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(45, 45, 45));

        timerBar = new JProgressBar(0, Config.GAME_DURATION);
        timerBar.setValue(Config.GAME_DURATION);
        timerBar.setForeground(new Color(76, 154, 42));
        timerBar.setBackground(Color.DARK_GRAY);
        timerBar.setBorderPainted(false);

        timeLabel = new JLabel(String.format("00:%02d", Config.GAME_DURATION), SwingConstants.CENTER);
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timeLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        topPanel.add(timerBar, BorderLayout.CENTER);
        topPanel.add(timeLabel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        scorePanel = new ScorePanel();
        scorePanel.setPreferredSize(new Dimension(200, 0));
        add(scorePanel, BorderLayout.EAST);

        pixelCanvas = new PixelCanvas(scorePanel, this);
        add(pixelCanvas, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void updateTimer(int seconds) {
        timerBar.setValue(seconds);
        timeLabel.setText(String.format("00:%02d", seconds));
    }

    public void showGameOver(Map<String, Integer> scores) {
        pixelCanvas.endGame();
        if (scores == null) return;
        StringBuilder sb = new StringBuilder("=== GAME OVER ===\n\n");
        scores.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .forEach(e -> sb.append(e.getKey())
                        .append(": ")
                        .append(e.getValue())
                        .append(" pixels\n"));
        JOptionPane.showMessageDialog(this, sb.toString(), "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }
    public void showCountdown(int seconds) {
        if (seconds > 0) {
            timeLabel.setText("Starting in: " + seconds + "s");
        } else {
            timeLabel.setText("GO!!!");
            getPixelCanvas().startGame();
        }
    }
    public void showWaitingForRematch() {
        timeLabel.setText("Waiting for players...");
        timerBar.setValue(0);
        getPixelCanvas().endGame();
    }

    public void resetForRematch() {
        getPixelCanvas().setSpectator(false);
        getPixelCanvas().resetBoard();
        timerBar.setMaximum(Config.GAME_DURATION);
        timerBar.setValue(Config.GAME_DURATION);
        timeLabel.setText("Waiting for next round...");
        scorePanel.resetScores();
        setTitle("Pixel War");
    }
    public void enterSpectatorMode() {
        getPixelCanvas().setSpectator(true);
        timeLabel.setText("Spectating - wait for next round...");
        setTitle("Pixel War — SPECTATING");
    }
    public PixelCanvas getPixelCanvas() { return this.pixelCanvas; }
    public ScorePanel getScorePanel() { return this.scorePanel; }
    public NetworkHandler getNetworkHandler() { return this.networkHandler; }
}