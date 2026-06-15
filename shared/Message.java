package shared;

import java.io.Serializable;
import java.util.Map;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    // Tipovi poruka koje klijent i server šalju jedan drugom
    public enum Type {
        JOIN,       // Klijent šalje svoje ime čim se poveže
        CLICK,          // Klijent šalje koordinate kada klikne na kvadrat
        UPDATE_GAME,    // Server šalje svima osveženu matricu i skorove,
        TIMER,          // Server salje preostalo vreme svake sekunde
        GAME_OVER       // Server salje kad istekne vreme
    }

    private Type type;
    private String name;
    private int row;
    private int col;
    private int[][] grid;
    private Map<String, Integer> scores;
    // Konstruktor za prijavu igrača (šalje se samo ime)
    public Message(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    // Konstruktor za klik na piksel (šalje se gde je kliknuto)
    public Message(Type type, int row, int col) {
        this.type = type;
        this.row = row;
        this.col = col;
    }
    // Konstruktor kojim server šalje osveženo stanje cele igre svima
    public Message(Type type, int[][] boardCopy, Map<String, Integer> scores) {
        this.type = type;
        this.grid = boardCopy;
        this.scores = scores;
    }
    // Getteri da bismo mogli da pročitamo podatke iz poruke
    public Type getType() { return type; }
    public String getName() { return name; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public int[][] getGrid() { return grid; }
    public Map<String, Integer> getScores(){return scores;}
}
