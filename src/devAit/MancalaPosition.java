package devAit;

import java.io.*;
import java.util.Arrays;

public class MancalaPosition implements Serializable {
    private static final long serialVersionUID = 1L;
    private int[] board; 
    private int currentPlayer; 

    public MancalaPosition() {
        board = new int[14]; 
        Arrays.fill(board, 4); 
        board[6] = 0; 
        board[13] = 0; 
        currentPlayer = 0;
    }

    public int[] getBoard() {
        return board;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void switchPlayer() {
        currentPlayer = 1 - currentPlayer;
    }

    public boolean isGameOver() {
        return (Arrays.stream(board, 0, 6).sum() == 0 || Arrays.stream(board, 7, 13).sum() == 0);
    }

    public void makeMove(MancalaMove move) {
        int stones = board[move.getPit()];
        board[move.getPit()] = 0;
        int index = move.getPit();

        while (stones > 0) {
            index = (index + 1) % 14;
            if (currentPlayer == 0 && index == 13) continue;
            if (currentPlayer == 1 && index == 6) continue;
            board[index]++;
            stones--;
        }

        if (currentPlayer == 0 && index < 6 && board[index] == 1 && board[12 - index] > 0) {
            board[6] += board[12 - index] + 1;
            board[index] = 0;
            board[12 - index] = 0;
        } else if (currentPlayer == 1 && index > 6 && index < 13 && board[index] == 1 && board[12 - index] > 0) {
            board[13] += board[12 - index] + 1;
            board[index] = 0;
            board[12 - index] = 0;
        }

        if ((currentPlayer == 0 && index == 6) || (currentPlayer == 1 && index == 13)) {
            return;
        }

        switchPlayer();
    }

    public int getScore(int player) {
        return board[player == 0 ? 6 : 13];
    }

    // AI move selection using a basic heuristic
    public MancalaMove aiMove() {
        int bestPit = -1;
        int bestScore = Integer.MIN_VALUE;

        // Heuristic: Select the pit that maximizes the player's score
        for (int i = (currentPlayer == 0 ? 0 : 7); i <= (currentPlayer == 0 ? 5 : 12); i++) {
            if (board[i] > 0) {
                int score = evaluateMove(i);
                if (score > bestScore) {
                    bestScore = score;
                    bestPit = i;
                }
            }
        }

        return new MancalaMove(bestPit);
    }

    // Simple evaluation heuristic: just the number of stones in the pit
    private int evaluateMove(int pit) {
        return board[pit];
    }

    // Save the game state to a file
    public static void saveGame(MancalaPosition position, String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(position);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load the game state from a file
    public static MancalaPosition loadGame(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (MancalaPosition) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
