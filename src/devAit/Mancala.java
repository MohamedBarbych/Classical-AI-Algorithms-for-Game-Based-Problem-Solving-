package devAit;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Mancala extends JFrame {
    private final JButton[] pitButtons = new JButton[14];
    private final JLabel statusLabel = new JLabel("Player 1's turn");
    private final MancalaPosition position = new MancalaPosition();

    public Mancala() {
        setTitle("Mancala Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Create the game board
        JPanel boardPanel = new JPanel(new GridLayout(2, 7));
        for (int i = 0; i < 14; i++) {
            JButton button = new JButton(i == 6 || i == 13 ? "0" : "4");
            button.setEnabled(i != 6 && i != 13); // Disable stores
            final int pit = i;
            button.addActionListener(e -> handlePitClick(pit));
            pitButtons[i] = button;
            if (i == 6 || i == 13) {
                button.setBackground(Color.YELLOW); // Highlight stores
            }
            boardPanel.add(button);
        }

        // Add components to the frame
        add(statusLabel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void handlePitClick(int pit) {
        // Check if it's a valid move
        if (position.getBoard()[pit] == 0 || (position.getCurrentPlayer() == 0 && pit > 5)
                || (position.getCurrentPlayer() == 1 && (pit < 7 || pit == 13))) {
            JOptionPane.showMessageDialog(this, "Invalid move. Try again.");
            return;
        }

        // Human move
        MancalaMove move = new MancalaMove(pit);
        position.makeMove(move);
        updateBoard();

        // Check if the game is over
        if (checkGameOver()) return;

        // AI turn
        if (position.getCurrentPlayer() == 1) {
            playAITurn();
        }
    }

    private void playAITurn() {
        // Simple AI: Choose a random valid move
        int[] board = position.getBoard();
        int aiMove = -1;
        for (int i = 7; i <= 12; i++) {
            if (board[i] > 0) {
                aiMove = i; // Pick the first valid move for simplicity
                break;
            }
        }

        if (aiMove != -1) {
            MancalaMove move = new MancalaMove(aiMove);
            position.makeMove(move);
            updateBoard();

            // Check if the game is over
            if (checkGameOver()) return;

            // Update the status label
            statusLabel.setText("Player 1's turn");
        }
    }

    private boolean checkGameOver() {
        if (position.isGameOver()) {
            int player1Score = position.getScore(0);
            int player2Score = position.getScore(1);
            String winner = player1Score > player2Score ? "Player 1" :
                    (player1Score < player2Score ? "Player 2" : "No one");
            JOptionPane.showMessageDialog(this,
                    "Game Over! Winner: " + winner + "\nScores:\nPlayer 1: " + player1Score + "\nPlayer 2: " + player2Score);
            System.exit(0);
            return true;
        }
        return false;
    }

    private void updateBoard() {
        int[] board = position.getBoard();
        for (int i = 0; i < 14; i++) {
            pitButtons[i].setText(String.valueOf(board[i]));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Mancala::new);
    }
}
