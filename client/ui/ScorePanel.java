package client.ui;

import javax.swing.*;
import java.awt.*;

public class ScorePanel extends JPanel {

    private JLabel lableName1;
    private JLabel labelScore1;
    private JLabel lableName2;
    private JLabel labelScore2;

    private int myScore = 0;
    private int opponentScore = 0;

    public ScorePanel() {
        setBackground(new Color(45, 45, 45));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Skorovi");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(titleLabel);
        add(Box.createRigidArea(new Dimension(0, 20)));

        // Igrač 1 (Crveni)
        JPanel red1 = new JPanel(new BorderLayout());
        red1.setOpaque(false);
        red1.setMaximumSize(new Dimension(180, 25));
        JLabel kvadrat1 = new JLabel("■ ");
        kvadrat1.setForeground(Color.RED);
        lableName1 = new JLabel("Čeka se...");
        lableName1.setForeground(Color.WHITE);
        labelScore1 = new JLabel("0");
        labelScore1.setForeground(Color.WHITE);

        JPanel levo1 = new JPanel(new BorderLayout());
        levo1.setOpaque(false);
        levo1.add(kvadrat1, BorderLayout.WEST);
        levo1.add(lableName1, BorderLayout.CENTER);
        red1.add(levo1, BorderLayout.WEST);
        red1.add(labelScore1, BorderLayout.EAST);
        add(red1);

        add(Box.createRigidArea(new Dimension(0, 10)));

        // Igrač 2 (Plavi)
        JPanel red2 = new JPanel(new BorderLayout());
        red2.setOpaque(false);
        red2.setMaximumSize(new Dimension(180, 25));
        JLabel kvadrat2 = new JLabel("■ ");
        kvadrat2.setForeground(Color.BLUE);
        lableName2 = new JLabel("Čeka se...");
        lableName2.setForeground(Color.WHITE);
        labelScore2 = new JLabel("0");
        labelScore2.setForeground(Color.WHITE);

        JPanel levo2 = new JPanel(new BorderLayout());
        levo2.setOpaque(false);
        levo2.add(kvadrat2, BorderLayout.WEST);
        levo2.add(lableName2, BorderLayout.CENTER);
        red2.add(levo2, BorderLayout.WEST);
        red2.add(labelScore2, BorderLayout.EAST);
        add(red2);
    }

    public void updateScores(int skor1, int skor2) {
        this.myScore = skor1;
        this.opponentScore = skor2;
        this.labelScore1.setText(String.valueOf(skor1));
        this.labelScore2.setText(String.valueOf(skor2));
    }

    public int getMyScore() {
        return this.myScore;
    }

    public int getOpponentScore() {
        return this.opponentScore;
    }

    public void updateNames(String ime1, String ime2) {
        lableName1.setText(ime1);
        lableName2.setText(ime2);
    }
}