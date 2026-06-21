package client.ui;

import shared.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PixelCanvas extends JPanel {

    private static final int GRID_SIZE = Config.BOARD_SIZE;
    private static final int PIXEL_SIZE = 12;
    private int[][] gridData = new int[GRID_SIZE][GRID_SIZE];
    private boolean gameActive = true;
    private ScorePanel scorePanel;
    private GameWindow gameWindow;
    private boolean gameStarted = false;
    private boolean isSpectator = false;
    private static final Color[] PLAYER_COLORS = {
            Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
            Color.ORANGE, Color.MAGENTA, Color.CYAN, Color.PINK
    };
    public PixelCanvas(ScorePanel scorePanel, GameWindow gameWindow) {
        this.scorePanel = scorePanel;
        this.gameWindow = gameWindow;

        setPreferredSize(new Dimension(GRID_SIZE * PIXEL_SIZE, GRID_SIZE * PIXEL_SIZE));
        setBackground(new Color(30, 30, 30));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!gameActive || !gameStarted || isSpectator) return;

                int col = e.getX() / PIXEL_SIZE;
                int row = e.getY() / PIXEL_SIZE;

                if (col >= 0 && col < GRID_SIZE && row >= 0 && row < GRID_SIZE) {
                    if (gameWindow.getNetworkHandler() != null) {
                        gameWindow.getNetworkHandler().sendClick(row, col);
                    }
                }
            }
        });
    }

    public void updateGrid(int[][] newGrid) {
        this.gridData = newGrid;
        repaint();
    }
    public void startGame(){
        this.gameStarted=true;
    }
    public void endGame() {
        this.gameActive = false;
    }
    public void setSpectator(boolean spectator) {
        this.isSpectator = spectator;
    }
    public void resetBoard() {
        this.gridData = new int[GRID_SIZE][GRID_SIZE];
        this.gameActive = true;
        this.gameStarted = false;
        repaint();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                int val = gridData[r][c];
                if (val == 0) {
                    g2d.setColor(new Color(60, 60, 60));
                } else {
                    g2d.setColor(PLAYER_COLORS[(val - 1) % PLAYER_COLORS.length]);
                }
                g2d.fillRect(c * PIXEL_SIZE, r * PIXEL_SIZE, PIXEL_SIZE - 1, PIXEL_SIZE - 1);
            }
        }
    }
}