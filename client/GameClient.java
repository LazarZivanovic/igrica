package client;
import client.ui.GameWindow;
import shared.Config;
import javax.swing.*;

public class GameClient {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Pokrećemo mrežnog rukovaoca koji će sam otvoriti prozor nakon prijave
            new NetworkHandler(Config.HOST,Config.PORT);
        });
    }
}