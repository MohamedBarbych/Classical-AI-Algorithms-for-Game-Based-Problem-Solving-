package Mancala;

import java.util.Vector;

/**
 * Classe abstraite pour gérer les algorithmes de recherche dans les jeux.
 * Fournit une base pour implémenter les algorithmes de recherche adversarielle comme Minimax et Alpha-Beta.
 */
public abstract class GameSearch {
    // Constantes pour débogage et identification des joueurs
    public static final boolean DEBUG = false; // Activer/désactiver les logs de débogage
    public static final boolean PROGRAM = true; // Représente l'IA
    public static final boolean HUMAN = false; // Représente le joueur humain

    // Configuration de l'algorithme
    private int complexity = 3; // Profondeur maximale par défaut (niveau de difficulté de l'IA)
    private String strategy = "offensive"; // Stratégie par défaut ("offensive" ou "defensive")

    // Méthodes abstraites que les sous-classes doivent implémenter
    public abstract boolean drawnPosition(Position p);

    public abstract boolean wonPosition(Position p, boolean player);

    public abstract float positionEvaluation(Position p, boolean player);

    public abstract Position[] possibleMoves(Position p, boolean player);

    public abstract Position makeMove(Position p, boolean player, Move move);

    public abstract boolean reachedMaxDepth(Position p, int depth);

    /**
     * Méthode principale pour effectuer une recherche alpha-beta.
     * @param depth Profondeur actuelle dans l'arbre de recherche.
     * @param p Position actuelle.
     * @param player Joueur en cours (IA ou humain).
     * @return Un vecteur contenant le score et le meilleur mouvement.
     */
    protected Vector alphaBeta(int depth, Position p, boolean player) {
        return alphaBetaHelper(depth, p, player, -1000000.0f, 1000000.0f);
    }

    /**
     * Méthode d'assistance pour alpha-beta avec les coupures.
     * Implémente l'algorithme optimisé vu en cours (diapositive 18).
     * @param depth Profondeur actuelle.
     * @param p Position actuelle.
     * @param player Joueur en cours.
     * @param alpha Valeur alpha pour la coupure.
     * @param beta Valeur beta pour la coupure.
     * @return Un vecteur contenant le score et le meilleur mouvement.
     */
    protected Vector alphaBetaHelper(int depth, Position p, boolean player, float alpha, float beta) {
        if (reachedMaxDepth(p, depth) || drawnPosition(p) || wonPosition(p, !player)) {
            Vector v = new Vector(2);
            v.addElement(positionEvaluation(p, player));
            v.addElement(null); // Aucun mouvement à retourner
            return v;
        }

        Position[] moves = possibleMoves(p, player); // Récupération des mouvements possibles
        Vector best = new Vector(2); // Contiendra le meilleur score et le mouvement associé
        float value;

        if (player == PROGRAM) { // Maximiser pour l'IA
            value = -Float.MAX_VALUE;
            for (Position move : moves) {
                Vector v = alphaBetaHelper(depth + 1, move, HUMAN, alpha, beta);
                float newValue = (Float) v.elementAt(0);
                if (newValue > value) {
                    value = newValue;
                    best.clear();
                    best.add(move);
                }
                alpha = Math.max(alpha, value);
                if (alpha >= beta) break; // Coupure beta
            }
        } else { // Minimiser pour le joueur humain
            value = Float.MAX_VALUE;
            for (Position move : moves) {
                Vector v = alphaBetaHelper(depth + 1, move, PROGRAM, alpha, beta);
                float newValue = (Float) v.elementAt(0);
                if (newValue < value) {
                    value = newValue;
                    best.clear();
                    best.add(move);
                }
                beta = Math.min(beta, value);
                if (beta <= alpha) break; // Coupure alpha
            }
        }

        // Insérer le score au début du vecteur
        best.insertElementAt(value, 0);
        return best;
    }

    // Méthodes pour configurer la complexité et la stratégie de l'algorithme
    public void setComplexity(int complexity) {
        this.complexity = complexity;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy.toLowerCase();
    }

    public int getComplexity() {
        return complexity;
    }

    public String getStrategy() {
        return strategy;
    }
}
