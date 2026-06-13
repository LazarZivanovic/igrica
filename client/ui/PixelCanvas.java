package client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PixelCanvas extends JPanel {

    private static final int GRID_SIZE = 50;
    private static final int PIXEL_SIZE = 12;
    private int[][] gridData = new int[GRID_SIZE][GRID_SIZE];
    private boolean igraAktivna = true;
    private ScorePanel scorePanel;
    private GameWindow gameWindow;

    public PixelCanvas(ScorePanel scorePanel, GameWindow gameWindow) {
        this.scorePanel = scorePanel;
        this.gameWindow = gameWindow;

        setPreferredSize(new Dimension(GRID_SIZE * PIXEL_SIZE, GRID_SIZE * PIXEL_SIZE));
        setBackground(new Color(30, 30, 30));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!igraAktivna) return;

                int col = e.getX() / PIXEL_SIZE;
                int row = e.getY() / PIXEL_SIZE;

                if (col >= 0 && col < GRID_SIZE && row >= 0 && row < GRID_SIZE) {
                    // Šaljemo klik mrežnom handleru umjesto lokalnog bojenja
                    if (gameWindow.getNetworkHandler() != null) {
                        gameWindow.getNetworkHandler().posaljiKlik(row, col);
                    }
                }
            }
        });
    }

    public void updateGrid(int[][] noviGrid) {
        this.gridData = noviGrid;
        repaint();
    }

    public void završiIgru() {
        this.igraAktivna = false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                if (gridData[r][c] == 1) {
                    g2d.setColor(Color.RED);
                } else if (gridData[r][c] == 2) {
                    g2d.setColor(Color.BLUE);
                } else {
                    g2d.setColor(new Color(60, 60, 60));
                }
                g2d.fillRect(c * PIXEL_SIZE, r * PIXEL_SIZE, PIXEL_SIZE - 1, PIXEL_SIZE - 1);
            }
        }
    }
}