package shared;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    // Tipovi poruka koje klijent i server šalju jedan drugom
    public enum Type {
        PRIJAVA,       // Klijent šalje svoje ime čim se poveže
        KLIK,          // Klijent šalje koordinate kada klikne na kvadrat
        UPDATE_GAME    // Server šalje svima osveženu matricu i skorove
    }

    private Type type;
    private String ime;
    private int row;
    private int col;
    private int[][] grid;
    private int skor1;
    private int skor2;

    // Konstruktor za prijavu igrača (šalje se samo ime)
    public Message(Type type, String ime) {
        this.type = type;
        this.ime = ime;
    }

    // Konstruktor za klik na piksel (šalje se gde je kliknuto)
    public Message(Type type, int row, int col) {
        this.type = type;
        this.row = row;
        this.col = col;
    }

    // Konstruktor kojim server šalje osveženo stanje cele igre svima
    public Message(Type type, int[][] grid, int skor1, int skor2) {
        this.type = type;
        this.grid = grid;
        this.skor1 = skor1;
        this.skor2 = skor2;
    }

    // Getteri da bismo mogli da pročitamo podatke iz poruke
    public Type getType() { return type; }
    public String getIme() { return ime; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public int[][] getGrid() { return grid; }
    public int getSkor1() { return skor1; }
    public int getSkor2() { return skor2; }
}
