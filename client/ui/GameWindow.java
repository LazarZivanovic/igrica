package client.ui;

import client.NetworkHandler;
import shared.Config;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {

    private client.ui.PixelCanvas pixelCanvas;
    private client.ui.ScorePanel scorePanel;
    private JProgressBar timerBar;
    private JLabel timeLabel;
    private NetworkHandler networkHandler;

    private int remainingTime = Config.GAME_DURATION;

    // Konstruktor sada ispravno prima mrežni handler
    public GameWindow(NetworkHandler networkHandler) {
        this.networkHandler = networkHandler;

        setTitle("Pixel War");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Gornji panel sa tajmerom
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(45, 45, 45));

        timerBar = new JProgressBar(0, Config.GAME_DURATION);
        timerBar.setValue(remainingTime);
        timerBar.setForeground(new Color(76, 154, 42));
        timerBar.setBackground(Color.DARK_GRAY);
        timerBar.setBorderPainted(false);

        timeLabel = new JLabel("00:" + remainingTime, SwingConstants.CENTER);
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timeLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        topPanel.add(timerBar, BorderLayout.CENTER);
        topPanel.add(timeLabel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // Desni panel sa skorovima
        scorePanel = new ScorePanel();
        scorePanel.setPreferredSize(new Dimension(200, 0));
        add(scorePanel, BorderLayout.EAST);

        // Centralni deo za crtanje - prosljeđujemo i prozor (this)
        pixelCanvas = new PixelCanvas(scorePanel, this);
        add(pixelCanvas, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Logika tajmera
        Timer tajmer = new Timer(1000, e -> {
            if (remainingTime > 0) {
                remainingTime--;
                timerBar.setValue(remainingTime);
                timeLabel.setText(String.format("00:%02d", remainingTime));
            } else {
                ((Timer)e.getSource()).stop();
                pixelCanvas.endGame();

                int skor1 = scorePanel.getMyScore();
                int skor2 = scorePanel.getOpponentScore();

                String poruka = "=== IGRA JE ZAVRŠENA ===\n\n" +
                        "Konačni učinak:\n" +
                        " Crveni: " + skor1 + " piksela\n" +
                        " Plavi: " + skor2 + " piksela";

                JOptionPane.showMessageDialog(this, poruka, "Kraj igre", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        tajmer.start();
    }

    public PixelCanvas getPixelCanvas() {
        return this.pixelCanvas;
    }

    public ScorePanel getScorePanel() {
        return this.scorePanel;
    }

    public NetworkHandler getNetworkHandler() {
        return this.networkHandler;
    }
}