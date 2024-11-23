package Mancala;

import java.io.*;
import java.util.Arrays;

public class MancalaPosition extends Position implements Serializable,Cloneable {
    private static final long serialVersionUID = 1L;
    private int[] board;
    private int currentPlayer;
    private int helpRequestsLeft;
    private int complexity;
    private String strategy;
    private String heuristic;

    public MancalaPosition() {
        board = new int[14];
        Arrays.fill(board, 4);
        board[6] = 0; // Grande case joueur 1
        board[13] = 0; // Grande case joueur 2
        currentPlayer = 0; // Joueur 1 commence
        helpRequestsLeft = 3; // Limite d'aides
        complexity = 3; // Complexité par défaut
        strategy = "offensive"; // Stratégie par défaut
        heuristic = "basic"; // Heuristique par défaut
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

    public void makeMove(Move move) {
        MancalaMove mancalaMove = (MancalaMove) move;
        int pit = mancalaMove.getPit();
        int stones = board[pit];
        board[pit] = 0;
        int index = pit;

        while (stones > 0) {
            index = (index + 1) % 14;
            if (currentPlayer == 0 && index == 13) continue; // Joueur 1 ne dépose pas dans la grande case du joueur 2
            if (currentPlayer == 1 && index == 6) continue;  // Joueur 2 ne dépose pas dans la grande case du joueur 1
            board[index]++;
            stones--;
        }

        // Capture si la dernière graine est dans une case vide
        if (currentPlayer == 0 && index < 6 && board[index] == 1 && board[12 - index] > 0) {
            board[6] += board[12 - index] + 1;
            board[index] = 0;
            board[12 - index] = 0;
        } else if (currentPlayer == 1 && index > 6 && index < 13 && board[index] == 1 && board[12 - index] > 0) {
            board[13] += board[12 - index] + 1;
            board[index] = 0;
            board[12 - index] = 0;
        }

        // Si la dernière graine tombe dans la grande case, le joueur rejoue
        if ((currentPlayer == 0 && index == 6) || (currentPlayer == 1 && index == 13)) {
            return;
        }

        switchPlayer();
    }

    public int getScore(int player) {
        return board[player == 0 ? 6 : 13];
    }
    public boolean isValidMove(Move move) {
        MancalaMove mancalaMove = (MancalaMove) move;
        int pit = mancalaMove.getPit();

        // Vérification : le joueur actuel doit jouer sur son côté du plateau
        if (currentPlayer == 0 && (pit < 0 || pit > 5)) return false;
        if (currentPlayer == 1 && (pit < 7 || pit > 12)) return false;

        // Vérification : la case sélectionnée ne doit pas être vide
        return board[pit] > 0;
    }




    public float evaluatePosition() {
        int playerStore = board[currentPlayer == 0 ? 6 : 13];
        int opponentStore = board[currentPlayer == 0 ? 13 : 6];
        int stonesOnBoard = Arrays.stream(board).sum();

        if ("offensive".equalsIgnoreCase(strategy)) {
            return playerStore - opponentStore + (heuristic.equalsIgnoreCase("advanced") ? stonesOnBoard / 4.0f : 0);
        } else if ("defensive".equalsIgnoreCase(strategy)) {
            return playerStore - opponentStore - (heuristic.equalsIgnoreCase("advanced") ? stonesOnBoard / 4.0f : 0);
        }
        return playerStore - opponentStore; // Stratégie par défaut
    }



    @Override
    public Object clone() {
        try {
            MancalaPosition cloned = (MancalaPosition) super.clone();
            cloned.board = this.board.clone(); // Cloner le tableau pour éviter les références partagées
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported", e); // Ne devrait pas arriver si Cloneable est implémenté
        }
    }


    public MancalaMove aiMove() {
        MancalaMove bestMove = null;
        float bestScore = -Float.MAX_VALUE;

        for (int i = 0; i < 6; i++) {
            int pit = currentPlayer == 0 ? i : i + 7;
            if (board[pit] > 0) {
                MancalaMove move = new MancalaMove(pit);
                MancalaPosition newPosition = (MancalaPosition) this.clone(); // Utiliser le clone corrigé
                newPosition.makeMove(move);
                float score = newPosition.evaluatePosition();
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
            }
        }
        return bestMove;
    }


    public int getHelpRequestsLeft() {
        return helpRequestsLeft;
    }

    public void decrementHelpRequestsLeft() {
        if (helpRequestsLeft > 0) helpRequestsLeft--;
    }

    public void setComplexity(int complexity) {
        this.complexity = complexity;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public void setHeuristic(String heuristic) {
        this.heuristic = heuristic;
    }

    public static void saveGame(MancalaPosition position, String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(position);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MancalaPosition loadGame(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (MancalaPosition) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
