package devAit;

import java.io.*;
import java.util.Arrays;

public class MancalaPosition implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int[] board; // 14 pits: 0-5 (player 1), 6 (store), 7-12 (player 2), 13 (store)
    private int currentPlayer; // 0: Player 1, 1: Player 2

    public MancalaPosition() {
        board = new int[14];
        Arrays.fill(board, 4); // Each pit starts with 4 stones
        board[6] = 0; // Player 1's store
        board[13] = 0; // Player 2's store
        currentPlayer = 0; // Player 1 starts
    }

    public int[] getBoard() {
        return board.clone(); // Return a copy to preserve encapsulation
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void switchPlayer() {
        currentPlayer = 1 - currentPlayer;
    }

    public boolean isGameOver() {
        // Game over if either side's pits are empty
        return Arrays.stream(board, 0, 6).sum() == 0 || Arrays.stream(board, 7, 13).sum() == 0;
    }

    public void makeMove(MancalaMove move) {
        int pit = move.getPit();

        if (pit < 0 || pit >= 14 || board[pit] == 0 || // Invalid move checks
                (currentPlayer == 0 && pit >= 6) || (currentPlayer == 1 && pit <= 6)) {
            throw new IllegalArgumentException("Invalid move for the current player.");
        }

        int stones = board[pit];
        board[pit] = 0; // Pick up all stones from the selected pit
        int index = pit;

        // Distribute stones
        while (stones > 0) {
            index = (index + 1) % 14;

            // Skip opponent's store
            if ((currentPlayer == 0 && index == 13) || (currentPlayer == 1 && index == 6)) {
                continue;
            }

            board[index]++;
            stones--;
        }

        // Check if the last stone landed in the current player's empty pit
        if (currentPlayer == 0 && index < 6 && board[index] == 1 && board[12 - index] > 0) {
            board[6] += board[12 - index] + 1; // Capture stones
            board[index] = 0;
            board[12 - index] = 0;
        } else if (currentPlayer == 1 && index > 6 && index < 13 && board[index] == 1 && board[12 - index] > 0) {
            board[13] += board[12 - index] + 1; // Capture stones
            board[index] = 0;
            board[12 - index] = 0;
        }

        // If the last stone lands in the player's store, they get another turn
        if ((currentPlayer == 0 && index == 6) || (currentPlayer == 1 && index == 13)) {
            return; // Do not switch the player
        }

        switchPlayer();
    }

    public int getScore(int player) {
        return board[player == 0 ? 6 : 13];
    }

    public MancalaMove aiMove() {
        int bestPit = -1;
        int bestScore = Integer.MIN_VALUE;

        for (int i = (currentPlayer == 0 ? 0 : 7); i <= (currentPlayer == 0 ? 5 : 12); i++) {
            if (board[i] > 0) {
                MancalaPosition simulated = simulateMove(i);
                int score = simulated.getScore(currentPlayer);
                if (score > bestScore) {
                    bestScore = score;
                    bestPit = i;
                }
            }
        }

        return new MancalaMove(bestPit);
    }

    private MancalaPosition simulateMove(int pit) {
        MancalaPosition clone = clonePosition();
        clone.makeMove(new MancalaMove(pit));
        return clone;
    }

    private MancalaPosition clonePosition() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            oos.flush();
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            return (MancalaPosition) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error cloning MancalaPosition", e);
        }
    }
}
