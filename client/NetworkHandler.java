package client;
import shared.Message;
import client.ui.GameWindow;
import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkHandler {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private GameWindow gameWindow;
    private String myName;

    public NetworkHandler(String host, int port) {
        try {
            // 1. Iskače prozor za ime čim se pokrene klijent
            myName = JOptionPane.showInputDialog(null, "Unesi svoje ime:", "Prijava na server", JOptionPane.QUESTION_MESSAGE);
            if (myName == null)return;
            if (myName.trim().isEmpty()) {
                myName = "Igrač_" + (int)(Math.random() * 1000);
            }

            // 2. Spajanje na socket
            System.out.println("[KLIJENT] Povezivanje na server " + host + ":" + port + "...");
            socket = new Socket(host, port);

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("[KLIJENT] Uspešno povezivanje! Otvaram prozor igre...");

            // 3. Otvaramo prozor igre i prosleđujemo mu ovaj mrežni handler
            gameWindow = new GameWindow(this);

            // 4. Šaljemo ime serveru
            sendMessage(new Message(Message.Type.JOIN, myName));

            // 5. Pokrećemo pozadinsku nit da sluša server
            new Thread(this::listenToServer).start();

        } catch (IOException e) {
            int choice = JOptionPane.showConfirmDialog(null,
                    "Server is not available. Try again?",
                    "Connection Error",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE);
            System.out.println("[CLIENT] Can't connect to server");
            if (choice == JOptionPane.YES_OPTION) {
                new NetworkHandler(host, port);
            }
        }
    }

    private void listenToServer() {
        try {
            while (true) {
                Message msg = (Message) in.readObject();

                if (msg.getType() == Message.Type.UPDATE_GAME) {
                    // Osvežavamo matricu piksela
                    gameWindow.getPixelCanvas().updateGrid(msg.getGrid());

                    // Osvežavamo tabelu sa skorovima
                    gameWindow.getScorePanel().updateScores(msg.getScore1(), msg.getScore2());
                }
            }
        } catch (Exception e) {
            System.out.println("[KLIJENT] Veza sa serverom je prekinuta.");
        } finally {
            closeAll();
        }
    }

    public void sendClick(int row, int col) {
        Message clickMessage = new Message(Message.Type.CLICK, row, col);
        sendMessage(clickMessage);
    }

    private synchronized void sendMessage(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            System.err.println("[KLIJENT] Greška pri slanju: " + e.getMessage());
        }
    }

    private void closeAll() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMyName() {
        return myName;
    }
}
