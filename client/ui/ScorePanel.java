package client.ui;

import javax.swing.*;
import java.awt.*;

public class ScorePanel extends JPanel {

    private JLabel labelIme1;
    private JLabel labelSkor1;
    private JLabel labelIme2;
    private JLabel labelSkor2;

    private int mojSkor = 0;
    private int kolegaSkor = 0;

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
        labelIme1 = new JLabel("Čeka se...");
        labelIme1.setForeground(Color.WHITE);
        labelSkor1 = new JLabel("0");
        labelSkor1.setForeground(Color.WHITE);

        JPanel levo1 = new JPanel(new BorderLayout());
        levo1.setOpaque(false);
        levo1.add(kvadrat1, BorderLayout.WEST);
        levo1.add(labelIme1, BorderLayout.CENTER);
        red1.add(levo1, BorderLayout.WEST);
        red1.add(labelSkor1, BorderLayout.EAST);
        add(red1);

        add(Box.createRigidArea(new Dimension(0, 10)));

        // Igrač 2 (Plavi)
        JPanel red2 = new JPanel(new BorderLayout());
        red2.setOpaque(false);
        red2.setMaximumSize(new Dimension(180, 25));
        JLabel kvadrat2 = new JLabel("■ ");
        kvadrat2.setForeground(Color.BLUE);
        labelIme2 = new JLabel("Čeka se...");
        labelIme2.setForeground(Color.WHITE);
        labelSkor2 = new JLabel("0");
        labelSkor2.setForeground(Color.WHITE);

        JPanel levo2 = new JPanel(new BorderLayout());
        levo2.setOpaque(false);
        levo2.add(kvadrat2, BorderLayout.WEST);
        levo2.add(labelIme2, BorderLayout.CENTER);
        red2.add(levo2, BorderLayout.WEST);
        red2.add(labelSkor2, BorderLayout.EAST);
        add(red2);
    }

    public void azurirajSkorove(int skor1, int skor2) {
        this.mojSkor = skor1;
        this.kolegaSkor = skor2;
        this.labelSkor1.setText(String.valueOf(skor1));
        this.labelSkor2.setText(String.valueOf(skor2));
    }

    public int getMojSkor() {
        return this.mojSkor;
    }

    public int getKolegaSkor() {
        return this.kolegaSkor;
    }

    public void azurirajImena(String ime1, String ime2) {
        labelIme1.setText(ime1);
        labelIme2.setText(ime2);
    }
}