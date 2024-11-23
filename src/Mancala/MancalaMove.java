package Mancala;

public class MancalaMove extends Move {
    private int pit;

    public MancalaMove(int pit) {
        this.pit = pit;
    }

    public int getPit() {
        return pit;
    }
}
