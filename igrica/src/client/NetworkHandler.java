package client;

import client.ui.GameWindow;
import shared.Message;

import java.io.*;
import java.net.Socket;
import javax.swing.JOptionPane;

public class NetworkHandler {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private GameWindow gameWindow;
    private String mojeIme;

    public NetworkHandler(String host, int port) {
        try {
            // 1. Iskače prozor za ime čim se pokrene klijent
            mojeIme = JOptionPane.showInputDialog(null, "Unesi svoje ime:", "Prijava na server", JOptionPane.QUESTION_MESSAGE);
            if (mojeIme == null || mojeIme.trim().isEmpty()) {
                mojeIme = "Igrač_" + (int)(Math.random() * 1000);
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
            posaljiPoruku(new Message(Message.Type.PRIJAVA, mojeIme));

            // 5. Pokrećemo pozadinsku nit da sluša server
            new Thread(this::listenToServer).start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ne mogu da se povežem na server! Proveri da li je server pokrenut.", "Greška", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
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
                    gameWindow.getScorePanel().azurirajSkorove(msg.getSkor1(), msg.getSkor2());
                }
            }
        } catch (Exception e) {
            System.out.println("[KLIJENT] Veza sa serverom je prekinuta.");
        } finally {
            zatvoriSve();
        }
    }

    public void posaljiKlik(int row, int col) {
        Message klikPoruka = new Message(Message.Type.KLIK, row, col);
        posaljiPoruku(klikPoruka);
    }

    private synchronized void posaljiPoruku(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            System.err.println("[KLIJENT] Greška pri slanju: " + e.getMessage());
        }
    }

    private void zatvoriSve() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMojeIme() {
        return mojeIme;
    }
}
