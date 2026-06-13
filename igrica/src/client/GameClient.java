package client;

import javax.swing.SwingUtilities;

public class GameClient {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Pokrećemo mrežnog rukovaoca koji će sam otvoriti prozor nakon prijave
            new NetworkHandler("127.0.0.1", 8888);
        });
    }
}