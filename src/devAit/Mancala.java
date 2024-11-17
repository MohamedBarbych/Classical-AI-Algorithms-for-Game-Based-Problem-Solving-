package devAit;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class Mancala extends JFrame {
    private final MancalaPosition position;
    private JLabel player1ScoreLabel, player2ScoreLabel, currentPlayerLabel;
    private JButton[] pits;

    public Mancala() {
        position = new MancalaPosition();

        // Set up the main frame
        setTitle("Mancala - Jeu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLayout(new BorderLayout());
        setContentPane(createBackgroundPanel("/home/med/MyProjects/Classical-AI-Algorithms-for-Game-Based-Problem-Solving-/src/images/mancala_Back.jpeg")); // Set the background

        // Add the welcome and game panels
        JPanel welcomePanel = createWelcomePanel();
        JPanel gamePanel = createGamePanel();
        gamePanel.setVisible(false);

        add(welcomePanel, BorderLayout.CENTER);
        add(gamePanel, BorderLayout.SOUTH);

        // Start button action listener
        JButton startButton = (JButton) ((BorderLayout) welcomePanel.getLayout()).getLayoutComponent(BorderLayout.SOUTH);
        startButton.addActionListener(e -> {
            welcomePanel.setVisible(false);
            gamePanel.setVisible(true);
        });

        setVisible(true);
    }

    private JPanel createBackgroundPanel(String imagePath) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image background = new ImageIcon(imagePath).getImage();
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }
        };
    }

    private JPanel createWelcomePanel() {
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Bienvenue dans Mancala !", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Serif", Font.BOLD, 32));
        welcomeLabel.setForeground(Color.WHITE);

        JButton startButton = new JButton("Commencer le jeu");
        startButton.setFont(new Font("Serif", Font.PLAIN, 20));

        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);
        welcomePanel.add(startButton, BorderLayout.SOUTH);
        return welcomePanel;
    }

    private JPanel createGamePanel() {
        JPanel gamePanel = new JPanel(new BorderLayout());
        gamePanel.setOpaque(false);

        // Player info panel
        JPanel infoPanel = new JPanel(new GridLayout(1, 3));
        infoPanel.setOpaque(false);
        player1ScoreLabel = new JLabel("Score Joueur 1: 0", SwingConstants.CENTER);
        player2ScoreLabel = new JLabel("Score Joueur 2: 0", SwingConstants.CENTER);
        currentPlayerLabel = new JLabel("Tour du joueur 1", SwingConstants.CENTER);

        styleLabel(player1ScoreLabel);
        styleLabel(player2ScoreLabel);
        styleLabel(currentPlayerLabel);

        infoPanel.add(player1ScoreLabel);
        infoPanel.add(currentPlayerLabel);
        infoPanel.add(player2ScoreLabel);

        // Board panel
        JPanel boardPanel = new JPanel(new GridLayout(2, 6));
        boardPanel.setOpaque(false);
        pits = new JButton[12];
        for (int i = 0; i < pits.length; i++) {
            pits[i] = new JButton("4");
            pits[i].setFont(new Font("SansSerif", Font.BOLD, 16));
            pits[i].setEnabled(i < 6); // Only enable player pits initially
            int pitIndex = i;
            pits[i].addActionListener(e -> handlePlayerMove(pitIndex));
            boardPanel.add(pits[i]);
        }

        gamePanel.add(infoPanel, BorderLayout.NORTH);
        gamePanel.add(boardPanel, BorderLayout.CENTER);

        return gamePanel;
    }

    private void styleLabel(JLabel label) {
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.WHITE);
    }

    private void handlePlayerMove(int pitIndex) {
        if (position.getCurrentPlayer() == 0 && pitIndex >= 0 && pitIndex <= 5 && position.getBoard()[pitIndex] > 0) {
            position.makeMove(new MancalaMove(pitIndex));
            refreshBoard();

            if (!position.isGameOver()) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handleAIMove();
                    }
                }, 1000);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Coup invalide. Essayez encore.");
        }
    }

    private void handleAIMove() {
        if (position.getCurrentPlayer() == 1 && !position.isGameOver()) {
            position.makeMove(position.aiMove());
            refreshBoard();
        }
    }

    private void refreshBoard() {
        int[] board = position.getBoard();
        for (int i = 0; i < pits.length; i++) {
            pits[i].setText(String.valueOf(board[i]));
        }

        // Update player scores
        player1ScoreLabel.setText("Score Joueur 1: " + position.getScore(0));
        player2ScoreLabel.setText("Score Joueur 2: " + position.getScore(1));
        currentPlayerLabel.setText("Tour du joueur " + (position.getCurrentPlayer() + 1));

        if (position.isGameOver()) {
            JOptionPane.showMessageDialog(this, "Fin du jeu !\n" +
                    "Score Joueur 1: " + position.getScore(0) + "\n" +
                    "Score Joueur 2: " + position.getScore(1));
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Mancala::new);
    }
}
