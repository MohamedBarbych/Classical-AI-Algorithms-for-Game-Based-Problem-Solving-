package Mancala;

import java.io.Serializable;

public abstract class Position implements Serializable {
    private static final long serialVersionUID = 1L;

    public abstract boolean isGameOver();

    public abstract void makeMove(Move move);
}
