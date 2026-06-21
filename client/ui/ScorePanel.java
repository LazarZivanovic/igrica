package client.ui;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScorePanel extends JPanel {

    private final Map<String, JLabel> scoreLabels = new LinkedHashMap<>();
    private final Map<String, JLabel> colorSquares = new LinkedHashMap<>();
    private final Map<String, Integer> lastColorNumber = new LinkedHashMap<>();
    private final Map<String, JPanel> playerRows = new ConcurrentHashMap<>();
    private static final Color[] COLORS = {
            Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
            Color.ORANGE, Color.MAGENTA, Color.CYAN, Color.PINK
    };
    public ScorePanel() {
        setBackground(new Color(45, 45, 45));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Scores");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(titleLabel);
        add(Box.createRigidArea(new Dimension(0, 20)));
    }

    public void addPlayer(String name, int colorNumber) {
        if (scoreLabels.containsKey(name)) {
            updatePlayerColor(name, colorNumber);
            return;
        }
        Color color = COLORS[(colorNumber - 1 + COLORS.length) % COLORS.length];

        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(180, 25));

        JLabel colorSquare = new JLabel("■ ");
        colorSquare.setForeground(color);
        JLabel nameLabel = new JLabel(name);
        nameLabel.setForeground(Color.WHITE);
        JLabel scoreLabel = new JLabel("0");
        scoreLabel.setForeground(Color.WHITE);
        scoreLabels.put(name, scoreLabel);
        colorSquares.put(name, colorSquare);
        lastColorNumber.put(name, colorNumber);

        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        left.add(colorSquare, BorderLayout.WEST);
        left.add(nameLabel, BorderLayout.CENTER);
        row.add(left, BorderLayout.WEST);
        row.add(scoreLabel, BorderLayout.EAST);
        add(row);
        playerRows.put(name, row);
        add(Box.createRigidArea(new Dimension(0, 10)));
        revalidate();
        repaint();
    }
    private void updatePlayerColor(String name, int colorNumber) {
        Integer previous = lastColorNumber.get(name);
        if (previous != null && previous == colorNumber) return;
        JLabel square = colorSquares.get(name);
        if (square != null) {
            square.setForeground(COLORS[(colorNumber - 1 + COLORS.length) % COLORS.length]);
        }
        lastColorNumber.put(name, colorNumber);
    }

    public void updateScores(Map<String, Integer> scores, Map<String, Integer> playerColors) {
        SwingUtilities.invokeLater(() -> {
            scores.forEach((name, score) -> {
                int colorNumber = playerColors != null ? playerColors.getOrDefault(name, 1) : 1;
                if (!scoreLabels.containsKey(name)) {
                    addPlayer(name, colorNumber);
                } else {
                    updatePlayerColor(name, colorNumber);
                }
                scoreLabels.get(name).setText(String.valueOf(score));
            });
        });
    }
    public void removePlayer(String name) {
        SwingUtilities.invokeLater(() -> {
            JPanel row = playerRows.remove(name);
            if (row != null) {
                this.remove(row);
                scoreLabels.remove(name);
                colorSquares.remove(name);
                lastColorNumber.remove(name);
                revalidate();
                repaint();
            }
        });
    }
    public void resetScores() {
        scoreLabels.values().forEach(label -> label.setText("0"));
    }
    public int getMyScore() { return 0; }
    public int getOpponentScore() { return 0; }
}