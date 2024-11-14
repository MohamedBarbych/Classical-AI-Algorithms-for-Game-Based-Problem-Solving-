package Maze;

import java.awt.Dimension;
import java.util.*;

public class AStarSearchEngine extends AbstractSearchEngine {
    public AStarSearchEngine(int width, int height) {
        super(width, height); // Appel du constructeur de la classe parente avec les dimensions du labyrinthe.
    }

    public void search() {
        // La liste des nœuds à explorer, triée par le coût total f (g + h)
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(Node::getF));
        // Ensemble des nœuds déjà explorés
        Set<Dimension> closedSet = new HashSet<>();

        // Création du nœud de départ avec sa position, son parent, le coût g et la heuristique h
        Node startNode = new Node(startLoc, null, 0, heuristic(startLoc));
        openSet.add(startNode); // Ajout du nœud de départ à la liste des nœuds à explorer

        while (!openSet.isEmpty()) {
            // Récupération du nœud ayant le coût total le plus bas (f)
            Node currentNode = openSet.poll();
            currentLoc = currentNode.location; // Mise à jour de la position actuelle

            // Vérification si le nœud actuel est l'objectif
            if (equals(currentLoc, goalLoc)) {
                reconstructPath(currentNode); // Reconstruire le chemin de l'objectif au départ
                return; // Terminer la recherche
            }

            closedSet.add(currentLoc); // Ajouter le nœud actuel à l'ensemble des nœuds explorés
            // Itération sur les mouvements possibles à partir de la position actuelle
            for (Dimension move : getPossibleMoves(currentLoc)) {
                // Vérification que le mouvement est valide et non encore exploré
                if (move != null && !closedSet.contains(move)) {
                    // Calculer le coût g (coût du chemin depuis le départ)
                    int gCost = currentNode.g + 1;
                    // Création d'un nouveau nœud pour le voisin avec son coût g et sa heuristique h
                    Node neighborNode = new Node(move, currentNode, gCost, heuristic(move));

                    // Vérification si le voisin doit être ajouté à la liste d'exploration
                    if (!openSet.contains(neighborNode) || gCost < neighborNode.g) {
                        openSet.add(neighborNode); // Ajout du voisin à la liste d'exploration
                    }
                }
            }
        }
    }

    /**
     * La méthode heuristique évalue la distance estimée du nœud actuel à l'objectif.
     * Ici, nous utilisons la distance de Manhattan comme heuristique.
     * La distance de Manhattan est calculée comme suit :
     * |x1 - x2| + |y1 - y2|, où (x1, y1) et (x2, y2) sont les coordonnées du nœud actuel et de l'objectif.
     * Cette heuristique est admissible car elle ne surestime jamais le coût réel pour atteindre l'objectif.
     */
    private int heuristic(Dimension loc) {
        // Calcul de la distance de Manhattan
        return Math.abs(loc.width - goalLoc.width) + Math.abs(loc.height - goalLoc.height);
    }

    private void reconstructPath(Node node) {
        maxDepth = 0; // Réinitialiser la profondeur maximale
        // Reconstruire le chemin en suivant les parents des nœuds
        while (node != null) {
            searchPath[maxDepth++] = node.location; // Stocke la position du nœud dans le chemin
            node = node.parent; // Remonter au parent du nœud
        }
    }

    private static class Node {
        Dimension location; // Position du nœud
        Node parent; // Nœud parent pour la reconstruction du chemin
        int g; // Coût du chemin depuis le nœud de départ
        int h; // Coût heuristique (estimation du coût jusqu'à l'objectif)
        int f; // Coût total (f = g + h)

        Node(Dimension location, Node parent, int g, int h) {
            this.location = location; // Initialisation de la position du nœud
            this.parent = parent; // Initialisation du parent
            this.g = g; // Coût depuis le départ
            this.h = h; // Coût heuristique
            this.f = g + h; // Calcul du coût total
        }

        int getF() {
            return f; // Retourne le coût total
        }
    }
}
