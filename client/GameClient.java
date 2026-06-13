package client;
import client.ui.GameWindow;
import client.ui.PixelCanvas;

import javax.swing.*;

public class GameClient {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Pokrećemo mrežnog rukovaoca koji će sam otvoriti prozor nakon prijave
            new GameWindow(null);
        });
    }
}